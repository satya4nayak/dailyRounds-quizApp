package com.assignment.mcqquiz.feature.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.mcqquiz.domain.service.QuizService
import com.assignment.mcqquiz.feature.quiz.state.AppScreen
import com.assignment.mcqquiz.feature.quiz.state.QuizUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizViewModel @Inject constructor(
    private val quizService: QuizService
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var autoAdvanceJob: Job? = null

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val questions = quizService.loadQuestions()
            _uiState.update { state ->
                state.copy(
                    questions = questions,
                    isLoading = false,
                    screen = AppScreen.Quiz
                )
            }
        }
    }

    fun onOptionSelected(index: Int) {
        val state = _uiState.value
        if (state.isAnswerRevealed) return

        val isCorrect = index == state.questions[state.currentIndex].correctOptionIndex
        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val newLongestStreak = maxOf(state.longestStreak, newStreak)
        val showCelebration = isCorrect && newStreak > 0 && newStreak % 3 == 0

        _uiState.update { it ->
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
            advanceQuestion()
        }
    }

    fun onSkip() {
        autoAdvanceJob?.cancel()
        _uiState.update { it.copy(skippedCount = it.skippedCount + 1) }
        advanceQuestion()
    }

    fun onStreakCelebrationDismissed() {
        _uiState.update { it.copy(showStreakCelebration = false) }
    }

    fun onRestart() {
        autoAdvanceJob?.cancel()
        _uiState.value = QuizUiState()
        loadQuestions()
    }

    private fun advanceQuestion() {
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
                    screen = AppScreen.Results,
                    selectedOptionIndex = null,
                    isAnswerRevealed = false,
                    showStreakCelebration = false
                )
            }
        }
    }
}

