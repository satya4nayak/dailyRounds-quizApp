package com.assignment.mcqquiz.feature.quiz.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignment.mcqquiz.feature.quiz.ui.theme.BorderColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.CorrectBg
import com.assignment.mcqquiz.feature.quiz.ui.theme.CorrectBorder
import com.assignment.mcqquiz.feature.quiz.ui.theme.CorrectGreen
import com.assignment.mcqquiz.feature.quiz.ui.theme.SurfaceColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.SurfaceHi
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextPrimary
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextSecondary
import com.assignment.mcqquiz.feature.quiz.ui.theme.WrongBg
import com.assignment.mcqquiz.feature.quiz.ui.theme.WrongBorder
import com.assignment.mcqquiz.feature.quiz.ui.theme.WrongRed
import androidx.compose.ui.tooling.preview.Preview
import com.assignment.mcqquiz.feature.quiz.ui.theme.Background
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme

@Composable
fun OptionCard(
    optionText: String,
    optionIndex: Int,
    selectedOptionIndex: Int?,
    correctOptionIndex: Int,
    isAnswerRevealed: Boolean,
    onClick: (Int) -> Unit
) {
    val isSelected = selectedOptionIndex == optionIndex
    val isCorrect  = optionIndex == correctOptionIndex
    val isOtherWrong = isAnswerRevealed && !isSelected && !isCorrect

    // ── Background colour ──────────────────────────────────────────────────
    val bgColor by animateColorAsState(
        targetValue = when {
            !isAnswerRevealed                  -> SurfaceColor
            isSelected && isCorrect            -> CorrectBg
            isSelected && !isCorrect           -> WrongBg
            !isSelected && isCorrect           -> CorrectBg
            else                               -> SurfaceColor
        },
        animationSpec = tween(300), label = "optionBg"
    )

    // ── Border colour ──────────────────────────────────────────────────────
    val borderColor by animateColorAsState(
        targetValue = when {
            !isAnswerRevealed                  -> BorderColor
            isSelected && isCorrect            -> CorrectBorder
            isSelected && !isCorrect           -> WrongBorder
            !isSelected && isCorrect           -> CorrectBorder
            else                               -> BorderColor
        },
        animationSpec = tween(300), label = "optionBorder"
    )

    // ── Left accent bar colour ─────────────────────────────────────────────
    val accentBarColor by animateColorAsState(
        targetValue = when {
            !isAnswerRevealed                  -> Color.Transparent
            isSelected && isCorrect            -> CorrectGreen
            isSelected && !isCorrect           -> WrongRed
            !isSelected && isCorrect           -> CorrectGreen
            else                               -> Color.Transparent
        },
        animationSpec = tween(300), label = "accentBar"
    )

    // ── Letter badge colours ───────────────────────────────────────────────
    val letterBgColor by animateColorAsState(
        targetValue = when {
            isAnswerRevealed && isCorrect      -> CorrectGreen
            isAnswerRevealed && isSelected     -> WrongRed
            else                               -> SurfaceHi
        },
        animationSpec = tween(300), label = "letterBg"
    )
    val letterTextColor by animateColorAsState(
        targetValue = when {
            isAnswerRevealed && (isCorrect || isSelected) -> Color.White
            else                                           -> TextSecondary
        },
        animationSpec = tween(300), label = "letterText"
    )

    // ── Option text colour ─────────────────────────────────────────────────
    val optionTextColor by animateColorAsState(
        targetValue = when {
            !isAnswerRevealed                  -> TextPrimary
            isSelected && isCorrect            -> CorrectGreen
            isSelected && !isCorrect           -> WrongRed
            !isSelected && isCorrect           -> CorrectGreen
            else                               -> TextPrimary
        },
        animationSpec = tween(300), label = "optionText"
    )

    val contentAlpha = if (isOtherWrong) 0.45f else 1f
    val optionLetter = ('A' + optionIndex).toString()
    val showIcon     = isAnswerRevealed && (isSelected || isCorrect)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(contentAlpha)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = !isAnswerRevealed) { onClick(optionIndex) }
    ) {
        // ── Left accent bar ──────────────────────────────────────
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(64.dp)
                .align(Alignment.CenterStart)
                .background(accentBarColor)
        )

        // ── Content row ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Letter badge
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(letterBgColor)
                    .border(1.dp, if (isAnswerRevealed && (isCorrect || isSelected)) Color.Transparent else BorderColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionLetter,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = letterTextColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Option text
            Text(
                text = optionText,
                fontSize = 14.sp,
                fontWeight = if (isAnswerRevealed && (isSelected || (!isSelected && isCorrect))) FontWeight.SemiBold else FontWeight.Medium,
                color = optionTextColor,
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f)
            )

            // Status icon
            if (showIcon) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (isCorrect) CorrectGreen else WrongRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (isCorrect) "Correct" else "Wrong",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

/** Default unanswered state */
@Preview(showBackground = true, widthDp = 360)
@Composable
private fun OptionCardPreview_Default() {
    QuizAppTheme {
        Column(
            modifier = Modifier
                .background(Background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OptionCard("Flappy Bird-style game", 0, null, 0, false) {}
            OptionCard("Virtual pet", 1, null, 0, false) {}
            OptionCard("Hidden performance menu", 2, null, 0, false) {}
            OptionCard("System UI tuner", 3, null, 0, false) {}
        }
    }
}

/** Correct answer selected */
@Preview(showBackground = true, widthDp = 360)
@Composable
private fun OptionCardPreview_Correct() {
    QuizAppTheme {
        Column(
            modifier = Modifier
                .background(Background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OptionCard("Flappy Bird-style game", 0, 0, 0, true) {}
            OptionCard("Virtual pet", 1, 0, 0, true) {}
            OptionCard("Hidden performance menu", 2, 0, 0, true) {}
            OptionCard("System UI tuner", 3, 0, 0, true) {}
        }
    }
}

/** Wrong answer selected — option B selected, correct is option A */
@Preview(showBackground = true, widthDp = 360)
@Composable
private fun OptionCardPreview_Wrong() {
    QuizAppTheme {
        Column(
            modifier = Modifier
                .background(Background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OptionCard("Flappy Bird-style game", 0, 2, 0, true) {}
            OptionCard("Virtual pet", 1, 2, 0, true) {}
            OptionCard("Hidden performance menu", 2, 2, 0, true) {}
            OptionCard("System UI tuner", 3, 2, 0, true) {}
        }
    }
}
