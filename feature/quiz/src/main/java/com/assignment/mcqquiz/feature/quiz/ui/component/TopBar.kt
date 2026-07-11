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
 * A thin invisible top bar to support devices with camera cutouts.
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

