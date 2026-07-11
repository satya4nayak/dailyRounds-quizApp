package com.assignment.mcqquiz.feature.quiz.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignment.mcqquiz.feature.quiz.ui.theme.Amber
import com.assignment.mcqquiz.feature.quiz.ui.theme.AmberBg
import com.assignment.mcqquiz.feature.quiz.ui.theme.AmberBorder
import com.assignment.mcqquiz.feature.quiz.ui.theme.BorderColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.SurfaceHi
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextSecondary
import androidx.compose.ui.tooling.preview.Preview
import com.assignment.mcqquiz.feature.quiz.ui.theme.Background
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme

@Composable
fun StreakBadge(currentStreak: Int) {
    val isActive = currentStreak >= 1

    val infiniteTransition = rememberInfiniteTransition(label = "streakPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.06f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badgeScale"
    )

    Row(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(99.dp))
            .background(if (isActive) AmberBg else SurfaceHi)
            .border(
                width = 1.dp,
                color = if (isActive) AmberBorder else BorderColor,
                shape = RoundedCornerShape(99.dp)
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = "🔥", fontSize = 14.sp)
        Text(
            text = "$currentStreak",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Amber else TextSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakBadgePreview_Idle() {
    QuizAppTheme {
        Column(modifier = Modifier.background(Background).padding(16.dp)) {
            StreakBadge(currentStreak = 0)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakBadgePreview_Active() {
    QuizAppTheme {
        Column(modifier = Modifier.background(Background).padding(16.dp)) {
            StreakBadge(currentStreak = 3)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakBadgePreview_HighStreak() {
    QuizAppTheme {
        Column(modifier = Modifier.background(Background).padding(16.dp)) {
            StreakBadge(currentStreak = 7)
        }
    }
}
