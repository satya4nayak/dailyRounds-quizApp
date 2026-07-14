package com.assignment.mcqquiz.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.assignment.mcqquiz.QuizApplication
import com.assignment.mcqquiz.feature.quiz.domain.state.QuizUiState
import com.assignment.mcqquiz.feature.quiz.ui.screen.NetworkLoaderScreen
import com.assignment.mcqquiz.feature.quiz.ui.screen.QuizErrorBanner
import com.assignment.mcqquiz.feature.quiz.ui.screen.QuizScreen
import com.assignment.mcqquiz.feature.quiz.ui.screen.ResultScreen
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme
import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.QuizViewModel
import javax.inject.Inject

/**
 * Single Activity — the only place that holds routing logic.
 */
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: QuizViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as QuizApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val viewModel: QuizViewModel = viewModel(factory = viewModelFactory)
            val state: QuizUiState by viewModel.uiState.collectAsStateWithLifecycle()
            val currentEffect by viewModel.effects.collectAsStateWithLifecycle(
                initialValue = viewModel.effects.replayCache.firstOrNull()
                    ?: QuizViewModel.Effect.ShowLoader
            )

            LaunchedEffect(Unit) {
                viewModel.handleEvent(QuizViewModel.Event.InitialLoad)
                viewModel.effects.collect { effect ->
                    if (effect == QuizViewModel.Effect.RestartGame) {
                        startActivity(
                            Intent(this@MainActivity, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            }
                        )
                    }
                }
            }

            QuizAppTheme {
                when (currentEffect) {
                    QuizViewModel.Effect.ShowLoader        -> NetworkLoaderScreen()
                    QuizViewModel.Effect.NavigateToQuiz    -> QuizScreen(
                        state = state,
                        onOptionSelected = { idx -> viewModel.handleEvent(QuizViewModel.Event.OptionSelected(idx)) },
                        onSkip = { viewModel.handleEvent(QuizViewModel.Event.SkipQuestion) }
                    )
                    QuizViewModel.Effect.NavigateToResults -> ResultScreen(
                        state = state,
                        onRestart = { viewModel.handleEvent(QuizViewModel.Event.RestartQuiz) }
                    )
                    QuizViewModel.Effect.ShowError         -> QuizErrorBanner(
                        onRetry = { viewModel.handleEvent(QuizViewModel.Event.RetryApiCall) }
                    )
                    QuizViewModel.Effect.RestartGame       -> { /* never reaches here; handled above */ }
                }
            }
        }
    }
}
