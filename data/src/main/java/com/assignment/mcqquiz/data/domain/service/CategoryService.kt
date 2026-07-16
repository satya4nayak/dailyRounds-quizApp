package com.assignment.mcqquiz.data.domain.service

import com.assignment.mcqquiz.data.domain.model.QuizCategory
import com.assignment.mcqquiz.data.domain.model.CategoryStatus

/**
 * Domain service port for loading quiz categories and reading their persisted progress.
 */
interface CategoryService {
    suspend fun loadCategories(): List<QuizCategory>
    suspend fun getCategoryProgress(id: String): CategoryProgressSnapshot?
    suspend fun getAllProgress(): List<CategoryProgressSnapshot>
    suspend fun saveProgress(
        categoryId: String,
        status: CategoryStatus,
        lastQuestionId: Int? = null
    )
}

/**
 * Lightweight snapshot of a category's persisted progress.
 * Score / streak are intentionally excluded: they are in-session only and the API schema may change.
 */
data class CategoryProgressSnapshot(
    val categoryId: String,
    val status: CategoryStatus,
    val lastQuestionId: Int? = null,
    /** Current running streak for the in-progress session. Restored on app reopen. */
    val currentStreak: Int = 0,
    /** All-time highest streak across all attempts for this category. */
    val allTimeLongestStreak: Int = 0
)
