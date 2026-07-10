package com.assignment.mcqquiz.data.domain.model

/**
 * Core domain entity representing a single quiz question.
 * Pure Kotlin — no Android or framework dependencies.
 */
data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctOptionIndex: Int
)

