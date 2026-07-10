package com.assignment.mcqquiz.data.domain.service

import com.assignment.mcqquiz.data.domain.model.Question

/**
 * Domain service port defining the contract for loading quiz questions.
 *
 * Implemented by [QuizAppService] in the :feature:quiz app layer.
 * Consumed by [QuizViewModel] in the :feature:quiz ui layer.
 */
interface QuizService {
    suspend fun loadQuestions(): List<Question>
}

