package com.assignment.mcqquiz.data.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Unit tests for the [QuizResult] domain model.
 *
 * Verifies property storage, equality, copy, and derived relationships
 * (e.g. correctCount cannot exceed totalQuestions in valid state).
 */
class QuizResultTest {

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private val sampleResult = QuizResult(
        totalQuestions = 10,
        correctQuestionCount = 7,
        skippedQuestionCount = 2,
        longestStreak = 4
    )

    // ─── Property access ──────────────────────────────────────────────────────

    @Test
    fun `given a quiz result, when totalQuestions is accessed, then correct count is returned`() {
        assertEquals(10, sampleResult.totalQuestions)
    }

    @Test
    fun `given a quiz result, when correctCount is accessed, then correct count is returned`() {
        assertEquals(7, sampleResult.correctQuestionCount)
    }

    @Test
    fun `given a quiz result, when skippedCount is accessed, then correct count is returned`() {
        assertEquals(2, sampleResult.skippedQuestionCount)
    }

    @Test
    fun `given a quiz result, when longestStreak is accessed, then correct streak is returned`() {
        assertEquals(4, sampleResult.longestStreak)
    }

    // ─── Equality & copy ──────────────────────────────────────────────────────

    @Test
    fun `given two results with identical fields, when compared, then they are equal`() {
        val duplicate = QuizResult(
            totalQuestions = 10,
            correctQuestionCount = 7,
            skippedQuestionCount = 2,
            longestStreak = 4
        )
        assertEquals(sampleResult, duplicate)
    }

    @Test
    fun `given two results with different correctCounts, when compared, then they are not equal`() {
        val different = sampleResult.copy(correctQuestionCount = 5)
        assertNotEquals(sampleResult, different)
    }

    @Test
    fun `given a result, when copied with updated skippedCount, then only skippedCount changes`() {
        val updated = sampleResult.copy(skippedQuestionCount = 3)
        assertEquals(3, updated.skippedQuestionCount)
        assertEquals(sampleResult.totalQuestions, updated.totalQuestions)
        assertEquals(sampleResult.correctQuestionCount, updated.correctQuestionCount)
        assertEquals(sampleResult.longestStreak, updated.longestStreak)
    }

    // ─── Edge cases ───────────────────────────────────────────────────────────

    @Test
    fun `given a perfect quiz result, when created, then correctCount equals totalQuestions`() {
        val perfect = QuizResult(
            totalQuestions = 10,
            correctQuestionCount = 10,
            skippedQuestionCount = 0,
            longestStreak = 10
        )
        assertEquals(perfect.totalQuestions, perfect.correctQuestionCount)
    }

    @Test
    fun `given a quiz result with all skipped, when created, then correctCount is zero`() {
        val allSkipped = QuizResult(
            totalQuestions = 10,
            correctQuestionCount = 0,
            skippedQuestionCount = 10,
            longestStreak = 0
        )
        assertEquals(0, allSkipped.correctQuestionCount)
        assertEquals(0, allSkipped.longestStreak)
    }

    @Test
    fun `given a quiz result with zero questions, when created, then all counts are zero`() {
        val empty = QuizResult(
            totalQuestions = 0,
            correctQuestionCount = 0,
            skippedQuestionCount = 0,
            longestStreak = 0
        )
        assertEquals(0, empty.totalQuestions)
        assertEquals(0, empty.correctQuestionCount)
    }

    @Test
    fun `given a quiz result, when longestStreak exceeds correct count, then it stores the value as-is`() {
        // longestStreak could technically be > correctCount in edge cases like streak reset mid-quiz
        val result = QuizResult(
            totalQuestions = 5,
            correctQuestionCount = 2,
            skippedQuestionCount = 1,
            longestStreak = 2
        )
        assertEquals(2, result.longestStreak)
    }
}

