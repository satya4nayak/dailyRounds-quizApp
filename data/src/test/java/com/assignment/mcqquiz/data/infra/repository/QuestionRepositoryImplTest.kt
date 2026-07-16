package com.assignment.mcqquiz.data.infra.repository

import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.domain.model.QuizCategory
import com.assignment.mcqquiz.data.infra.api.QuestionApiClient
import com.assignment.mcqquiz.data.infra.dto.CategoryDto
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
 * [QuestionApiClient] is mocked so tests cover:
 *  - Correct delegation to the API client (URL is passed through).
 *  - Accurate DTO → domain-model mapping for questions and categories.
 *  - Propagation of exceptions from the API layer.
 *  - Handling of empty responses.
 */
class QuestionRepositoryImplTest {

    // ─── Mocks & SUT ──────────────────────────────────────────────────────────

    private val apiClient: QuestionApiClient = mockk()
    private lateinit var repository: QuestionRepositoryImpl

    private val url = "https://example.com/questions.json"

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
        Question(id = 1, question = "Q1", options = listOf("A", "B", "C", "D"), correctOptionIndex = 0),
        Question(id = 2, question = "Q2", options = listOf("E", "F", "G", "H"), correctOptionIndex = 2),
        Question(id = 3, question = "Q3", options = listOf("I", "J", "K", "L"), correctOptionIndex = 3)
    )

    private val sampleCategoryDtos = listOf(
        CategoryDto(id = "cat1", title = "Android", description = "Android questions", questionUrl = "https://example.com/android"),
        CategoryDto(id = "cat2", title = "Kotlin", description = "Kotlin questions", questionUrl = "https://example.com/kotlin")
    )

    // =========================================================================
    // getQuestions
    // =========================================================================

    @Test
    fun `given api returns dtos, when getQuestions called, then mapped domain models returned`() =
        runTest {
            coEvery { apiClient.fetchQuestions(url) } returns sampleDtos
            val result = repository.getQuestions(url)
            assertEquals(expectedDomainModels, result)
        }

    @Test
    fun `given getQuestions called, then apiClient fetchQuestions called with correct url`() =
        runTest {
            coEvery { apiClient.fetchQuestions(url) } returns sampleDtos
            repository.getQuestions(url)
            coVerify(exactly = 1) { apiClient.fetchQuestions(url) }
        }

    @Test
    fun `given api returns dtos, when getQuestions called, then returned list has same size as dto list`() =
        runTest {
            coEvery { apiClient.fetchQuestions(url) } returns sampleDtos
            val result = repository.getQuestions(url)
            assertEquals(sampleDtos.size, result.size)
        }

    @Test
    fun `given api returns dtos, when getQuestions called, then dto question field maps to domain question field`() =
        runTest {
            coEvery { apiClient.fetchQuestions(url) } returns sampleDtos
            val result = repository.getQuestions(url)
            assertEquals("Q1", result[0].question)
            assertEquals("Q2", result[1].question)
            assertEquals("Q3", result[2].question)
        }

    @Test
    fun `given api returns dtos, when getQuestions called, then correctOptionIndex is preserved`() =
        runTest {
            coEvery { apiClient.fetchQuestions(url) } returns sampleDtos
            val result = repository.getQuestions(url)
            assertEquals(0, result[0].correctOptionIndex)
            assertEquals(2, result[1].correctOptionIndex)
            assertEquals(3, result[2].correctOptionIndex)
        }

    @Test
    fun `given api returns dtos, when getQuestions called, then options list is preserved`() =
        runTest {
            coEvery { apiClient.fetchQuestions(url) } returns sampleDtos
            val result = repository.getQuestions(url)
            assertEquals(listOf("A", "B", "C", "D"), result[0].options)
        }

    @Test
    fun `given api returns empty list, when getQuestions called, then empty list returned`() =
        runTest {
            coEvery { apiClient.fetchQuestions(url) } returns emptyList()
            val result = repository.getQuestions(url)
            assertTrue(result.isEmpty())
        }

    @Test(expected = RuntimeException::class)
    fun `given api throws RuntimeException, when getQuestions called, then exception propagates`() =
        runTest {
            coEvery { apiClient.fetchQuestions(url) } throws RuntimeException("Network failure")
            repository.getQuestions(url)
        }

    @Test(expected = IllegalStateException::class)
    fun `given api throws IllegalStateException, when getQuestions called, then exception propagates`() =
        runTest {
            coEvery { apiClient.fetchQuestions(url) } throws IllegalStateException("Server error")
            repository.getQuestions(url)
        }

    @Test
    fun `given api throws, when getQuestions fails, then apiClient was still called once`() =
        runTest {
            coEvery { apiClient.fetchQuestions(url) } throws RuntimeException("Boom")
            runCatching { repository.getQuestions(url) }
            coVerify(exactly = 1) { apiClient.fetchQuestions(url) }
        }

    @Test
    fun `given api returns single dto, when getQuestions called, then single domain model returned`() =
        runTest {
            val single = listOf(
                QuestionDto(id = 99, question = "Only Q", options = listOf("A", "B"), correctOptionIndex = 1)
            )
            coEvery { apiClient.fetchQuestions(url) } returns single
            val result = repository.getQuestions(url)
            assertEquals(1, result.size)
            assertEquals(99, result[0].id)
            assertEquals("Only Q", result[0].question)
            assertEquals(1, result[0].correctOptionIndex)
        }

    // =========================================================================
    // getCategories
    // =========================================================================

    @Test
    fun `given api returns category dtos, when getCategories called, then mapped QuizCategories returned`() =
        runTest {
            coEvery { apiClient.fetchCategories() } returns sampleCategoryDtos
            val result = repository.getCategories()
            assertEquals(2, result.size)
        }

    @Test
    fun `given getCategories called, then apiClient fetchCategories called exactly once`() =
        runTest {
            coEvery { apiClient.fetchCategories() } returns sampleCategoryDtos
            repository.getCategories()
            coVerify(exactly = 1) { apiClient.fetchCategories() }
        }

    @Test
    fun `given api returns category dtos, when getCategories called, then category id is preserved`() =
        runTest {
            coEvery { apiClient.fetchCategories() } returns sampleCategoryDtos
            val result = repository.getCategories()
            assertEquals("cat1", result[0].id)
            assertEquals("cat2", result[1].id)
        }

    @Test
    fun `given api returns category dtos, when getCategories called, then title is preserved`() =
        runTest {
            coEvery { apiClient.fetchCategories() } returns sampleCategoryDtos
            val result = repository.getCategories()
            assertEquals("Android", result[0].title)
            assertEquals("Kotlin", result[1].title)
        }

    @Test
    fun `given api returns category dtos, when getCategories called, then questionUrl is preserved`() =
        runTest {
            coEvery { apiClient.fetchCategories() } returns sampleCategoryDtos
            val result = repository.getCategories()
            assertEquals("https://example.com/android", result[0].questionUrl)
        }

    @Test
    fun `given api returns empty list of categories, when getCategories called, then empty list returned`() =
        runTest {
            coEvery { apiClient.fetchCategories() } returns emptyList()
            val result = repository.getCategories()
            assertTrue(result.isEmpty())
        }

    @Test(expected = RuntimeException::class)
    fun `given api throws, when getCategories called, then exception propagates`() =
        runTest {
            coEvery { apiClient.fetchCategories() } throws RuntimeException("Network failure")
            repository.getCategories()
        }

    @Test
    fun `given api throws, when getCategories fails, then apiClient still called once`() =
        runTest {
            coEvery { apiClient.fetchCategories() } throws RuntimeException("Boom")
            runCatching { repository.getCategories() }
            coVerify(exactly = 1) { apiClient.fetchCategories() }
        }
}
