package com.assignment.mcqquiz

import android.app.Application
import com.assignment.mcqquiz.di.AppComponent
import com.assignment.mcqquiz.di.DaggerAppComponent

/**
 * Application entry point.
 * Builds the Dagger [AppComponent] once and holds it for the app lifetime.
 * Activities and other Android components retrieve it via `(application as QuizApplication).appComponent`.
 */
class QuizApplication : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
    }
}
