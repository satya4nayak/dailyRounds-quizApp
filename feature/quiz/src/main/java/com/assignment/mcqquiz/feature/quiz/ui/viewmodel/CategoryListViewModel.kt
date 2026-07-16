package com.assignment.mcqquiz.feature.quiz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.service.CategoryService
import com.assignment.mcqquiz.feature.quiz.domain.state.CategoryListUiState
import com.assignment.mcqquiz.feature.quiz.domain.state.CategoryWithProgress
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI ViewModel for the Category List screen.
 *
 * Inputs:  [Event]
 * Outputs: [CategoryListUiState], [Effect]
 */
class CategoryListViewModel(
    private val categoryService: CategoryService
) : ViewModel() {

    // ─── MVI Contracts ────────────────────────────────────────────────────────

    sealed interface Event {
        data object InitialLoad : Event
        data object Retry       : Event
        data class CategorySelected(val categoryId: String, val questionUrl: String) : Event
    }

    sealed interface Effect {
        data object ShowLoader : Effect
        data object ShowError  : Effect
        data object ShowList   : Effect
        data class NavigateToQuiz(val categoryId: String, val questionUrl: String) : Effect
    }

    // ─── Streams ──────────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<Effect>(replay = 1, extraBufferCapacity = 8)
    val effects: SharedFlow<Effect> = _effects.asSharedFlow()

    private var isInitialized = false

    // ─── Public MVI Entry Point ───────────────────────────────────────────────

    fun handleEvent(event: Event) {
        when (event) {
            is Event.InitialLoad         -> onInitialLoad()
            is Event.Retry               -> onRetry()
            is Event.CategorySelected    -> onCategorySelected(event.categoryId, event.questionUrl)
        }
    }

    // ─── Private Reducers ────────────────────────────────────────────────────

    private fun onInitialLoad() {
        if (isInitialized) return
        isInitialized = true
        viewModelScope.launch { loadCategories() }
    }

    private fun onRetry() {
        viewModelScope.launch { loadCategories() }
    }

    private fun onCategorySelected(categoryId: String, questionUrl: String) {
        viewModelScope.launch {
            _effects.emit(Effect.NavigateToQuiz(categoryId, questionUrl))
        }
    }

    private suspend fun loadCategories() {
        _effects.emit(Effect.ShowLoader)
        try {
            val categories = categoryService.loadCategories()
            if (categories.isEmpty()) {
                _effects.emit(Effect.ShowError)
                return
            }
            val progressMap = categoryService.getAllProgress().associateBy { it.categoryId }
            val merged = categories.map { category ->
                val progress = progressMap[category.id]
                CategoryWithProgress(
                    category = category,
                    status = progress?.status ?: CategoryStatus.NOT_STARTED
                )
            }
            _uiState.update { it.copy(categories = merged) }
            _effects.emit(Effect.ShowList)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            _effects.emit(Effect.ShowError)
        }
    }
}
