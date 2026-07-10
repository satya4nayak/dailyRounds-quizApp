package com.assignment.mcqquiz.domain.service

import com.assignment.mcqquiz.domain.model.Question

/**
 * Port (interface) for the quiz loading service.
 * Implemented by QuizAppService in the :app module.
 * Used by QuizViewModel in :feature:quiz to avoid circular Gradle dependencies.
 */
interface QuizService {
    suspend fun loadQuestions(): List<Question>
}

