package com.assignment.mcqquiz.feature.quiz.ui.factory

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.assignment.mcqquiz.data.domain.service.CategoryService
import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.CategoryListViewModel
import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.QuizViewModel

/**
 * Single factory for all quiz-feature ViewModels.
 *
 * - [CategoryListViewModel] → built with [categoryService]
 * - [QuizViewModel]         → built with [SavedStateHandle] + [quizService]
 *
 * Dispatcher concerns are handled inside each service via withContext(ioDispatcher).
 */
class QuizViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val quizService: QuizService,
    private val categoryService: CategoryService,
    private val categoryId: String = "",
    private val questionUrl: String = ""
) : AbstractSavedStateViewModelFactory(
    owner,
    Bundle().apply {
        putString(QuizViewModel.KEY_CATEGORY_ID, categoryId)
        putString(QuizViewModel.KEY_QUESTION_URL, questionUrl)
    }
) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T = when {
        modelClass.isAssignableFrom(CategoryListViewModel::class.java) ->
            CategoryListViewModel(categoryService)
        modelClass.isAssignableFrom(QuizViewModel::class.java) ->
            QuizViewModel(handle, quizService)
        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    } as T
}
