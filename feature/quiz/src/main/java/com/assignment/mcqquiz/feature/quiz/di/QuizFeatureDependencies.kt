package com.assignment.mcqquiz.feature.quiz.di

import com.assignment.mcqquiz.data.domain.service.CategoryService
import com.assignment.mcqquiz.data.domain.service.QuizService

/**
 * Provides the services that quiz-feature Activities need.
 * Implemented by the Application class in :app so the feature module
 * never references :app's Dagger component directly (no circular dependency).
 *
 * Usage inside a feature Activity:
 *   val deps = application as QuizFeatureDependencies
 */
interface QuizFeatureDependencies {
    fun quizService(): QuizService
    fun categoryService(): CategoryService
}
