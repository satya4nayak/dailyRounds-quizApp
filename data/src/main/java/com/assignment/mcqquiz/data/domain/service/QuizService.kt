package com.assignment.mcqquiz.data.domain.service

import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.model.Question

/**
 * Domain service port for loading quiz questions and saving progress.
 */
interface QuizService {
    suspend fun loadQuestions(url: String): List<Question>
    suspend fun saveProgress(
        categoryId: String,
        status: CategoryStatus,
        lastQuestionId: Int? = null
    )
    /** Lightweight per-question update — only writes the last question id. */
    suspend fun updateLastQuestionId(categoryId: String, questionId: Int)
    /** Persists live streak counters after every answered question. */
    suspend fun updateStreaks(categoryId: String, currentStreak: Int, allTimeLongestStreak: Int)
    /** Fetch saved progress so the quiz can resume from the last position. */
    suspend fun getProgress(categoryId: String): CategoryProgressSnapshot?
}
