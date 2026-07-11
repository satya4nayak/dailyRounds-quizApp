package com.assignment.mcqquiz.data.domain.service

import com.assignment.mcqquiz.data.domain.model.Question

/**
 * Domain service port defining the contract for loading quiz questions.
 */
interface QuizService {
    suspend fun loadQuestions(): List<Question>
}

