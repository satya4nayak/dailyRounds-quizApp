package com.assignment.mcqquiz.di

import com.assignment.mcqquiz.MainActivity
import com.assignment.mcqquiz.data.di.DataModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}

