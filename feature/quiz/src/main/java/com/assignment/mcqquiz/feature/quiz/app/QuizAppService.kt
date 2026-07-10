package com.assignment.mcqquiz.feature.quiz.app

import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.data.infra.repository.QuestionRepository

/**
 * Application service that implements the [QuizService] domain port.
 *
 * Orchestrates question loading by delegating to [QuestionRepository]
 * and returning clean domain [Question] entities to callers.
 * Future use cases (shuffling, filtering by category) belong here.
 */
class QuizAppService(
    private val questionRepository: QuestionRepository
) : QuizService {

    override suspend fun loadQuestions(): List<Question> =
        questionRepository.getQuestions()
}
