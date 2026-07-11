package com.assignment.mcqquiz.feature.quiz.domain.state

import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.feature.quiz.domain.contract.QuizAppContract

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
    val showStreakCelebration: Boolean = false,
    val correctCount: Int = 0,
    val skippedCount: Int = 0,
    val screen: QuizAppContract = QuizAppContract.Splash,
    val isLoading: Boolean = true
)
