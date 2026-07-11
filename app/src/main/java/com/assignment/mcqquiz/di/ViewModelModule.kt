package com.assignment.mcqquiz.di

import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.QuizViewModel
import com.assignment.mcqquiz.ui.QuizViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Dagger module for the ViewModel layer.
 */
@Module
class ViewModelModule {

    @Provides
    @Singleton
    fun provideQuizViewModelFactory(
        provider: Provider<QuizViewModel>
    ): QuizViewModelFactory = QuizViewModelFactory(provider)
}

