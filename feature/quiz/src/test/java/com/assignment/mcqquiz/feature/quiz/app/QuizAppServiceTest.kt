package com.assignment.mcqquiz.feature.quiz.app

import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.infra.repository.QuestionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [QuizAppService].
 *
 * [QuizAppService] is the application-layer port adapter that implements the
 * [com.assignment.mcqquiz.data.domain.service.QuizService] domain interface by
 * delegating to [QuestionRepository].
 *
 * Tests verify:
 *  - Correct delegation to the repository.
 *  - Transparent pass-through of domain models.
 *  - Exception propagation without swallowing.
 *  - Correct handling of an empty repository response.
 */
class QuizAppServiceTest {

    // ─── Mocks & SUT ──────────────────────────────────────────────────────────

    private val repository: QuestionRepository = mockk()
    private lateinit var service: QuizAppService

    @Before
    fun setUp() {
        service = QuizAppService(repository)
    }

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private val sampleQuestions = listOf(
        Question(id = 1, question = "Q1", options = listOf("A", "B", "C", "D"), correctOptionIndex = 0),
        Question(id = 2, question = "Q2", options = listOf("A", "B", "C", "D"), correctOptionIndex = 1),
        Question(id = 3, question = "Q3", options = listOf("A", "B", "C", "D"), correctOptionIndex = 2)
    )

    // ─── Happy path ───────────────────────────────────────────────────────────

    @Test
    fun `given repository returns questions, when loadQuestions is called, then same questions are returned`() =
        runTest {
            coEvery { repository.getQuestions() } returns sampleQuestions

            val result = service.loadQuestions()

            assertEquals(sampleQuestions, result)
        }

    @Test
    fun `given repository returns questions, when loadQuestions is called, then repository getQuestions is called exactly once`() =
        runTest {
            coEvery { repository.getQuestions() } returns sampleQuestions

            service.loadQuestions()

            coVerify(exactly = 1) { repository.getQuestions() }
        }

    @Test
    fun `given repository returns 3 questions, when loadQuestions is called, then result has 3 questions`() =
        runTest {
            coEvery { repository.getQuestions() } returns sampleQuestions

            val result = service.loadQuestions()

            assertEquals(3, result.size)
        }

    @Test
    fun `given repository returns questions, when loadQuestions is called, then question ids are preserved`() =
        runTest {
            coEvery { repository.getQuestions() } returns sampleQuestions

            val result = service.loadQuestions()

            assertEquals(listOf(1, 2, 3), result.map { it.id })
        }

    @Test
    fun `given repository returns questions, when loadQuestions is called, then question texts are preserved`() =
        runTest {
            coEvery { repository.getQuestions() } returns sampleQuestions

            val result = service.loadQuestions()

            assertEquals(listOf("Q1", "Q2", "Q3"), result.map { it.question })
        }

    // ─── Empty dataset ────────────────────────────────────────────────────────

    @Test
    fun `given repository returns empty list, when loadQuestions is called, then empty list is returned`() =
        runTest {
            coEvery { repository.getQuestions() } returns emptyList()

            val result = service.loadQuestions()

            assertTrue(result.isEmpty())
        }

    // ─── Error propagation ────────────────────────────────────────────────────

    @Test(expected = RuntimeException::class)
    fun `given repository throws RuntimeException, when loadQuestions is called, then exception propagates`() =
        runTest {
            coEvery { repository.getQuestions() } throws RuntimeException("DB failure")

            service.loadQuestions()
        }

    @Test(expected = IllegalStateException::class)
    fun `given repository throws IllegalStateException, when loadQuestions is called, then exception propagates`() =
        runTest {
            coEvery { repository.getQuestions() } throws IllegalStateException("Broken state")

            service.loadQuestions()
        }

    @Test
    fun `given repository throws exception, when loadQuestions fails, then repository was still called once`() =
        runTest {
            coEvery { repository.getQuestions() } throws RuntimeException("Boom")

            runCatching { service.loadQuestions() }

            coVerify(exactly = 1) { repository.getQuestions() }
        }

    // ─── Ordering guarantee ───────────────────────────────────────────────────

    @Test
    fun `given repository returns ordered questions, when loadQuestions is called, then order is preserved`() =
        runTest {
            val ordered = listOf(
                Question(id = 5, question = "Q5", options = listOf("A"), correctOptionIndex = 0),
                Question(id = 1, question = "Q1", options = listOf("A"), correctOptionIndex = 0),
                Question(id = 3, question = "Q3", options = listOf("A"), correctOptionIndex = 0)
            )
            coEvery { repository.getQuestions() } returns ordered

            val result = service.loadQuestions()

            assertEquals(listOf(5, 1, 3), result.map { it.id })
        }
}

