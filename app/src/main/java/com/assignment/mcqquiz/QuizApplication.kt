package com.assignment.mcqquiz

import android.app.Application
import com.assignment.mcqquiz.data.di.DataModule
import com.assignment.mcqquiz.di.AppComponent
import com.assignment.mcqquiz.di.AppModule
import com.assignment.mcqquiz.di.DaggerAppComponent

class QuizApplication : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .dataModule(DataModule())
            .appModule(AppModule())
            .build()
    }
}

