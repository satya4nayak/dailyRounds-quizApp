package com.assignment.mcqquiz.di

import android.content.Context
import com.assignment.mcqquiz.data.di.DataModule
import com.assignment.mcqquiz.data.di.NetworkModule
import com.assignment.mcqquiz.data.domain.service.CategoryService
import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.feature.quiz.di.QuizModule
import com.assignment.mcqquiz.ui.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Root Dagger component for the application.
 */
@Singleton
@Component(modules = [NetworkModule::class, DataModule::class, QuizModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)

    fun quizService(): QuizService
    fun categoryService(): CategoryService

    @Component.Builder
    interface Builder {
        @BindsInstance fun appContext(context: Context): Builder
        fun build(): AppComponent
    }
}
