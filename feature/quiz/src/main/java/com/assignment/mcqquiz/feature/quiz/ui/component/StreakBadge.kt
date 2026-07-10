package com.assignment.mcqquiz.feature.quiz.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.assignment.mcqquiz.feature.quiz.ui.theme.StreakActive
import com.assignment.mcqquiz.feature.quiz.ui.theme.StreakInactive

@Composable
fun StreakBadge(currentStreak: Int) {
    val isActive = currentStreak >= 3

    val infiniteTransition = rememberInfiniteTransition(label = "streakPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badgeScale"
    )

    val badgeColor = if (isActive) StreakActive else StreakInactive

    Box(
        modifier = Modifier
            .scale(scale)
            .then(
                if (isActive) Modifier.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(50),
                    ambientColor = StreakActive,
                    spotColor = StreakActive
                ) else Modifier
            )
            .background(color = badgeColor, shape = RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "🔥",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$currentStreak",
                style = MaterialTheme.typography.bodyLarge,
                color = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}

