package com.assignment.mcqquiz.data.domain.model

/**
 * Domain model capturing the final outcome of a completed quiz session.
 */
data class QuizResult(
    val totalQuestions: Int,
    val correctQuestionCount: Int,
    val skippedQuestionCount: Int,
    val longestStreak: Int
)

