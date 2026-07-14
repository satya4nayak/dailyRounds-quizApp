package com.assignment.mcqquiz.data.infra.api

import com.assignment.mcqquiz.data.infra.dto.QuestionDto
import javax.inject.Inject

/**
 * Real API service that delegates to [QuizRetrofitService] for remote quiz data.
 */
class QuestionApiService @Inject constructor(
    private val retrofitService: QuizRetrofitService
) : QuestionApiClient {

    override suspend fun fetchQuestions(): List<QuestionDto> =
        retrofitService.fetchQuestions()
}

