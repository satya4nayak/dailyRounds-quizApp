package com.assignment.mcqquiz.feature.quiz.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.assignment.mcqquiz.feature.quiz.domain.state.QuizUiState
import com.assignment.mcqquiz.feature.quiz.ui.component.StreakCelebrationOverlay
import com.assignment.mcqquiz.feature.quiz.ui.component.OptionCard
import com.assignment.mcqquiz.feature.quiz.ui.component.QuizProgressBar
import com.assignment.mcqquiz.feature.quiz.ui.component.StreakBadge
import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.QuizViewModel
import com.assignment.mcqquiz.feature.quiz.ui.theme.Surface

/**
 * Main quiz screen displaying the current question, answer options,
 * progress indicator, streak badge, and skip button.
 *
 * Fully stateless — receives [QuizUiState] and emits [QuizViewModel.Event]s upward.
 */
@Composable
fun QuizScreen(
    state: QuizUiState,
    onEvent: (QuizViewModel.Event) -> Unit
) {
    if (state.questions.isEmpty()) return

    val currentQuestion = state.questions[state.currentIndex]

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            QuizProgressBar(
                currentIndex = state.currentIndex,
                totalQuestions = state.questions.size
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${state.currentIndex + 1} of ${state.questions.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
                StreakBadge(currentStreak = state.currentStreak)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = currentQuestion.text,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            currentQuestion.options.forEachIndexed { index, option ->
                OptionCard(
                    optionText = option,
                    optionIndex = index,
                    selectedOptionIndex = state.selectedOptionIndex,
                    correctOptionIndex = currentQuestion.correctOptionIndex,
                    isAnswerRevealed = state.isAnswerRevealed,
                    onClick = { onEvent(QuizViewModel.Event.OptionSelected(it)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = { onEvent(QuizViewModel.Event.SkipQuestion) },
                    enabled = !state.isAnswerRevealed
                ) {
                    Text(
                        text = "Skip Question",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.showStreakCelebration) {
            StreakCelebrationOverlay(
                streakCount = state.currentStreak,
                onDismiss = { onEvent(QuizViewModel.Event.StreakCelebrationDismissed) }
            )
        }
    }
}
