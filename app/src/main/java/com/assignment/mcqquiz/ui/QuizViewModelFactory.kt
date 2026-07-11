package com.assignment.mcqquiz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.QuizViewModel
import javax.inject.Provider

/**
 * [ViewModelProvider.Factory] for [QuizViewModel].
 */
class QuizViewModelFactory(
    private val provider: Provider<QuizViewModel>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return provider.get() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
