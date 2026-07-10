package com.assignment.mcqquiz.di

import com.assignment.mcqquiz.data.di.DataModule
import com.assignment.mcqquiz.feature.quiz.di.QuizModule
import com.assignment.mcqquiz.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Root Dagger component for the application.
 *
 * Includes both modules that together cover the full dependency graph:
 *   [DataModule]  — provides infra bindings (QuestionApiService, QuestionRepository)
 *   [QuizModule]  — provides app-layer binding (QuizService ← QuizAppService)
 */
@Singleton
@Component(modules = [DataModule::class, QuizModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}
