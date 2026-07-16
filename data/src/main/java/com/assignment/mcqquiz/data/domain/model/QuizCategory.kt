package com.assignment.mcqquiz.data.domain.model

/**
 * Core domain entity representing a quiz category/module.
 */
data class QuizCategory(
    val id: String,
    val title: String,
    val description: String,
    val questionUrl: String
)

