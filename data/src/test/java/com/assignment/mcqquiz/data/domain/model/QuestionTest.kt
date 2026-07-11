package com.assignment.mcqquiz.data.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Unit tests for the [Question] domain entity.
 *
 * Verifies correct property storage, equality semantics, and copy behaviour
 * guaranteed by Kotlin data classes.
 */
class QuestionTest {

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private val sampleQuestion = Question(
        id = 1,
        question = "What is the capital of France?",
        options = listOf("Berlin", "Madrid", "Paris", "Rome"),
        correctOptionIndex = 2
    )

    // ─── Property access ──────────────────────────────────────────────────────

    @Test
    fun `given a question, when id is accessed, then correct id is returned`() {
        assertEquals(1, sampleQuestion.id)
    }

    @Test
    fun `given a question, when text is accessed, then correct text is returned`() {
        assertEquals("What is the capital of France?", sampleQuestion.question)
    }

    @Test
    fun `given a question, when options are accessed, then full options list is returned`() {
        assertEquals(listOf("Berlin", "Madrid", "Paris", "Rome"), sampleQuestion.options)
    }

    @Test
    fun `given a question, when correctOptionIndex is accessed, then correct index is returned`() {
        assertEquals(2, sampleQuestion.correctOptionIndex)
    }

    @Test
    fun `given a question, when options size is checked, then it has four options`() {
        assertEquals(4, sampleQuestion.options.size)
    }

    // ─── Equality & copy ──────────────────────────────────────────────────────

    @Test
    fun `given two questions with identical fields, when compared, then they are equal`() {
        val duplicate = Question(
            id = 1,
            question = "What is the capital of France?",
            options = listOf("Berlin", "Madrid", "Paris", "Rome"),
            correctOptionIndex = 2
        )
        assertEquals(sampleQuestion, duplicate)
    }

    @Test
    fun `given two questions with different ids, when compared, then they are not equal`() {
        val different = sampleQuestion.copy(id = 99)
        assertNotEquals(sampleQuestion, different)
    }

    @Test
    fun `given a question, when copied with new text, then only text changes`() {
        val copied = sampleQuestion.copy(question = "Updated question?")
        assertEquals("Updated question?", copied.question)
        assertEquals(sampleQuestion.id, copied.id)
        assertEquals(sampleQuestion.options, copied.options)
        assertEquals(sampleQuestion.correctOptionIndex, copied.correctOptionIndex)
    }

    @Test
    fun `given a question with correctOptionIndex 0, when checking first option, then it is the correct answer`() {
        val q = Question(id = 2, question = "Q?", options = listOf("A", "B", "C", "D"), correctOptionIndex = 0)
        assertEquals("A", q.options[q.correctOptionIndex])
    }

    @Test
    fun `given a question with correctOptionIndex 3, when checking last option, then it is the correct answer`() {
        val q = Question(id = 3, question = "Q?", options = listOf("A", "B", "C", "D"), correctOptionIndex = 3)
        assertEquals("D", q.options[q.correctOptionIndex])
    }

    // ─── Edge cases ───────────────────────────────────────────────────────────

    @Test
    fun `given a question with empty text, when created, then it stores empty string`() {
        val q = Question(id = 4, question = "", options = listOf("A"), correctOptionIndex = 0)
        assertEquals("", q.question)
    }

    @Test
    fun `given a question with single option, when created, then options size is one`() {
        val q = Question(id = 5, question = "Q?", options = listOf("Only option"), correctOptionIndex = 0)
        assertEquals(1, q.options.size)
    }

    @Test
    fun `given a question with empty options list, when created, then options is empty`() {
        val q = Question(id = 6, question = "Q?", options = emptyList(), correctOptionIndex = 0)
        assertEquals(emptyList<String>(), q.options)
    }
}

