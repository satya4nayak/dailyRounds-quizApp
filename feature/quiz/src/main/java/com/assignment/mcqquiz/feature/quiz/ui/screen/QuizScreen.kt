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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.feature.quiz.domain.state.QuizUiState
import com.assignment.mcqquiz.feature.quiz.ui.component.OptionCard
import com.assignment.mcqquiz.feature.quiz.ui.component.QuizProgressBar
import com.assignment.mcqquiz.feature.quiz.ui.component.ScreenTopBar
import com.assignment.mcqquiz.feature.quiz.ui.component.StreakBadge
import com.assignment.mcqquiz.feature.quiz.ui.component.StreakCelebrationOverlay
import com.assignment.mcqquiz.feature.quiz.ui.theme.Background
import com.assignment.mcqquiz.feature.quiz.ui.theme.BorderColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.CorrectGreen
import com.assignment.mcqquiz.feature.quiz.ui.theme.Primary
import com.assignment.mcqquiz.feature.quiz.ui.theme.SurfaceColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.SurfaceHi
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextPrimary
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextSecondary
import com.assignment.mcqquiz.feature.quiz.ui.theme.WrongRed
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme

/**
 * Main quiz screen displaying the current question, answer options,
 * progress indicator, streak badge, and skip button.
 *
 * Fully stateless — receives [QuizUiState] and emits [QuizViewModel.Event]s upward.
 */
@Composable
fun QuizScreen(
    state: QuizUiState,
    onOptionSelected: (Int) -> Unit,
    onSkip: () -> Unit
) {
    if (state.questions.isEmpty()) return

    val currentQuestion = state.questions[state.currentQuestionIndex]
    val total = state.questions.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar ───────────────────────────────────────────
            ScreenTopBar()

            // ── Scrollable content ────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

            // ── Header row: counter + streak badge ────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = "${state.currentQuestionIndex + 1}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = " of $total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                StreakBadge(currentStreak = state.currentStreak)
            }

            Spacer(modifier = Modifier.height(12.dp))


            // ── Progress bar ──────────────────────────────────────
            QuizProgressBar(
                currentIndex = state.currentQuestionIndex,
                totalQuestions = total
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Question card ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceColor)
                    .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            ) {
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 25.dp, bottom = 22.dp)
                ) {
                    Text(
                        text = "QUESTION ${state.currentQuestionIndex + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        letterSpacing = 0.1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentQuestion.question,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 26.sp,
                        letterSpacing = (-0.2).sp
                    )
                }
                // Top accent bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(Primary)
                        .align(Alignment.TopStart)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Options ───────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                currentQuestion.options.forEachIndexed { index, option ->
                    OptionCard(
                        optionText = option,
                        optionIndex = index,
                        selectedOptionIndex = state.selectedOptionIndex,
                        correctOptionIndex = currentQuestion.correctOptionIndex,
                        isAnswerRevealed = state.isAnswerRevealed,
                        onClick = onOptionSelected
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Answer feedback banner ────────────────────────────
            if (state.isAnswerRevealed) {
                val isCorrectAnswer = state.selectedOptionIndex == currentQuestion.correctOptionIndex
                val skipped = state.selectedOptionIndex == null
                val bannerBg = when {
                    skipped -> SurfaceHi
                    isCorrectAnswer -> Color(0x1A1DAA60)
                    else -> Color(0x1AE84545)
                }
                val bannerBorder = when {
                    skipped -> BorderColor
                    isCorrectAnswer -> Color(0x661DAA60)
                    else -> Color(0x66E84545)
                }
                val bannerIcon  = when { skipped -> "⏭️"; isCorrectAnswer -> "✅"; else -> "❌" }
                val bannerTitle = when { skipped -> "Skipped"; isCorrectAnswer -> "Correct!"; else -> "Not quite!" }
                val bannerSub   = when {
                    skipped -> "Moving to next question…"
                    isCorrectAnswer -> "Moving to next question…"
                    else -> "The correct answer is highlighted above"
                }
                val bannerTitleColor = when {
                    skipped -> TextSecondary
                    isCorrectAnswer -> CorrectGreen
                    else -> WrongRed
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(bannerBg)
                        .border(1.dp, bannerBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = bannerIcon, fontSize = 18.sp)
                    Column {
                        Text(
                            text = bannerTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = bannerTitleColor
                        )
                        Text(
                            text = bannerSub,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Skip button ───────────────────────────────────────
            val skipAlpha = if (state.isAnswerRevealed) 0.4f else 1f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceHi.copy(alpha = skipAlpha))
                    .border(1.5.dp, TextSecondary.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .clickable(enabled = !state.isAnswerRevealed) { onSkip() }
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "↪  Skip Question",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary.copy(alpha = skipAlpha),
                    letterSpacing = 0.02.sp
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
            } // end scrollable Column
        } // end outer Column

        // ── Streak celebration overlay ─────────────────────────
        if (state.showStreakCelebration) {
            StreakCelebrationOverlay(
                streakCount = state.currentStreak,
                onDismiss = {}
            )
        }
    }
}

/**
 * Preview Section
 */

// ── Preview helpers ────────────────────────────────────────────────────────────

private val previewQuestions = listOf(
    Question(
        id = 1,
        question = "What hidden feature do recent Android versions reveal when you tap the version number multiple times in Settings?",
        options = listOf("Flappy Bird-style game", "Virtual pet", "Hidden performance menu", "System UI tuner"),
        correctOptionIndex = 0
    ),
    Question(
        id = 2,
        question = "If you were to implement 'shake to undo' in your Android app, what's the biggest technical challenge you'd face?",
        options = listOf("Detecting accidental shakes", "Battery drain due to sensors", "Android doesn't allow motion APIs", "Undo logic is illegal on Android"),
        correctOptionIndex = 0
    ),
    Question(
        id = 3,
        question = "Which Android system permission is needed to draw a floating overlay on top of other apps?",
        options = listOf("SYSTEM_ALERT_WINDOW", "ACCESS_OVERLAY_UI", "DRAW_OVER_APPS", "FOREGROUND_SERVICE"),
        correctOptionIndex = 0
    )
)

/** Default: question 1, nothing selected */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizScreenPreview_Default() {
    QuizAppTheme {
        QuizScreen(
            state = QuizUiState(questions = previewQuestions, currentQuestionIndex = 0, currentStreak = 0),
            onOptionSelected = {},
            onSkip = {}
        )
    }
}

/** Correct answer selected on question 1 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizScreenPreview_CorrectAnswer() {
    QuizAppTheme {
        QuizScreen(
            state = QuizUiState(questions = previewQuestions, currentQuestionIndex = 0, selectedOptionIndex = 0, isAnswerRevealed = true, currentStreak = 1),
            onOptionSelected = {},
            onSkip = {}
        )
    }
}

/** Wrong answer selected on question 2, streak reset */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizScreenPreview_WrongAnswer() {
    QuizAppTheme {
        QuizScreen(
            state = QuizUiState(questions = previewQuestions, currentQuestionIndex = 1, selectedOptionIndex = 2, isAnswerRevealed = true, currentStreak = 0),
            onOptionSelected = {},
            onSkip = {}
        )
    }
}

/** Mid-quiz with active streak badge */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizScreenPreview_ActiveStreak() {
    QuizAppTheme {
        QuizScreen(
            state = QuizUiState(questions = previewQuestions, currentQuestionIndex = 2, currentStreak = 3),
            onOptionSelected = {},
            onSkip = {}
        )
    }
}

