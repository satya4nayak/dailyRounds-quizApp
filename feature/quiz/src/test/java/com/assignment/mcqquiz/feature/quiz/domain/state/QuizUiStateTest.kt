package com.assignment.mcqquiz.feature.quiz.domain.state

import com.assignment.mcqquiz.data.domain.model.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [QuizUiState].
 *
 * [QuizUiState] is a pure quiz-session data holder — navigation state is managed
 * by [com.assignment.mcqquiz.feature.quiz.ui.viewmodel.QuizViewModel.Effect], so
 * there are no screen or isLoading fields to test here.
 */
class QuizUiStateTest {

    // ─── Default values ───────────────────────────────────────────────────────

    @Test
    fun `given default state, when questions is accessed, then it is empty`() {
        val state = QuizUiState()
        assertTrue(state.questions.isEmpty())
    }

    @Test
    fun `given default state, when currentIndex is accessed, then it is 0`() {
        val state = QuizUiState()
        assertEquals(0, state.currentQuestionIndex)
    }

    @Test
    fun `given default state, when selectedOptionIndex is accessed, then it is null`() {
        val state = QuizUiState()
        assertNull(state.selectedOptionIndex)
    }

    @Test
    fun `given default state, when isAnswerRevealed is accessed, then it is false`() {
        val state = QuizUiState()
        assertFalse(state.isAnswerRevealed)
    }

    @Test
    fun `given default state, when currentStreak is accessed, then it is 0`() {
        val state = QuizUiState()
        assertEquals(0, state.currentStreak)
    }

    @Test
    fun `given default state, when longestStreak is accessed, then it is 0`() {
        val state = QuizUiState()
        assertEquals(0, state.longestStreak)
    }

    @Test
    fun `given default state, when allTimeLongestStreak is accessed, then it is 0`() {
        val state = QuizUiState()
        assertEquals(0, state.allTimeLongestStreak)
    }

    @Test
    fun `given default state, when showStreakCelebration is accessed, then it is false`() {
        val state = QuizUiState()
        assertFalse(state.showStreakCelebration)
    }

    @Test
    fun `given default state, when correctCount is accessed, then it is 0`() {
        val state = QuizUiState()
        assertEquals(0, state.correctCount)
    }

    @Test
    fun `given default state, when skippedCount is accessed, then it is 0`() {
        val state = QuizUiState()
        assertEquals(0, state.skippedCount)
    }

    // ─── State transitions via copy ───────────────────────────────────────────

    @Test
    fun `given default state, when copied with questions, then questions list is set`() {
        val questions = listOf(
            Question(id = 1, question = "Q?", options = listOf("A", "B"), correctOptionIndex = 0)
        )
        val loadedState = QuizUiState().copy(questions = questions)
        assertEquals(1, loadedState.questions.size)
    }

    @Test
    fun `given loaded state, when answer is selected, then isAnswerRevealed is true`() {
        val state = QuizUiState().copy(
            selectedOptionIndex = 2,
            isAnswerRevealed = true,
            correctCount = 1,
            currentStreak = 1,
            longestStreak = 1
        )
        assertTrue(state.isAnswerRevealed)
        assertEquals(2, state.selectedOptionIndex)
        assertEquals(1, state.correctCount)
        assertEquals(1, state.currentStreak)
    }

    @Test
    fun `given active quiz state, when question is skipped, then skippedCount increments`() {
        val base = QuizUiState()
        val afterSkip = base.copy(skippedCount = base.skippedCount + 1)
        assertEquals(1, afterSkip.skippedCount)
    }

    @Test
    fun `given streak milestone state, when showStreakCelebration is set true, then celebration flag is enabled`() {
        val state = QuizUiState().copy(
            currentStreak = 3,
            longestStreak = 3,
            showStreakCelebration = true
        )
        assertTrue(state.showStreakCelebration)
        assertEquals(3, state.currentStreak)
    }

    @Test
    fun `given celebration showing state, when dismissed, then showStreakCelebration is false`() {
        val celebrating = QuizUiState().copy(showStreakCelebration = true)
        val dismissed = celebrating.copy(showStreakCelebration = false)
        assertFalse(dismissed.showStreakCelebration)
    }

    // ─── Equality ─────────────────────────────────────────────────────────────

    @Test
    fun `given two identical default states, when compared, then they are equal`() {
        assertEquals(QuizUiState(), QuizUiState())
    }

    @Test
    fun `given two states with different currentIndex, when compared, then they are not equal`() {
        val s1 = QuizUiState(currentQuestionIndex = 0)
        val s2 = QuizUiState(currentQuestionIndex = 1)
        assertTrue(s1 != s2)
    }

    // ─── allTimeLongestStreak ─────────────────────────────────────────────────

    @Test
    fun `given state with allTimeLongestStreak 5, when accessed, then it is 5`() {
        val state = QuizUiState(allTimeLongestStreak = 5)
        assertEquals(5, state.allTimeLongestStreak)
    }

    @Test
    fun `given state copied with new allTimeLongestStreak, then only that field changes`() {
        val base = QuizUiState(allTimeLongestStreak = 3, longestStreak = 3, currentStreak = 2)
        val updated = base.copy(allTimeLongestStreak = 7)
        assertEquals(7, updated.allTimeLongestStreak)
        assertEquals(3, updated.longestStreak)
        assertEquals(2, updated.currentStreak)
    }

    @Test
    fun `given two states differing only in allTimeLongestStreak, when compared, they are not equal`() {
        val s1 = QuizUiState(allTimeLongestStreak = 3)
        val s2 = QuizUiState(allTimeLongestStreak = 7)
        assertTrue(s1 != s2)
    }

    @Test
    fun `given state with allTimeLongestStreak greater than longestStreak, when accessed, then both values are independent`() {
        // allTimeLongestStreak is from a previous session; longestStreak is the current attempt
        val state = QuizUiState(longestStreak = 2, allTimeLongestStreak = 10)
        assertEquals(2, state.longestStreak)
        assertEquals(10, state.allTimeLongestStreak)
    }
}
