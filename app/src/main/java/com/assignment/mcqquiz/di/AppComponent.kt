package com.assignment.mcqquiz.di

import com.assignment.mcqquiz.data.di.DataModule
import com.assignment.mcqquiz.data.di.NetworkModule
import com.assignment.mcqquiz.feature.quiz.di.QuizModule
import com.assignment.mcqquiz.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Root Dagger component for the application.
 */
@Singleton
@Component(modules = [NetworkModule::class, DataModule::class, QuizModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}
