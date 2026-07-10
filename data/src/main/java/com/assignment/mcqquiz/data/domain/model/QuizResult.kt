package com.assignment.mcqquiz.data.domain.model

/**
 * Domain model capturing the final outcome of a completed quiz session.
 */
data class QuizResult(
    val totalQuestions: Int,
    val correctCount: Int,
    val skippedCount: Int,
    val longestStreak: Int
)

