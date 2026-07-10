package com.assignment.mcqquiz.data.mapper

import com.assignment.mcqquiz.data.dto.QuestionDto
import com.assignment.mcqquiz.domain.model.Question

fun QuestionDto.toDomain(): Question = Question(
    id = id,
    text = question,
    options = options,
    correctOptionIndex = correctOptionIndex
)

