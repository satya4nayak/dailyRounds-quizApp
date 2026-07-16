package com.assignment.mcqquiz.data.infra.api

import com.assignment.mcqquiz.data.infra.dto.CategoryDto
import retrofit2.http.GET

/**
 * Retrofit interface for the categories endpoint only.
 * Question fetching uses raw OkHttp with the full URL supplied by the backend.
 */
interface QuizRetrofitService {

    /** Fetches the list of quiz categories (id, title, description, questions_url). */
    @GET("dr-samrat/ee986f16da9d8303c1acfd364ece22c5/raw")
    suspend fun fetchCategories(): List<CategoryDto>
}

