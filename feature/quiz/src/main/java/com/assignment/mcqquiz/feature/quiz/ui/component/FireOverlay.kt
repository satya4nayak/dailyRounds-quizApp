package com.assignment.mcqquiz.feature.quiz.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

@Composable
fun FireOverlay(
    streakCount: Int,
    onDismiss: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1_500L)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
            exit = scaleOut(animationSpec = tween(200)) + fadeOut(animationSpec = tween(200))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset("streak_fire.json")
                )
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(240.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "You're on fire! 🔥",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$streakCount in a row!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFFF6D00)
                )
            }
        }
    }
}

