package com.assignment.mcqquiz.data.infra.repository

import com.assignment.mcqquiz.data.domain.model.Question

/**
 * Repository contract for accessing quiz question data.
 * Implemented by [QuestionRepositoryImpl].
 */
interface QuestionRepository {
    suspend fun getQuestions(): List<Question>
}

