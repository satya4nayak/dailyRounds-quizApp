package com.assignment.mcqquiz.feature.quiz.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignment.mcqquiz.feature.quiz.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private val APPEAR_DELAY     = 250L.milliseconds
private val VISIBLE_DURATION = 3_000L.milliseconds

/**
 * Full-screen overlay celebrating a streak milestone (every 3 correct in a row).
 */
@Composable
fun StreakCelebrationOverlay(
    streakCount: Int,
    onDismiss: () -> Unit
) {
    var cardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(APPEAR_DELAY)
        cardVisible = true
        delay(VISIBLE_DURATION)
        onDismiss()
    }

    // Dark backdrop — appears immediately
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE00F121E))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Card animates in after the 1s delay
        AnimatedVisibility(
            visible = cardVisible,
            enter = fadeIn(animationSpec = tween(300)) +
                    scaleIn(initialScale = 0.85f, animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurfaceColor)
                    .border(1.dp, AmberBorder, RoundedCornerShape(28.dp))
                    .padding(horizontal = 28.dp, vertical = 32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // ── Animated fire emoji ───────────────────────
                    val fireTransition = rememberInfiniteTransition(label = "fireWobble")
                    val fireRotation by fireTransition.animateFloat(
                        initialValue = -6f,
                        targetValue  = 6f,
                        animationSpec = infiniteRepeatable(
                            animation  = tween(600, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "fireRotation"
                    )
                    val fireScale by fireTransition.animateFloat(
                        initialValue = 1f,
                        targetValue  = 1.12f,
                        animationSpec = infiniteRepeatable(
                            animation  = tween(600, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "fireScale"
                    )
                    Text(
                        text     = "🔥",
                        fontSize = 64.sp,
                        modifier = Modifier
                            .scale(fireScale)
                            .rotate(fireRotation)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "You're on fire!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Amber,
                        letterSpacing = (-0.3).sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Three correct answers in a row.\nKeep it going!",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ⚡ streak badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(AmberBg)
                            .border(1.dp, AmberBorder, RoundedCornerShape(99.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "⚡", fontSize = 15.sp)
                        Text(
                            text = "$streakCount in a row!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Amber
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StreakCelebrationOverlayPreview_3() {
    QuizAppTheme {
        StreakCelebrationOverlay(streakCount = 3, onDismiss = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StreakCelebrationOverlayPreview_6() {
    QuizAppTheme {
        StreakCelebrationOverlay(streakCount = 6, onDismiss = {})
    }
}
