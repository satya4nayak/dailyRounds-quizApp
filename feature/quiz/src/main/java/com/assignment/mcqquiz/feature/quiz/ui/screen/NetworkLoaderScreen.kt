package com.assignment.mcqquiz.feature.quiz.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignment.mcqquiz.feature.quiz.ui.theme.Amber
import com.assignment.mcqquiz.feature.quiz.ui.theme.Background
import com.assignment.mcqquiz.feature.quiz.ui.theme.BorderColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.Primary
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme
import com.assignment.mcqquiz.feature.quiz.ui.theme.SurfaceColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextMuted
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextPrimary
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextSecondary

/**
 * Loading screen shown while questions are being fetched.
 */
@Composable
fun NetworkLoaderScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Logo box ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧠", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Title ─────────────────────────────────────────────
            Text(
                text = "QuizMaster",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Test your Android knowledge",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Info pills ────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NetworkLoaderPill(text = "10 Questions")
                NetworkLoaderPill(text = "~2 Minutes")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Animated loading dots ─────────────────────────────
            AnimatedLoadingDots()

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Loading questions…",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                letterSpacing = 0.04.sp
            )
        }
    }
}

@Composable
private fun NetworkLoaderPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(SurfaceColor)
            .border(1.dp, BorderColor, RoundedCornerShape(99.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
    }
}

@Composable
private fun AnimatedLoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "loadingDots")

    val dot1Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Scale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .scale(dot1Scale)
                .size(8.dp)
                .clip(CircleShape)
                .background(Primary)
        )
        Box(
            modifier = Modifier
                .scale(dot2Scale)
                .size(8.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.6f))
        )
        Box(
            modifier = Modifier
                .scale(dot3Scale)
                .size(8.dp)
                .clip(CircleShape)
                .background(Amber)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    QuizAppTheme {
        NetworkLoaderScreen()
    }
}
