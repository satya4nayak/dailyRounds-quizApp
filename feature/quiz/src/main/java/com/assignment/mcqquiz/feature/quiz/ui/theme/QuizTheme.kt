package com.assignment.mcqquiz.feature.quiz.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val QuizDarkColorScheme = darkColorScheme(
    primary = Primary,
    background = Background,
    surface = Surface,
    onPrimary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    secondary = StreakActive,
    onSecondary = TextPrimary
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
        colorScheme = QuizDarkColorScheme,
        typography = QuizTypography,
        content = content
    )
}

