package com.assignment.mcqquiz.feature.quiz.app

import com.assignment.mcqquiz.data.domain.model.QuizCategory
import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.service.CategoryProgressSnapshot
import com.assignment.mcqquiz.data.domain.service.CategoryService
import com.assignment.mcqquiz.data.infra.repository.CategoryProgressRepository
import com.assignment.mcqquiz.data.infra.repository.QuestionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.concurrent.Volatile

/**
 * Application service implementing [CategoryService].
 * Categories are cached in-memory for the app session lifetime.
 * All IO operations are dispatched on [ioDispatcher].
 */
class CategoryAppService(
    private val questionRepository: QuestionRepository,
    private val categoryProgressRepository: CategoryProgressRepository,
    private val ioDispatcher: CoroutineDispatcher
) : CategoryService {
    @Volatile
    private var cachedCategories: List<QuizCategory> = emptyList()

    override suspend fun loadCategories(): List<QuizCategory> =
        withContext(ioDispatcher) {
            if (cachedCategories.isNotEmpty()) return@withContext cachedCategories
            val fetched = questionRepository.getCategories()
            if (fetched.isNotEmpty()) cachedCategories = fetched
            fetched
        }

    override suspend fun getCategoryProgress(id: String): CategoryProgressSnapshot? =
        withContext(ioDispatcher) { categoryProgressRepository.getById(id) }

    override suspend fun getAllProgress(): List<CategoryProgressSnapshot> =
        withContext(ioDispatcher) { categoryProgressRepository.getAll() }

    override suspend fun saveProgress(
        categoryId: String,
        status: CategoryStatus,
        lastQuestionId: Int?
    ) = withContext(ioDispatcher) {
        categoryProgressRepository.upsert(categoryId, status, lastQuestionId)
    }
}
