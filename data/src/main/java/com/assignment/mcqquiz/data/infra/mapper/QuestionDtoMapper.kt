package com.assignment.mcqquiz.data.infra.mapper

import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.infra.dto.QuestionDto

/**
 * Maps the infrastructure-layer [QuestionDto] to the domain [Question] entity.
 */
fun QuestionDto.toDomainModel(): Question = Question(
    id = id,
    text = question,
    options = options,
    correctOptionIndex = correctOptionIndex
)

