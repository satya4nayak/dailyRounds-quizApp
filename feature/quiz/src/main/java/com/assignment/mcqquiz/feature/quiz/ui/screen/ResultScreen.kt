package com.assignment.mcqquiz.feature.quiz.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignment.mcqquiz.feature.quiz.domain.state.QuizUiState
import com.assignment.mcqquiz.feature.quiz.ui.component.ScreenTopBar
import com.assignment.mcqquiz.feature.quiz.ui.theme.Amber
import com.assignment.mcqquiz.feature.quiz.ui.theme.AmberBg
import com.assignment.mcqquiz.feature.quiz.ui.theme.Background
import com.assignment.mcqquiz.feature.quiz.ui.theme.BorderColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.CorrectBg
import com.assignment.mcqquiz.feature.quiz.ui.theme.CorrectGreen
import com.assignment.mcqquiz.feature.quiz.ui.theme.Primary
import com.assignment.mcqquiz.feature.quiz.ui.theme.SurfaceColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.SurfaceHi
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextPrimary
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme

/**
 * Results screen displayed after the last question is answered.
 *
 * Shows animated score counter, a stat summary card (correct/skipped/streak),
 * a performance badge, and a restart button. Fully stateless — events flow up via [onEvent].
 */
@Composable
fun ResultScreen(
    state: QuizUiState,
    onRestart: () -> Unit
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // ── Top bar ───────────────────────────────────────────────
        ScreenTopBar()

        // ── Scrollable content ─────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ── Hero ──────────────────────────────────────────────────
        Text(text = "🎉", fontSize = 36.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Quiz Complete!",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            letterSpacing = (-0.3).sp
        )
        Text(
            text = "Here's how you did",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Score ring ────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = Primary,
                trackColor = SurfaceHi,
                strokeWidth = 10.dp,
                strokeCap = StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$displayedCount",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    letterSpacing = (-1).sp,
                    lineHeight = 40.sp
                )
                Text(
                    text = "/ $total",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Stats card ────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceColor)
                .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
        ) {
            ResultStatRow(
                icon = "✅",
                iconBgColor = CorrectBg,
                label = "Correct Answers",
                value = "${state.correctCount} / $total",
                valueColor = CorrectGreen
            )
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
            ResultStatRow(
                icon = "⏭",
                iconBgColor = SurfaceHi,
                label = "Skipped",
                value = "${state.skippedCount}",
                valueColor = TextSecondary
            )
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
            ResultStatRow(
                icon = "🔥",
                iconBgColor = AmberBg,
                label = "Longest Streak",
                value = "${state.longestStreak}",
                valueColor = Amber
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Performance badge ─────────────────────────────────────
        val (badgeEmoji, badgeTitle, badgeSub) = when {
            state.correctCount >= (total * 0.8).toInt() ->
                Triple("🏆", "Excellent!", "Outstanding score — you're a quiz master!")
            state.correctCount >= (total * 0.5).toInt() ->
                Triple("🥈", "Good effort!", "Score ${(total * 0.8).toInt()}+ to earn the 🏆 Gold badge")
            else ->
                Triple("🥉", "Keep practicing!", "You'll nail it next time!")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceColor)
                .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = badgeEmoji, fontSize = 28.sp)
            Column {
                Text(
                    text = badgeTitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = badgeSub,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Restart button ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Primary)
                .clickable { onRestart() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "↩  Restart Quiz",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.02.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        } // end scrollable Column
    } // end outer Column
}

@Composable
private fun ResultStatRow(
    icon: String,
    iconBgColor: Color,
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 18.sp)
        }
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val resultPreviewQuestions = (1..10).map { i ->
    Question(
        id = i,
        question = "Sample question $i",
        options = listOf("Option A", "Option B", "Option C", "Option D"),
        correctOptionIndex = 0
    )
}

/** Good score — 7/10, silver badge */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ResultScreenPreview_GoodScore() {
    QuizAppTheme {
        ResultScreen(
            state = QuizUiState(questions = resultPreviewQuestions, correctCount = 7, skippedCount = 1, longestStreak = 4),
            onRestart = {}
        )
    }
}

/** Perfect score — 10/10, gold badge */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ResultScreenPreview_PerfectScore() {
    QuizAppTheme {
        ResultScreen(
            state = QuizUiState(questions = resultPreviewQuestions, correctCount = 10, skippedCount = 0, longestStreak = 10),
            onRestart = {}
        )
    }
}

/** Low score — 3/10, bronze badge */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ResultScreenPreview_LowScore() {
    QuizAppTheme {
        ResultScreen(
            state = QuizUiState(questions = resultPreviewQuestions, correctCount = 3, skippedCount = 4, longestStreak = 2),
            onRestart = {}
        )
    }
}
