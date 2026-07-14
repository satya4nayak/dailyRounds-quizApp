package com.assignment.mcqquiz.feature.quiz.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.feature.quiz.domain.state.QuizUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
import kotlin.math.log
import kotlin.time.Duration.Companion.milliseconds

/**
 * MVI ViewModel for the Quiz feature.
 *
 * Inputs:  [Event]       — user intents sent from the screen via [handleEvent]
 * Outputs: [QuizUiState] — immutable quiz-session data collected by the screen
 *          [Effect]      — single stream for both navigation signals and one-shot OS actions.
 */
class QuizViewModel @Inject constructor(
    private val quizService: QuizService
) : ViewModel() {

    // ─── MVI Contracts ────────────────────────────────────────────────────────

    sealed interface Event {
        data class OptionSelected(val optionIndex: Int) : Event
        data object SkipQuestion  : Event
        data object RestartQuiz   : Event
        data object RetryApiCall  : Event
        data object InitialLoad   : Event
    }

    sealed interface Effect {
        /** Network call in progress — show the loader screen. */
        data object ShowLoader        : Effect
        /** Questions loaded — navigate to the quiz screen. */
        data object NavigateToQuiz    : Effect
        /** All questions answered/skipped — navigate to the results screen. */
        data object NavigateToResults : Effect
        /** Network call failed or returned no data — show the error screen. */
        data object ShowError         : Effect
        /** User tapped Restart — one-shot: finish this Activity, launch a fresh one. */
        data object RestartGame       : Effect
    }

    // ─── Streams ──────────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<Effect>(replay = 1, extraBufferCapacity = 8)
    val effects: SharedFlow<Effect> = _effects.asSharedFlow()

    private var autoAdvanceJob: Job? = null

    /** Guards against re-fetching on configuration changes. */
    private var isInitialized = false

    init { /* intentionally empty — load is triggered explicitly via Event.InitialLoad */ }

    // ─── Public MVI Entry Point ───────────────────────────────────────────────

    fun handleEvent(event: Event) {
        when (event) {
            is Event.InitialLoad    -> onInitialLoad()
            is Event.OptionSelected -> onOptionSelected(event.optionIndex)
            is Event.SkipQuestion   -> onSkip()
            is Event.RestartQuiz    -> onRestart()
            is Event.RetryApiCall   -> onRetry()
        }
    }

    // ─── Private Reducers ────────────────────────────────────────────────────

    private suspend fun loadQuestions() {
        _effects.emit(Effect.ShowLoader)
        viewModelScope.launch {
            try {
                val questions = quizService.loadQuestions()
                if (questions.isEmpty()) {
                    _effects.emit(Effect.ShowError)
                } else {
                    _uiState.update { it.copy(questions = questions, longestStreak = allTimeLongestStreak) }
                    _effects.emit(Effect.NavigateToQuiz)
                }
            } catch (_: Exception) {
                _effects.emit(Effect.ShowError)
            }
        }
    }

    private fun onInitialLoad() {
        if (isInitialized) return   // config change — ViewModel already has data, no extra API call
        isInitialized = true
        viewModelScope.launch { loadQuestions() }
    }

    private fun onRetry() {
        viewModelScope.launch { loadQuestions() }    // user explicitly asked — bypass guard
    }

    private fun onOptionSelected(index: Int) {
        val state = _uiState.value
        val isCorrect = index == state.questions[state.currentQuestionIndex].correctOptionIndex
        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val newLongestStreak = maxOf(state.longestStreak, newStreak)
        val showStreakCelebration = isCorrect && newStreak >= 3

        _uiState.update {
            it.copy(
                selectedOptionIndex = index,
                isAnswerRevealed = true,
                currentStreak = newStreak,
                longestStreak = newLongestStreak,
                showStreakCelebration = showStreakCelebration,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount
            )
        }

        autoAdvanceJob?.cancel()
        autoAdvanceJob = viewModelScope.launch {
            if (showStreakCelebration) onShowStreakCelebrationDismissed()
            else delay(AUTO_ADVANCE_DELAY_MS)
            advanceToNextQuestion()
        }
    }

    private fun onSkip() {
        autoAdvanceJob?.cancel()
        _uiState.update { it.copy(skippedCount = it.skippedCount + 1) }
        advanceToNextQuestion()
    }

    private fun onRestart() {
        autoAdvanceJob?.cancel()
        allTimeLongestStreak = maxOf(allTimeLongestStreak, _uiState.value.longestStreak)
        viewModelScope.launch { _effects.emit(Effect.RestartGame) }
    }

    private fun advanceToNextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentQuestionIndex + 1
        if (nextIndex < state.questions.size) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false,
                    showStreakCelebration = false
                )
            }
        } else {
            _uiState.update { it.copy(selectedOptionIndex = null, isAnswerRevealed = false, showStreakCelebration = false) }
            viewModelScope.launch { _effects.emit(Effect.NavigateToResults) }
        }
    }

    suspend fun onShowStreakCelebrationDismissed() {
        delay(STREAK_CELEBRATION_DISMISS_MS)
        _uiState.update { it.copy(showStreakCelebration = false) }
        delay(AUTO_ADVANCE_DELAY_MS - STREAK_CELEBRATION_DISMISS_MS)
    }

    companion object {
        internal var allTimeLongestStreak: Int = 0
            private set

        internal fun resetAllTimeLongestStreakForTest() { allTimeLongestStreak = 0 }

        private val AUTO_ADVANCE_DELAY_MS         = 2_000L.milliseconds
        private val STREAK_CELEBRATION_DISMISS_MS = 1_500L.milliseconds
    }
}
