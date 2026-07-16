package com.assignment.mcqquiz.data.infra.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity persisting a user's progress for a single quiz category.
 */
@Entity(tableName = "category_progress")
data class CategoryProgressEntity(
    @PrimaryKey val categoryId: String,
    val status: String,
    /**
     * The `id` of the last question the user was on (from the API response).
     * null  = not started or quiz completed/reset.
     * n     = question id the user left at; used to resume IN_PROGRESS quizzes.
     *         If the id no longer exists in a fresh API response, resume from first question.
     */
    val lastQuestionId: Int? = null,
    /** Streak of correct answers for the current in-progress session. Resets to 0 on new/review start. */
    val currentStreak: Int = 0,
    /** The highest streak ever achieved for this category across all attempts. Never decreases. */
    val allTimeLongestStreak: Int = 0
)
