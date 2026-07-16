package com.assignment.mcqquiz.data.infra.repository

import com.assignment.mcqquiz.data.domain.model.QuizCategory
import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.infra.api.QuestionApiClient
import com.assignment.mcqquiz.data.infra.mapper.toDomainModel
import javax.inject.Inject

/**
 * Fetches DTOs from [QuestionApiClient] and maps them to domain entities.
 */
class QuestionRepositoryImpl @Inject constructor(
    private val apiClient: QuestionApiClient
) : QuestionRepository {

    override suspend fun getCategories(): List<QuizCategory> =
        apiClient.fetchCategories().map { it.toDomainModel() }

    override suspend fun getQuestions(url: String): List<Question> =
        apiClient.fetchQuestions(url).map { it.toDomainModel() }
}
