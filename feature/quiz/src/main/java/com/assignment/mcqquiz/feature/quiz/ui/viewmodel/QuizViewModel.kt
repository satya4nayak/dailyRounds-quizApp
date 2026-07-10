package com.assignment.mcqquiz.feature.quiz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.feature.quiz.domain.contract.QuizAppContract
import com.assignment.mcqquiz.feature.quiz.domain.state.QuizUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MVI ViewModel for the Quiz feature.
 *
 * All MVI contracts ([Event], [Effect]) are co-located here as nested types —
 * they are an intrinsic part of this ViewModel's contract and have no reason
 * to exist independently.
 *
 * Inputs:  [Event]       — user intents sent from the screen via [handleEvent]
 * Outputs: [QuizUiState] — immutable state collected by the screen
 *          [Effect]      — one-off side-effect commands (errors, navigation)
 *
 * All private functions mutate state; none are exposed directly to the UI.
 * The screen must ONLY interact through [handleEvent].
 */
class QuizViewModel @Inject constructor(
    private val quizService: QuizService
) : ViewModel() {

    // ─── MVI Contracts ────────────────────────────────────────────────────────

    /**
     * Represents every user interaction (intent) that can occur on Quiz screens.
     * The screen sends these to the ViewModel via [handleEvent].
     */
    sealed interface Event {
        /** The user tapped an answer option at the given [optionIndex]. */
        data class OptionSelected(val optionIndex: Int) : Event

        /** The user tapped "Skip Question". */
        data object SkipQuestion : Event

        /** The user tapped "Restart Quiz" on the Results screen. */
        data object RestartQuiz : Event

        /** The streak celebration overlay has been dismissed (auto or by tap). */
        data object StreakCelebrationDismissed : Event
    }

    /**
     * Represents one-off side-effect commands emitted to the screen.
     * Consumed exactly once — must NOT be modelled as persistent state.
     */
    sealed interface Effect {
        /** Emitted when question loading fails. Show [message] in a Snackbar. */
        data class ShowQuestionLoadError(val message: String) : Effect
    }

    // ─── State & Effect Streams ───────────────────────────────────────────────

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<Effect>(extraBufferCapacity = 1)
    val effects: SharedFlow<Effect> = _effects.asSharedFlow()

    private var autoAdvanceJob: Job? = null

    init {
        loadQuestions()
    }

    // ─── Public MVI Entry Point ───────────────────────────────────────────────

    fun handleEvent(event: Event) {
        when (event) {
            is Event.OptionSelected             -> onOptionSelected(event.optionIndex)
            is Event.SkipQuestion               -> onSkip()
            is Event.RestartQuiz                -> onRestart()
            is Event.StreakCelebrationDismissed -> onStreakCelebrationDismissed()
        }
    }

    // ─── Private State Reducers ───────────────────────────────────────────────

    private fun loadQuestions() {
        viewModelScope.launch {
            try {
                val questions = quizService.loadQuestions()
                _uiState.update { state ->
                    state.copy(
                        questions = questions,
                        isLoading = false,
                        screen = QuizAppContract.Quiz
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _effects.tryEmit(
                    Effect.ShowQuestionLoadError("Failed to load questions. Please try again.")
                )
            }
        }
    }

    private fun onOptionSelected(index: Int) {
        val state = _uiState.value
        if (state.isAnswerRevealed) return

        val isCorrect = index == state.questions[state.currentIndex].correctOptionIndex
        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val newLongestStreak = maxOf(state.longestStreak, newStreak)
        val showCelebration = isCorrect && newStreak > 0 && newStreak % 3 == 0

        _uiState.update {
            it.copy(
                selectedOptionIndex = index,
                isAnswerRevealed = true,
                currentStreak = newStreak,
                longestStreak = newLongestStreak,
                showStreakCelebration = showCelebration,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount
            )
        }

        autoAdvanceJob?.cancel()
        autoAdvanceJob = viewModelScope.launch {
            delay(2_000L)
            advanceToNextQuestion()
        }
    }

    private fun onSkip() {
        autoAdvanceJob?.cancel()
        _uiState.update { it.copy(skippedCount = it.skippedCount + 1) }
        advanceToNextQuestion()
    }

    private fun onStreakCelebrationDismissed() {
        _uiState.update { it.copy(showStreakCelebration = false) }
    }

    private fun onRestart() {
        autoAdvanceJob?.cancel()
        _uiState.value = QuizUiState()
        loadQuestions()
    }

    private fun advanceToNextQuestion() {
        _uiState.update { state ->
            val nextIndex = state.currentIndex + 1
            if (nextIndex < state.questions.size) {
                state.copy(
                    currentIndex = nextIndex,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false,
                    showStreakCelebration = false
                )
            } else {
                state.copy(
                    screen = QuizAppContract.Results,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false,
                    showStreakCelebration = false
                )
            }
        }
    }
}
