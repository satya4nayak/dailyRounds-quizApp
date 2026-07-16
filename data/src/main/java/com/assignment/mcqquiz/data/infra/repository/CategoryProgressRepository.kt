package com.assignment.mcqquiz.data.infra.repository

import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.service.CategoryProgressSnapshot

/**
 * Repository contract for reading and writing category progress.
 */
interface CategoryProgressRepository {
    suspend fun upsert(categoryId: String, status: CategoryStatus, lastQuestionId: Int? = null)
    suspend fun updateLastQuestionId(categoryId: String, questionId: Int)
    /** Updates live streak counters. Called after every answered question. */
    suspend fun updateStreaks(categoryId: String, currentStreak: Int, allTimeLongestStreak: Int)
    suspend fun getById(categoryId: String): CategoryProgressSnapshot?
    suspend fun getAll(): List<CategoryProgressSnapshot>
}
