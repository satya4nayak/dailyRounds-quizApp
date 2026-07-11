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
import kotlin.time.Duration.Companion.milliseconds

/**
 * MVI ViewModel for the Quiz feature.
 *
 * All MVI contracts ([Event], [Effect]) are co-located here as nested types —
 *
 * Inputs:  [Event]       — user intents sent from the screen via [handleEvent]
 * Outputs: [QuizUiState] — immutable state collected by the screen
 *          [Effect]      — one-off side-effect commands (errors, navigation)
 */
class QuizViewModel @Inject constructor(
    private val quizService: QuizService
) : ViewModel() {

    // ─── MVI Contracts ────────────────────────────────────────────────────────
    sealed interface Event {
        /** The user tapped an answer option at the given [optionIndex]. */
        data class OptionSelected(val optionIndex: Int) : Event

        /** The user tapped "Skip Question". */
        data object SkipQuestion : Event

        /** The user tapped "Restart Quiz" on the Results screen. */
        data object RestartQuiz : Event
    }

    sealed interface Effect {
        /** Emitted when question loading fails. Show [message] in a Snackbar. */
        data class ShowQuestionLoadError(val message: String) : Effect

        /**
         * Emitted when the quiz session ends (user taps Restart or all questions are done).
         * The Activity should call finish() and restart itself so Compose resets from scratch.
         */
        data object RestartApp : Effect
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
            is Event.OptionSelected -> onOptionSelected(event.optionIndex)
            is Event.SkipQuestion   -> onSkip()
            is Event.RestartQuiz    -> onRestart()
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
                        screen = QuizAppContract.Quiz,
                        // Carry the all-time best streak forward into the new session so
                        // the Results screen always shows the historical high-water mark.
                        longestStreak = allTimeLongestStreak
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _effects.emit(Effect.ShowQuestionLoadError("Failed to load questions. Please try again."))
            }
        }
    }

    private fun onOptionSelected(index: Int) {
        val state = _uiState.value
        if (state.isAnswerRevealed) return

        val isCorrect = index == state.questions[state.currentQuestionIndex].correctOptionIndex
        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val newLongestStreak = maxOf(state.longestStreak, newStreak)

        // Show the celebration whenever the streak reaches or exceeds 3.
        // The ViewModel is solely responsible for both showing and hiding it.
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
        // Persist the highest streak reached in this session before the Activity dies.
        allTimeLongestStreak = maxOf(allTimeLongestStreak, _uiState.value.longestStreak)
        // Use emit (suspending) instead of tryEmit to guarantee delivery to the collector.
        viewModelScope.launch {
            _effects.emit(Effect.RestartApp)
        }
    }

    private fun advanceToNextQuestion() {
        _uiState.update { state ->
            val nextIndex = state.currentQuestionIndex + 1
            if (nextIndex < state.questions.size) {
                state.copy(
                    currentQuestionIndex = nextIndex,
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

    suspend fun onShowStreakCelebrationDismissed() {
        delay(STREAK_CELEBRATION_DISMISS_MS)
        _uiState.update { it.copy(showStreakCelebration = false) }
        delay(AUTO_ADVANCE_DELAY_MS - STREAK_CELEBRATION_DISMISS_MS)
    }

    companion object {
        // Survives Activity restarts within the same process lifetime.
        // internal so tests in this module can reset between runs.
        internal var allTimeLongestStreak: Int = 0
            private set

        /** For use in unit tests only — resets the cross-session streak counter. */
        internal fun resetAllTimeLongestStreakForTest() { allTimeLongestStreak = 0 }

        private val AUTO_ADVANCE_DELAY_MS         = 2_000L.milliseconds
        private val STREAK_CELEBRATION_DISMISS_MS = 1_500L.milliseconds
    }

}
