package com.assignment.mcqquiz.feature.quiz.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import com.assignment.mcqquiz.feature.quiz.domain.contract.QuizAppContract
import com.assignment.mcqquiz.feature.quiz.domain.state.QuizUiState

/**
 * Root entry-point Composable for the Quiz feature.

 */
@Composable
fun QuizEntryPoint(
    state: QuizUiState,
    snackbarHostState: SnackbarHostState,
    onOptionSelected: (Int) -> Unit,
    onSkip: () -> Unit,
    onRestart: () -> Unit
) {
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
            QuizAppContract.Splash -> NetworkLoaderScreen()

            QuizAppContract.Quiz -> QuizScreen(
                state = state,
                onOptionSelected = onOptionSelected,
                onSkip = onSkip
            )

            QuizAppContract.Results -> ResultScreen(
                state = state,
                onRestart = onRestart
            )
        }
    }
}
