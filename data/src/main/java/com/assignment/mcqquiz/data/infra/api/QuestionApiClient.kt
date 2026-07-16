package com.assignment.mcqquiz.data.infra.api

import com.assignment.mcqquiz.data.infra.dto.CategoryDto
import com.assignment.mcqquiz.data.infra.dto.QuestionDto

/**
 * Contract for the remote quiz data source.
 */
interface QuestionApiClient {
    suspend fun fetchCategories(): List<CategoryDto>
    suspend fun fetchQuestions(url: String): List<QuestionDto>
}

