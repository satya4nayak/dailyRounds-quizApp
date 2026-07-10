package com.assignment.mcqquiz.data.infra.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for a question as returned by the remote/mock API.
 * Strictly an infrastructure concern — mapped to [Question] via [QuestionDtoMapper].
 */
@Serializable
data class QuestionDto(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int
)

