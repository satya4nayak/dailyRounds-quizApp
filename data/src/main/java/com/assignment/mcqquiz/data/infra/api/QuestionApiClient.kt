package com.assignment.mcqquiz.data.infra.api

import com.assignment.mcqquiz.data.infra.dto.QuestionDto

/**
 * Contract for the remote quiz question data source.
 *
 * Decouples [QuestionRepositoryImpl] from the concrete [QuestionApiService],
 * making the data source easily swappable (mock ↔ Retrofit ↔ test fake)
 * without touching any repository logic.
 */
interface QuestionApiClient {
    suspend fun fetchQuestions(): List<QuestionDto>
}

