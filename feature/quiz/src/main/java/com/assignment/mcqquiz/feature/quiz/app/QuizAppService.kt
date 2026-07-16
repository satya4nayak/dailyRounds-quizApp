package com.assignment.mcqquiz.feature.quiz.app

import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.domain.service.CategoryProgressSnapshot
import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.data.infra.repository.CategoryProgressRepository
import com.assignment.mcqquiz.data.infra.repository.QuestionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Application service implementing the [QuizService] domain port.
 * All IO operations are dispatched on [ioDispatcher].
 */
class QuizAppService(
    private val questionRepository: QuestionRepository,
    private val categoryProgressRepository: CategoryProgressRepository,
    private val ioDispatcher: CoroutineDispatcher
) : QuizService {

    override suspend fun loadQuestions(url: String): List<Question> =
        withContext(ioDispatcher) { questionRepository.getQuestions(url) }

    override suspend fun saveProgress(
        categoryId: String,
        status: CategoryStatus,
        lastQuestionId: Int?
    ) = withContext(ioDispatcher) {
        categoryProgressRepository.upsert(categoryId, status, lastQuestionId)
    }

    override suspend fun updateLastQuestionId(categoryId: String, questionId: Int) =
        withContext(ioDispatcher) { categoryProgressRepository.updateLastQuestionId(categoryId, questionId) }

    override suspend fun updateStreaks(categoryId: String, currentStreak: Int, allTimeLongestStreak: Int) =
        withContext(ioDispatcher) { categoryProgressRepository.updateStreaks(categoryId, currentStreak, allTimeLongestStreak) }

    override suspend fun getProgress(categoryId: String): CategoryProgressSnapshot? =
        withContext(ioDispatcher) { categoryProgressRepository.getById(categoryId) }
}
