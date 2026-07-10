package com.assignment.mcqquiz.data.di

import com.assignment.mcqquiz.data.repository.QuestionRepositoryImpl
import com.assignment.mcqquiz.data.source.QuizApiService
import com.assignment.mcqquiz.domain.repository.QuestionRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideQuizApiService(): QuizApiService = QuizApiService()

    @Provides
    @Singleton
    fun provideQuestionRepository(
        apiService: QuizApiService
    ): QuestionRepository = QuestionRepositoryImpl(apiService)
}

