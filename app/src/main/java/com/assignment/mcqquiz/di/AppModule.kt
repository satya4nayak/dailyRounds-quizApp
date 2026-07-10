package com.assignment.mcqquiz.di

import com.assignment.mcqquiz.domain.repository.QuestionRepository
import com.assignment.mcqquiz.domain.service.QuizService
import com.assignment.mcqquiz.service.QuizAppService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideQuizService(
        repository: QuestionRepository
    ): QuizService = QuizAppService(repository)
}

