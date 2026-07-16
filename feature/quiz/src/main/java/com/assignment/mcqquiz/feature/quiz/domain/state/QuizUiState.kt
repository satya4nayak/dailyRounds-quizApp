package com.assignment.mcqquiz.feature.quiz.domain.state

import com.assignment.mcqquiz.data.domain.model.Question

/**
 * Immutable UI state model for the Quiz feature.
 *
 * Owned by the domain layer because it is a pure data representation
 * of what the screen should render — no Android framework types included.
 * The ViewModel produces this; Composables consume it passively.
 */
data class QuizUiState(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    /** The highest streak ever achieved for this category across all time. Loaded from DB on start. */
    val allTimeLongestStreak: Int = 0,
    val showStreakCelebration: Boolean = false,
    val correctCount: Int = 0,
    val skippedCount: Int = 0
)
