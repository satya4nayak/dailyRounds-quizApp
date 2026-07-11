package com.assignment.mcqquiz.data.infra.repository

import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.infra.api.QuestionApiClient
import com.assignment.mcqquiz.data.infra.mapper.toDomainModel
import javax.inject.Inject

/**
 * Fetches DTOs from [QuestionApiClient] and maps them to domain [Question] entities.
 */
class QuestionRepositoryImpl @Inject constructor(
    private val apiClient: QuestionApiClient
) : QuestionRepository {

    override suspend fun getQuestions(): List<Question> =
        apiClient.fetchQuestions().map { it.toDomainModel() }
}

