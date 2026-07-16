package com.assignment.mcqquiz

import android.app.Application
import com.assignment.mcqquiz.data.domain.service.CategoryService
import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.di.AppComponent
import com.assignment.mcqquiz.di.DaggerAppComponent
import com.assignment.mcqquiz.feature.quiz.di.QuizFeatureDependencies

/**
 * Application entry point.
 * Implements [QuizFeatureDependencies] so feature-module Activities can obtain their
 * services without referencing :app's Dagger component directly.
 */
class QuizApplication : Application(), QuizFeatureDependencies {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appContext(this)
            .build()
    }

    override fun quizService(): QuizService = appComponent.quizService()
    override fun categoryService(): CategoryService = appComponent.categoryService()
}
