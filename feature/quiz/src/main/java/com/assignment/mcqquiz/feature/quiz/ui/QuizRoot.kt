package com.assignment.mcqquiz.feature.quiz.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.assignment.mcqquiz.feature.quiz.state.AppScreen
import com.assignment.mcqquiz.feature.quiz.ui.screen.QuizScreen
import com.assignment.mcqquiz.feature.quiz.ui.screen.ResultScreen
import com.assignment.mcqquiz.feature.quiz.ui.screen.SplashScreen
import com.assignment.mcqquiz.feature.quiz.viewmodel.QuizViewModel

@Composable
fun QuizRoot(viewModel: QuizViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = state.screen,
        transitionSpec = {
            (fadeIn() + slideInVertically { it / 4 }) togetherWith
                    (fadeOut() + slideOutVertically { -it / 4 })
        },
        label = "screenTransition"
    ) { screen ->
        when (screen) {
            AppScreen.Splash -> SplashScreen()
            AppScreen.Quiz -> QuizScreen(
                state = state,
                onOptionSelected = viewModel::onOptionSelected,
                onSkip = viewModel::onSkip,
                onStreakCelebrationDismissed = viewModel::onStreakCelebrationDismissed
            )
            AppScreen.Results -> ResultScreen(
                state = state,
                onRestart = viewModel::onRestart
            )
        }
    }
}

