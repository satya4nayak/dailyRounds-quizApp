package com.assignment.mcqquiz.feature.quiz.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.assignment.mcqquiz.feature.quiz.di.QuizFeatureDependencies
import com.assignment.mcqquiz.feature.quiz.ui.factory.QuizViewModelFactory
import com.assignment.mcqquiz.feature.quiz.ui.screen.NetworkLoaderScreen
import com.assignment.mcqquiz.feature.quiz.ui.screen.QuizErrorBanner
import com.assignment.mcqquiz.feature.quiz.ui.screen.QuizScreen
import com.assignment.mcqquiz.feature.quiz.ui.screen.ResultScreen
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme
import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.QuizViewModel

/**
 * Hosts the full quiz session: loading → questions → results.
 * Receives [EXTRA_CATEGORY_ID] and [EXTRA_QUESTION_URL] via Intent extras.
 * Calls [finish] on both [QuizViewModel.Effect.FinishModule] and [QuizViewModel.Effect.NavigateBack];
 * [CategorySelectionActivity] refreshes its list when this Activity returns.
 */
class QuizActivity : ComponentActivity() {

    private lateinit var quizVm: QuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryId  = requireNotNull(intent.getStringExtra(EXTRA_CATEGORY_ID))  { "Missing $EXTRA_CATEGORY_ID" }
        val questionUrl = requireNotNull(intent.getStringExtra(EXTRA_QUESTION_URL)) { "Missing $EXTRA_QUESTION_URL" }

        val deps = application as QuizFeatureDependencies
        quizVm = ViewModelProvider(
            this,
            QuizViewModelFactory(
                owner           = this,
                quizService     = deps.quizService(),
                categoryService = deps.categoryService(),
                categoryId      = categoryId,
                questionUrl     = questionUrl
            )
        )[QuizViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            val quizState  by quizVm.uiState.collectAsStateWithLifecycle()
            val quizEffect by quizVm.effects.collectAsStateWithLifecycle(
                initialValue = quizVm.effects.replayCache.firstOrNull()
                    ?: QuizViewModel.Effect.ShowLoader
            )

            LaunchedEffect(Unit) {
                quizVm.handleEvent(QuizViewModel.Event.InitialLoad)
                quizVm.effects.collect { effect ->
                    when (effect) {
                        QuizViewModel.Effect.FinishModule,
                        QuizViewModel.Effect.NavigateBack -> finish()
                        else -> Unit
                    }
                }
            }

            BackHandler {
                when (quizEffect) {
                    QuizViewModel.Effect.NavigateToResults ->
                        quizVm.handleEvent(QuizViewModel.Event.FinishModule)
                    else ->
                        quizVm.handleEvent(QuizViewModel.Event.NavigateBack)
                }
            }

            QuizAppTheme {
                when (quizEffect) {
                    QuizViewModel.Effect.ShowLoader        -> NetworkLoaderScreen()
                    QuizViewModel.Effect.NavigateToQuiz    -> QuizScreen(
                        state            = quizState,
                        onOptionSelected = { idx -> quizVm.handleEvent(QuizViewModel.Event.OptionSelected(idx)) },
                        onSkip           = { quizVm.handleEvent(QuizViewModel.Event.SkipQuestion) },
                        onBack           = { quizVm.handleEvent(QuizViewModel.Event.NavigateBack) }
                    )
                    QuizViewModel.Effect.NavigateToResults -> ResultScreen(
                        state    = quizState,
                        onFinish = { quizVm.handleEvent(QuizViewModel.Event.FinishModule) }
                    )
                    QuizViewModel.Effect.ShowError         -> QuizErrorBanner(
                        onRetry = { quizVm.handleEvent(QuizViewModel.Event.RetryApiCall) },
                        onBack  = { quizVm.handleEvent(QuizViewModel.Event.NavigateBack) }
                    )
                    QuizViewModel.Effect.FinishModule,
                    QuizViewModel.Effect.NavigateBack      -> { /* handled in LaunchedEffect */ }
                }
            }
        }
    }

    companion object {
        const val EXTRA_CATEGORY_ID  = QuizViewModel.KEY_CATEGORY_ID
        const val EXTRA_QUESTION_URL = QuizViewModel.KEY_QUESTION_URL

        fun createIntent(context: Context, categoryId: String, questionUrl: String): Intent =
            Intent(context, QuizActivity::class.java).apply {
                putExtra(EXTRA_CATEGORY_ID, categoryId)
                putExtra(EXTRA_QUESTION_URL, questionUrl)
            }
    }
}


