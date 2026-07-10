package com.assignment.mcqquiz.feature.quiz.di

import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.data.infra.repository.QuestionRepository
import com.assignment.mcqquiz.feature.quiz.app.QuizAppService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger module for the :feature:quiz layer.
 *
 * Provides the application-layer binding:
 *   [QuizService] ← [QuizAppService]
 *
 * Infrastructure bindings (QuestionApiService, QuestionRepository) are
 * provided by [DataModule] which is included in the root [AppComponent].
 */
@Module
class QuizModule {

    @Provides
    @Singleton
    fun provideQuizService(
        questionRepository: QuestionRepository
    ): QuizService = QuizAppService(questionRepository)
}
