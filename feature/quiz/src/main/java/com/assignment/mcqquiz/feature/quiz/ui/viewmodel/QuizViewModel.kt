package com.assignment.mcqquiz.feature.quiz.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.feature.quiz.domain.state.QuizUiState
import kotlinx.coroutines.CancellationException
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
import kotlin.time.Duration.Companion.milliseconds

/**
 * MVI ViewModel for the Quiz feature.
 *
 * Runtime args are read from [SavedStateHandle]:
 *   - KEY_CATEGORY_ID  : String  — the selected category id
 *   - KEY_QUESTION_URL : String  — the full questions endpoint URL
 */
class QuizViewModel(
    savedStateHandle: SavedStateHandle,
    private val quizService: QuizService
) : ViewModel() {

    private val categoryId: String = requireNotNull(savedStateHandle[KEY_CATEGORY_ID]) {
        "QuizViewModel requires '$KEY_CATEGORY_ID' in SavedStateHandle"
    }
    private val questionUrl: String = requireNotNull(savedStateHandle[KEY_QUESTION_URL]) {
        "QuizViewModel requires '$KEY_QUESTION_URL' in SavedStateHandle"
    }

    // ─── MVI Contracts ────────────────────────────────────────────────────────

    sealed interface Event {
        data class OptionSelected(val optionIndex: Int) : Event
        data object SkipQuestion  : Event
        data object FinishModule  : Event
        data object NavigateBack  : Event
        data object RetryApiCall  : Event
        data object InitialLoad   : Event
    }

    sealed interface Effect {
        data object ShowLoader        : Effect
        data object NavigateToQuiz    : Effect
        data object NavigateToResults : Effect
        data object ShowError         : Effect
        data object FinishModule      : Effect
        /** Back pressed mid-quiz — no completion save, return to category list. */
        data object NavigateBack      : Effect
    }

    // ─── Streams ──────────────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<Effect>(replay = 1, extraBufferCapacity = 8)
    val effects: SharedFlow<Effect> = _effects.asSharedFlow()

    private var autoAdvanceJob: Job? = null
    private var isInitialized = false
    /** True when the quiz was opened in Review mode (COMPLETED); flips to IN_PROGRESS on first answer. */
    private var wasCompleted = false

    // ─── Public MVI Entry Point ───────────────────────────────────────────────

    fun handleEvent(event: Event) {
        when (event) {
            is Event.InitialLoad    -> onInitialLoad()
            is Event.OptionSelected -> onOptionSelected(event.optionIndex)
            is Event.SkipQuestion   -> onSkip()
            is Event.FinishModule   -> onFinish()
            is Event.NavigateBack   -> onNavigateBack()
            is Event.RetryApiCall   -> onRetry()
        }
    }

    // ─── Private Reducers ────────────────────────────────────────────────────

    /**
     * True suspend function — suspends the caller until loading is fully complete.
     * Dispatcher switching is handled inside [QuizService] via withContext(ioDispatcher).
     */
    private suspend fun loadQuestions() {
        _effects.emit(Effect.ShowLoader)
        try {
            val questions = quizService.loadQuestions(questionUrl)
            if (questions.isEmpty()) {
                _effects.emit(Effect.ShowError)
                return
            }
            val savedProgress = quizService.getProgress(categoryId)

            var initCurrentStreak = 0
            var initAllTimeLongest = 0

            val startIndex = when {
                savedProgress == null || savedProgress.status == CategoryStatus.NOT_STARTED -> {
                    quizService.saveProgress(
                        categoryId = categoryId,
                        status = CategoryStatus.IN_PROGRESS,
                        lastQuestionId = questions.first().id
                    )
                    0
                }
                savedProgress.status == CategoryStatus.IN_PROGRESS -> {
                    initCurrentStreak = savedProgress.currentStreak
                    initAllTimeLongest = savedProgress.allTimeLongestStreak
                    val savedId = savedProgress.lastQuestionId
                    if (savedId != null) {
                        val idx = questions.indexOfFirst { it.id == savedId }
                        if (idx >= 0) idx else 0
                    } else 0
                }
                else -> {
                    wasCompleted = true
                    initCurrentStreak = 0
                    initAllTimeLongest = savedProgress.allTimeLongestStreak
                    0
                }
            }
            _uiState.update {
                it.copy(
                    questions = questions,
                    currentQuestionIndex = startIndex,
                    currentStreak = initCurrentStreak,
                    longestStreak = initCurrentStreak,
                    allTimeLongestStreak = initAllTimeLongest
                )
            }
            _effects.emit(Effect.NavigateToQuiz)
        } catch (e: CancellationException) {
            throw e  // must re-throw — structured concurrency depends on it
        } catch (_: Exception) {
            _effects.emit(Effect.ShowError)
        }
    }

    private fun onInitialLoad() {
        if (isInitialized) return
        isInitialized = true
        viewModelScope.launch { loadQuestions() }
    }

    private fun onRetry() {
        viewModelScope.launch { loadQuestions() }
    }

    private fun onOptionSelected(index: Int) {
        val state = _uiState.value
        val isCorrect = index == state.questions[state.currentQuestionIndex].correctOptionIndex
        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val newLongestStreak = maxOf(state.longestStreak, newStreak)
        val newAllTimeLongest = maxOf(state.allTimeLongestStreak, newStreak)
        val showStreakCelebration = isCorrect && newStreak == 3

        _uiState.update {
            it.copy(
                selectedOptionIndex = index,
                isAnswerRevealed = true,
                currentStreak = newStreak,
                longestStreak = newLongestStreak,
                allTimeLongestStreak = newAllTimeLongest,
                showStreakCelebration = showStreakCelebration,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount
            )
        }

        if (wasCompleted) {
            wasCompleted = false
            viewModelScope.launch {
                quizService.saveProgress(
                    categoryId = categoryId,
                    status = CategoryStatus.IN_PROGRESS,
                    lastQuestionId = state.questions.getOrNull(state.currentQuestionIndex)?.id
                )
                quizService.updateStreaks(categoryId, newStreak, newAllTimeLongest)
            }
        } else {
            viewModelScope.launch {
                quizService.updateStreaks(categoryId, newStreak, newAllTimeLongest)
            }
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
        // Increment skip counter then move on immediately — no answer reveal, no delay.
        _uiState.update { it.copy(skippedCount = it.skippedCount + 1) }
        advanceToNextQuestion()
    }

    private fun onFinish() {
        autoAdvanceJob?.cancel()
        viewModelScope.launch {
            quizService.saveProgress(
                categoryId = categoryId,
                status = CategoryStatus.COMPLETED,
                lastQuestionId = null
            )
            _effects.emit(Effect.FinishModule)
        }
    }

    private fun onNavigateBack() {
        autoAdvanceJob?.cancel()
        wasCompleted = false
        val currentQuestion = _uiState.value.let { it.questions.getOrNull(it.currentQuestionIndex) }
        if (currentQuestion != null) {
            viewModelScope.launch { quizService.updateLastQuestionId(categoryId, currentQuestion.id) }
        }
        viewModelScope.launch { _effects.emit(Effect.NavigateBack) }
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
            viewModelScope.launch { quizService.updateLastQuestionId(categoryId, state.questions[nextIndex].id) }
        } else {
            _uiState.update { it.copy(selectedOptionIndex = null, isAnswerRevealed = false, showStreakCelebration = false) }
            viewModelScope.launch { _effects.emit(Effect.NavigateToResults) }
        }
    }

    private suspend fun onShowStreakCelebrationDismissed() {
        delay(STREAK_CELEBRATION_DISMISS_MS)
        _uiState.update { it.copy(showStreakCelebration = false) }
        delay(AUTO_ADVANCE_DELAY_MS - STREAK_CELEBRATION_DISMISS_MS)
    }

    companion object {
        const val KEY_CATEGORY_ID  = "categoryId"
        const val KEY_QUESTION_URL = "questionUrl"

        private val AUTO_ADVANCE_DELAY_MS         = 2_000L.milliseconds
        private val STREAK_CELEBRATION_DISMISS_MS = 1_500L.milliseconds
    }
}
