package com.assignment.mcqquiz.data.infra.api

import com.assignment.mcqquiz.data.infra.dto.QuestionDto

/**
 * Contract for the remote quiz question data source.
 */
interface QuestionApiClient {
    suspend fun fetchQuestions(): List<QuestionDto>
}

