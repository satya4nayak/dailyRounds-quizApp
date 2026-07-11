package com.assignment.mcqquiz.data.infra.repository

import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.infra.api.QuestionApiClient
import com.assignment.mcqquiz.data.infra.dto.QuestionDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [QuestionRepositoryImpl].
 *
 * The [QuestionApiClient] is mocked so tests cover:
 *  - Correct delegation to the API client.
 *  - Accurate DTO → domain model mapping for the full list.
 *  - Propagation of exceptions from the API layer to the caller.
 *  - Handling of an empty response.
 */
class QuestionRepositoryImplTest {

    // ─── Mocks & SUT ──────────────────────────────────────────────────────────

    private val apiClient: QuestionApiClient = mockk()
    private lateinit var repository: QuestionRepositoryImpl

    @Before
    fun setUp() {
        repository = QuestionRepositoryImpl(apiClient)
    }

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private val sampleDtos = listOf(
        QuestionDto(id = 1, question = "Q1", options = listOf("A", "B", "C", "D"), correctOptionIndex = 0),
        QuestionDto(id = 2, question = "Q2", options = listOf("E", "F", "G", "H"), correctOptionIndex = 2),
        QuestionDto(id = 3, question = "Q3", options = listOf("I", "J", "K", "L"), correctOptionIndex = 3)
    )

    private val expectedDomainModels = listOf(
        Question(id = 1, text = "Q1", options = listOf("A", "B", "C", "D"), correctOptionIndex = 0),
        Question(id = 2, text = "Q2", options = listOf("E", "F", "G", "H"), correctOptionIndex = 2),
        Question(id = 3, text = "Q3", options = listOf("I", "J", "K", "L"), correctOptionIndex = 3)
    )

    // ─── Happy path ───────────────────────────────────────────────────────────

    @Test
    fun `given api returns dtos, when getQuestions is called, then mapped domain models are returned`() =
        runTest {
            coEvery { apiClient.fetchQuestions() } returns sampleDtos

            val result = repository.getQuestions()

            assertEquals(expectedDomainModels, result)
        }

    @Test
    fun `given api returns dtos, when getQuestions is called, then dto question field maps to domain text field`() =
        runTest {
            coEvery { apiClient.fetchQuestions() } returns sampleDtos

            val result = repository.getQuestions()

            assertEquals("Q1", result[0].text)
            assertEquals("Q2", result[1].text)
            assertEquals("Q3", result[2].text)
        }

    @Test
    fun `given api returns dtos, when getQuestions is called, then apiClient fetchQuestions is invoked exactly once`() =
        runTest {
            coEvery { apiClient.fetchQuestions() } returns sampleDtos

            repository.getQuestions()

            coVerify(exactly = 1) { apiClient.fetchQuestions() }
        }

    @Test
    fun `given api returns dtos, when getQuestions is called, then returned list has same size as dto list`() =
        runTest {
            coEvery { apiClient.fetchQuestions() } returns sampleDtos

            val result = repository.getQuestions()

            assertEquals(sampleDtos.size, result.size)
        }

    @Test
    fun `given api returns dtos, when getQuestions is called, then correctOptionIndex is preserved in each model`() =
        runTest {
            coEvery { apiClient.fetchQuestions() } returns sampleDtos

            val result = repository.getQuestions()

            assertEquals(0, result[0].correctOptionIndex)
            assertEquals(2, result[1].correctOptionIndex)
            assertEquals(3, result[2].correctOptionIndex)
        }

    // ─── Empty dataset ────────────────────────────────────────────────────────

    @Test
    fun `given api returns empty list, when getQuestions is called, then empty list is returned`() =
        runTest {
            coEvery { apiClient.fetchQuestions() } returns emptyList()

            val result = repository.getQuestions()

            assertTrue(result.isEmpty())
        }

    // ─── Error handling ───────────────────────────────────────────────────────

    @Test(expected = RuntimeException::class)
    fun `given api throws RuntimeException, when getQuestions is called, then exception propagates`() =
        runTest {
            coEvery { apiClient.fetchQuestions() } throws RuntimeException("Network failure")

            repository.getQuestions()
        }

    @Test(expected = IllegalStateException::class)
    fun `given api throws IllegalStateException, when getQuestions is called, then exception propagates`() =
        runTest {
            coEvery { apiClient.fetchQuestions() } throws IllegalStateException("Server error")

            repository.getQuestions()
        }

    @Test
    fun `given api throws exception, when getQuestions fails, then apiClient was still called once`() =
        runTest {
            coEvery { apiClient.fetchQuestions() } throws RuntimeException("Boom")

            runCatching { repository.getQuestions() }

            coVerify(exactly = 1) { apiClient.fetchQuestions() }
        }

    // ─── Single item ──────────────────────────────────────────────────────────

    @Test
    fun `given api returns single dto, when getQuestions is called, then single domain model is returned`() =
        runTest {
            val single = listOf(
                QuestionDto(id = 99, question = "Only Q", options = listOf("A", "B"), correctOptionIndex = 1)
            )
            coEvery { apiClient.fetchQuestions() } returns single

            val result = repository.getQuestions()

            assertEquals(1, result.size)
            assertEquals(99, result[0].id)
            assertEquals("Only Q", result[0].text)
            assertEquals(1, result[0].correctOptionIndex)
        }
}

