package com.assignment.mcqquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.assignment.mcqquiz.feature.quiz.ui.QuizRoot
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme
import com.assignment.mcqquiz.feature.quiz.viewmodel.QuizViewModel
import javax.inject.Inject
import javax.inject.Provider

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
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return viewModelProvider.get() as T
                }
            }
            val vm: QuizViewModel = viewModel(factory = factory)

            QuizAppTheme {
                QuizRoot(viewModel = vm)
            }
        }
    }
}

