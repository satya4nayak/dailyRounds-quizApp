package com.assignment.mcqquiz.data.di

import com.assignment.mcqquiz.data.infra.api.QuestionApiClient
import com.assignment.mcqquiz.data.infra.api.QuestionApiService
import com.assignment.mcqquiz.data.infra.repository.QuestionRepository
import com.assignment.mcqquiz.data.infra.repository.QuestionRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger module for the :data layer.
 *
 * Binding chain:
 *   [QuestionApiClient]  ← [QuestionApiService]   (concrete mock implementation)
 *   [QuestionRepository] ← [QuestionRepositoryImpl] (depends on [QuestionApiClient])
 */
@Module
class DataModule {

    @Provides
    @Singleton
    fun provideQuestionApiClient(): QuestionApiClient = QuestionApiService()

    @Provides
    @Singleton
    fun provideQuestionRepository(
        apiClient: QuestionApiClient
    ): QuestionRepository = QuestionRepositoryImpl(apiClient)
}
