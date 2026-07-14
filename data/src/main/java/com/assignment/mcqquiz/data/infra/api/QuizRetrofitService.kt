package com.assignment.mcqquiz.data.infra.api

import com.assignment.mcqquiz.data.infra.dto.QuestionDto
import retrofit2.http.GET

/**
 * Retrofit interface for the quiz questions endpoint.
 * The path is relative to [BASE_URL] defined in build.gradle.kts.
 */
interface QuizRetrofitService {

    @GET("dr-samrat/53846277a8fcb034e482906ccc0d12b2/raw")
    suspend fun fetchQuestions(): List<QuestionDto>
}

