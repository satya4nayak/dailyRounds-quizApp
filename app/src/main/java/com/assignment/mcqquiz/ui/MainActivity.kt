package com.assignment.mcqquiz.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.assignment.mcqquiz.QuizApplication
import com.assignment.mcqquiz.feature.quiz.ui.screen.QuizEntryPoint
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme
import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.QuizViewModel
import javax.inject.Inject
import javax.inject.Provider

/**
 * The single Activity of the application.
 *
 * Responsibilities:
 *  - Receives Dagger injection from [AppComponent] (the [QuizViewModel] provider).
 *  - Creates a [ViewModelProvider.Factory] that delegates to the Dagger-managed provider.
 *  - Hosts the Compose hierarchy under [QuizAppTheme].
 *  - Passes the ViewModel to [QuizEntryPoint] — the only Composable that holds a ViewModel ref.
 */
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelProvider: Provider<QuizViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as QuizApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    viewModelProvider.get() as T
            }
            val vm: QuizViewModel = viewModel(factory = factory)

            QuizAppTheme {
                QuizEntryPoint(viewModel = vm)
            }
        }
    }
}

