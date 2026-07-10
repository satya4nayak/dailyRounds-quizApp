package com.assignment.mcqquiz.domain.model

data class QuizResult(
    val totalQuestions: Int,
    val correctCount: Int,
    val skippedCount: Int,
    val longestStreak: Int
)

