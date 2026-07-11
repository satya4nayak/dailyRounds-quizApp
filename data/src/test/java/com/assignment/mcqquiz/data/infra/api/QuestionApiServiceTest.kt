package com.assignment.mcqquiz.data.infra.api

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [QuestionApiService].
 *
 * Because this is the mock data source, the tests verify:
 *   - The returned dataset has exactly 10 questions.
 *   - Every question has 4 answer options.
 *   - Every question has a unique id.
 *   - Specific known questions have the correct content and correctOptionIndex.
 *
 * [runTest] fast-forwards the simulated 1-second network delay so the suite
 * runs in milliseconds.
 */
class QuestionApiServiceTest {

    private lateinit var apiService: QuestionApiService

    @Before
    fun setUp() {
        apiService = QuestionApiService()
    }

    // ─── Dataset size ─────────────────────────────────────────────────────────

    @Test
    fun `given the mock api service, when fetchQuestions is called, then exactly 10 questions are returned`() =
        runTest {
            val questions = apiService.fetchQuestions()
            assertEquals(10, questions.size)
        }

    // ─── Question structure ───────────────────────────────────────────────────

    @Test
    fun `given the mock api service, when fetchQuestions is called, then every question has 4 options`() =
        runTest {
            val questions = apiService.fetchQuestions()
            questions.forEach { dto ->
                assertEquals(
                    "Question ${dto.id} should have 4 options but had ${dto.options.size}",
                    4,
                    dto.options.size
                )
            }
        }

    @Test
    fun `given the mock api service, when fetchQuestions is called, then all question ids are unique`() =
        runTest {
            val questions = apiService.fetchQuestions()
            val distinctIds = questions.map { it.id }.distinct()
            assertEquals(questions.size, distinctIds.size)
        }

    @Test
    fun `given the mock api service, when fetchQuestions is called, then no question has blank text`() =
        runTest {
            val questions = apiService.fetchQuestions()
            questions.forEach { dto ->
                assertTrue(
                    "Question ${dto.id} has blank text",
                    dto.question.isNotBlank()
                )
            }
        }

    @Test
    fun `given the mock api service, when fetchQuestions is called, then correctOptionIndex is within options bounds`() =
        runTest {
            val questions = apiService.fetchQuestions()
            questions.forEach { dto ->
                assertTrue(
                    "correctOptionIndex ${dto.correctOptionIndex} out of range for question ${dto.id}",
                    dto.correctOptionIndex in dto.options.indices
                )
            }
        }

    // ─── Specific question assertions ─────────────────────────────────────────

    @Test
    fun `given the mock api service, when fetchQuestions is called, then question 1 has id 1`() =
        runTest {
            val question1 = apiService.fetchQuestions().first { it.id == 1 }
            assertNotNull(question1)
            assertEquals(1, question1.id)
        }

    @Test
    fun `given the mock api service, when fetchQuestions is called, then question 1 correctOptionIndex is 0`() =
        runTest {
            val question1 = apiService.fetchQuestions().first { it.id == 1 }
            assertEquals(0, question1.correctOptionIndex)
        }

    @Test
    fun `given the mock api service, when fetchQuestions is called, then question 5 correctOptionIndex is 1`() =
        runTest {
            // Question 5 has a correctOptionIndex of 1 (the non-zero outlier in the dataset)
            val question5 = apiService.fetchQuestions().first { it.id == 5 }
            assertEquals(1, question5.correctOptionIndex)
        }

    @Test
    fun `given the mock api service, when fetchQuestions is called, then question 1 first option mentions hidden feature`() =
        runTest {
            val question1 = apiService.fetchQuestions().first { it.id == 1 }
            assertTrue(question1.options[0].isNotBlank())
        }

    @Test
    fun `given the mock api service, when fetchQuestions is called twice, then identical data is returned`() =
        runTest {
            val first = apiService.fetchQuestions()
            val second = apiService.fetchQuestions()
            assertEquals(first, second)
        }

    // ─── IDs range ────────────────────────────────────────────────────────────

    @Test
    fun `given the mock api service, when fetchQuestions is called, then ids run sequentially from 1 to 10`() =
        runTest {
            val ids = apiService.fetchQuestions().map { it.id }.sorted()
            assertEquals((1..10).toList(), ids)
        }
}

