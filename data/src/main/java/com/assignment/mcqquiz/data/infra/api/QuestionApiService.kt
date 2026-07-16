package com.assignment.mcqquiz.data.infra.api

import com.assignment.mcqquiz.data.infra.dto.CategoryDto
import com.assignment.mcqquiz.data.infra.dto.QuestionDto
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

/**
 * Real API service.
 *
 * - Categories  → delegated to [QuizRetrofitService] (base-URL-relative path).
 * - Questions   → raw [OkHttpClient] call using the full URL supplied by the categories API,
 *                 completely independent of any base URL.
 */
class QuestionApiService @Inject constructor(
    private val retrofitService: QuizRetrofitService,
    private val okHttpClient: OkHttpClient,
    private val json: Json
) : QuestionApiClient {

    override suspend fun fetchCategories(): List<CategoryDto> =
        retrofitService.fetchCategories()

    override suspend fun fetchQuestions(url: String): List<QuestionDto> {
        val request = Request.Builder().url(url).build()
        val responseBody = okHttpClient.newCall(request).execute().use { response ->
            response.body?.string() ?: "[]"
        }
        return json.decodeFromString(responseBody)
    }
}
