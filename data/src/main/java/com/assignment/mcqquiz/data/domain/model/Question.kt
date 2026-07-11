package com.assignment.mcqquiz.data.domain.model

/**
 * Core domain entity representing a single quiz question.
 */
data class Question(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int
)

