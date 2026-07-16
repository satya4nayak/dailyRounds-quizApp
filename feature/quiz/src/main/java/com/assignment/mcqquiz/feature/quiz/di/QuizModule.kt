package com.assignment.mcqquiz.feature.quiz.di

import com.assignment.mcqquiz.data.di.IoDispatcher
import com.assignment.mcqquiz.data.domain.service.CategoryService
import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.data.infra.repository.CategoryProgressRepository
import com.assignment.mcqquiz.data.infra.repository.QuestionRepository
import com.assignment.mcqquiz.feature.quiz.app.CategoryAppService
import com.assignment.mcqquiz.feature.quiz.app.QuizAppService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

/**
 * Dagger module for the :feature:quiz layer.
 */
@Module
class QuizModule {

    @Provides
    @Singleton
    fun provideQuizService(
        questionRepository: QuestionRepository,
        categoryProgressRepository: CategoryProgressRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): QuizService = QuizAppService(questionRepository, categoryProgressRepository, ioDispatcher)

    @Provides
    @Singleton
    fun provideCategoryService(
        questionRepository: QuestionRepository,
        categoryProgressRepository: CategoryProgressRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): CategoryService = CategoryAppService(questionRepository, categoryProgressRepository, ioDispatcher)
}
