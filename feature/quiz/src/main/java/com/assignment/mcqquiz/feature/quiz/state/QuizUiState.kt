package com.assignment.mcqquiz.feature.quiz.state

import com.assignment.mcqquiz.domain.model.Question

data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val showStreakCelebration: Boolean = false,
    val correctCount: Int = 0,
    val skippedCount: Int = 0,
    val screen: AppScreen = AppScreen.Splash,
    val isLoading: Boolean = true
)

