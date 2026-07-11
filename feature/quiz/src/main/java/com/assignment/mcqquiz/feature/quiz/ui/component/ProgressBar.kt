package com.assignment.mcqquiz.feature.quiz.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.assignment.mcqquiz.feature.quiz.ui.theme.Primary
import com.assignment.mcqquiz.feature.quiz.ui.theme.SurfaceHi
import androidx.compose.ui.tooling.preview.Preview
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme

@Composable
fun QuizProgressBar(
    currentIndex: Int,
    totalQuestions: Int
) {
    val progress by animateFloatAsState(
        targetValue = if (totalQuestions > 0) (currentIndex + 1).toFloat() / totalQuestions else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "progressAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(SurfaceHi),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(99.dp))
                .background(Primary)
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun QuizProgressBarPreview_Start() {
    QuizAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            QuizProgressBar(currentIndex = 0, totalQuestions = 10)
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun QuizProgressBarPreview_Mid() {
    QuizAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            QuizProgressBar(currentIndex = 4, totalQuestions = 10)
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun QuizProgressBarPreview_End() {
    QuizAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            QuizProgressBar(currentIndex = 9, totalQuestions = 10)
        }
    }
}
