package com.assignment.mcqquiz.feature.quiz.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuizProgressBar(
    currentIndex: Int,
    totalQuestions: Int
) {
    val progress by animateFloatAsState(
        targetValue = if (totalQuestions > 0) (currentIndex + 1).toFloat() / totalQuestions else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "progressAnimation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

