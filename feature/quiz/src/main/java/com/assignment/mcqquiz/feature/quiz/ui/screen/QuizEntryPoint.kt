package com.assignment.mcqquiz.feature.quiz.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.assignment.mcqquiz.feature.quiz.domain.contract.QuizAppContract
import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.QuizViewModel

/**
 * Root entry-point Composable for the Quiz feature.
 *
 * Responsibilities:
 *  - Collects [QuizUiState] and drives screen transitions via [QuizAppContract].
 *  - Collects [QuizViewModel.Effect]s and reacts (e.g. shows a Snackbar on error).
 *  - Forwards all user interactions down to child screens as [QuizViewModel.Event] lambdas.
 *
 * This is the only Composable that holds a reference to the ViewModel.
 * All child screens are stateless and receive only what they need.
 */
@Composable
fun QuizEntryPoint(viewModel: QuizViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-off effects
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is QuizViewModel.Effect.ShowQuestionLoadError ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState)

    AnimatedContent(
        targetState = state.screen,
        transitionSpec = {
            (fadeIn() + slideInVertically { it / 4 }) togetherWith
                    (fadeOut() + slideOutVertically { -it / 4 })
        },
        label = "quizScreenTransition"
    ) { screen ->
        when (screen) {
            QuizAppContract.Splash -> SplashScreen()

            QuizAppContract.Quiz -> QuizScreen(
                state = state,
                onEvent = viewModel::handleEvent
            )

            QuizAppContract.Results -> ResultScreen(
                state = state,
                onEvent = viewModel::handleEvent
            )
        }
    }
}

