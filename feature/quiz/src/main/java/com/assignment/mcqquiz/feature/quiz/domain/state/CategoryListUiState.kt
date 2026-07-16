package com.assignment.mcqquiz.feature.quiz.domain.state

import com.assignment.mcqquiz.data.domain.model.QuizCategory
import com.assignment.mcqquiz.data.domain.model.CategoryStatus

/**
 * Merges a [QuizCategory] with its persisted status for display on the list screen.
 * Score / streak are not stored — they are in-session only.
 */
data class CategoryWithProgress(
    val category: QuizCategory,
    val status: CategoryStatus = CategoryStatus.NOT_STARTED
)

data class CategoryListUiState(
    val categories: List<CategoryWithProgress> = emptyList()
)

