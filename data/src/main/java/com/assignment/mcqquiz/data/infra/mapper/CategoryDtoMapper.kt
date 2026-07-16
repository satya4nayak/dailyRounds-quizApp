package com.assignment.mcqquiz.data.infra.mapper

import com.assignment.mcqquiz.data.domain.model.QuizCategory
import com.assignment.mcqquiz.data.infra.dto.CategoryDto

/**
 * Maps the infrastructure-layer [CategoryDto] to the domain [QuizCategory] entity.
 */
fun CategoryDto.toDomainModel(): QuizCategory = QuizCategory(
    id = id,
    title = title,
    description = description,
    questionUrl = questionUrl
)

