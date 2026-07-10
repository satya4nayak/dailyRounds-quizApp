package com.assignment.mcqquiz.feature.quiz.ui.screen

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignment.mcqquiz.feature.quiz.domain.state.QuizUiState
import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.QuizViewModel
import com.assignment.mcqquiz.feature.quiz.ui.theme.CorrectGreen
import com.assignment.mcqquiz.feature.quiz.ui.theme.Primary
import com.assignment.mcqquiz.feature.quiz.ui.theme.Surface
import kotlinx.coroutines.delay

/**
 * Results screen displayed after the last question is answered.
 *
 * Shows animated score counter, a stat summary card (correct/skipped/streak),
 * and a restart button. Fully stateless — events flow up via [onEvent].
 */
@Composable
fun ResultScreen(
    state: QuizUiState,
    onEvent: (QuizViewModel.Event) -> Unit
) {
    val total = state.questions.size
    var displayedCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.correctCount) {
        displayedCount = 0
        repeat(state.correctCount) {
            delay(50L)
            displayedCount++
        }
    }

    val progress = if (total > 0) state.correctCount.toFloat() / total else 0f
    val isGreatScore = state.correctCount >= total / 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isGreatScore) "Congratulations! 🎉" else "Quiz Complete!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Animated score ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = CorrectGreen,
                trackColor = Surface,
                strokeWidth = 12.dp
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$displayedCount",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = CorrectGreen
                )
                Text(text = "/ $total", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ResultStatRow(label = "✅ Correct Answers", value = "${state.correctCount} / $total")
                Spacer(modifier = Modifier.height(12.dp))
                ResultStatRow(label = "⏭️ Skipped", value = "${state.skippedCount}")
                Spacer(modifier = Modifier.height(12.dp))
                ResultStatRow(label = "🔥 Longest Streak", value = "${state.longestStreak}")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { onEvent(QuizViewModel.Event.RestartQuiz) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(
                text = "Restart Quiz",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ResultStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
