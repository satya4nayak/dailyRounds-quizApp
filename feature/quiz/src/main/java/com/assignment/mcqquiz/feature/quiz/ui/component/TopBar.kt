package com.assignment.mcqquiz.feature.quiz.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.assignment.mcqquiz.feature.quiz.ui.theme.Background
import com.assignment.mcqquiz.feature.quiz.ui.theme.BorderColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme

/**
 * A thin 20dp surface-coloured bar pinned at the top of a screen,
 * separated from the content below by a 1dp [BorderColor] divider.
 *
 * Used in [QuizScreen] and [ResultScreen] (not on SplashScreen).
 */
@Composable
fun ScreenTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Background)
    )
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ScreenTopBarPreview() {
    QuizAppTheme {
        ScreenTopBar()
    }
}

