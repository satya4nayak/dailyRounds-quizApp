package com.assignment.mcqquiz.feature.quiz.domain.state

import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.feature.quiz.domain.contract.QuizAppContract
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [QuizUiState].
 *
 * Verifies default values, copy-based state transitions, and that the model
 * correctly represents every possible phase of a quiz session.
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
        assertEquals(0, state.currentIndex)
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

    @Test
    fun `given default state, when screen is accessed, then it is Splash`() {
        val state = QuizUiState()
        assertEquals(QuizAppContract.Splash, state.screen)
    }

    @Test
    fun `given default state, when isLoading is accessed, then it is true`() {
        val state = QuizUiState()
        assertTrue(state.isLoading)
    }

    // ─── State transitions via copy ───────────────────────────────────────────

    @Test
    fun `given default state, when copied with questions and not loading, then screen should be Quiz`() {
        val questions = listOf(
            Question(id = 1, text = "Q?", options = listOf("A", "B"), correctOptionIndex = 0)
        )
        val loadedState = QuizUiState().copy(
            questions = questions,
            isLoading = false,
            screen = QuizAppContract.Quiz
        )
        assertEquals(QuizAppContract.Quiz, loadedState.screen)
        assertFalse(loadedState.isLoading)
        assertEquals(1, loadedState.questions.size)
    }

    @Test
    fun `given loaded state, when answer is selected, then isAnswerRevealed is true`() {
        val state = QuizUiState(isLoading = false).copy(
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
        val base = QuizUiState(isLoading = false, screen = QuizAppContract.Quiz)
        val afterSkip = base.copy(skippedCount = base.skippedCount + 1)
        assertEquals(1, afterSkip.skippedCount)
    }

    @Test
    fun `given quiz state at last question, when answer given, then screen transitions to Results`() {
        val state = QuizUiState(
            isLoading = false,
            screen = QuizAppContract.Quiz,
            currentIndex = 9,
            correctCount = 10
        ).copy(screen = QuizAppContract.Results)
        assertEquals(QuizAppContract.Results, state.screen)
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
        val s1 = QuizUiState(currentIndex = 0)
        val s2 = QuizUiState(currentIndex = 1)
        assertTrue(s1 != s2)
    }
}

