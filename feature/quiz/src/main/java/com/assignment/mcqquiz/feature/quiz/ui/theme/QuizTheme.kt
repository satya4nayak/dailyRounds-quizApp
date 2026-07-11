package com.assignment.mcqquiz.feature.quiz.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val QuizLightColorScheme = lightColorScheme(
    primary = Primary,
    background = Background,
    surface = SurfaceColor,
    onPrimary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    secondary = Amber,
    onSecondary = Color.White,
    outline = BorderColor
)

/**
 * Root MaterialTheme wrapper for the Quiz feature.
 * Apply this at the top of the Compose hierarchy in [MainActivity].
 */
@Composable
fun QuizAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = QuizLightColorScheme,
        typography = QuizTypography,
        content = content
    )
}
