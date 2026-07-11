package com.assignment.mcqquiz.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.assignment.mcqquiz.QuizApplication
import com.assignment.mcqquiz.feature.quiz.domain.state.QuizUiState
import com.assignment.mcqquiz.feature.quiz.ui.screen.QuizEntryPoint
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme
import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.QuizViewModel
import javax.inject.Inject

/**
 * The single Activity of the application — acts as the **orchestrator**.
 *
 * Responsibilities:
 *  - Receives Dagger injection from [AppComponent]; [QuizViewModelFactory] is
 *    injected directly — no manual construction required.
 *  - Collects [QuizUiState] with [collectAsStateWithLifecycle] — single source of truth for UI.
 *  - Collects one-off [QuizViewModel.Effect]s and handles them (e.g. showing a Snackbar).
 *  - Passes the resolved state and event callbacks down to [QuizEntryPoint].
 *    No ViewModel reference is ever forwarded to any Composable.
 */
class MainActivity : ComponentActivity() {

    // Provided by ViewModelModule and injected on launch via appComponent.inject(this).
    @Inject
    lateinit var viewModelFactory: QuizViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as QuizApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val viewModel : QuizViewModel = viewModel(factory = viewModelFactory)

            // ── Collect state ──────────────────────────────────────────────
            val state: QuizUiState by viewModel.uiState.collectAsStateWithLifecycle()

            // ── Collect one-off effects ────────────────────────────────────
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is QuizViewModel.Effect.ShowQuestionLoadError ->
                            snackbarHostState.showSnackbar(effect.message)

                        // Finish the current Activity and start a fresh one so the
                        QuizViewModel.Effect.RestartApp -> {
                            val intent = Intent(this@MainActivity, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            }
                            startActivity(intent)
                        }
                    }
                }
            }

            // ── Render ─────────────────────────────────────────────────────────────────
            QuizAppTheme {
                QuizEntryPoint(
                    state = state,
                    snackbarHostState = snackbarHostState,
                    onOptionSelected = { index -> viewModel.handleEvent(QuizViewModel.Event.OptionSelected(index)) },
                    onSkip            = { viewModel.handleEvent(QuizViewModel.Event.SkipQuestion) },
                    onRestart         = { viewModel.handleEvent(QuizViewModel.Event.RestartQuiz) }
                )
            }
        }
    }
}
