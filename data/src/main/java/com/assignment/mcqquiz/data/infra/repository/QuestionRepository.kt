package com.assignment.mcqquiz.data.infra.repository

import com.assignment.mcqquiz.data.domain.model.QuizCategory
import com.assignment.mcqquiz.data.domain.model.Question

/**
 * Repository contract for accessing quiz question and category data.
 */
interface QuestionRepository {
    suspend fun getCategories(): List<QuizCategory>
    suspend fun getQuestions(url: String): List<Question>
}

