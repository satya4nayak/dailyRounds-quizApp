package com.assignment.mcqquiz.data.infra.api

import com.assignment.mcqquiz.data.infra.dto.QuestionDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [QuestionApiService].
 *
 * [QuestionApiService] is a thin delegate — its only contract is:
 *   1. It calls [QuizRetrofitService.fetchQuestions] exactly once per invocation.
 *   2. It returns the result unchanged.
 *   3. Any exception thrown by the Retrofit service propagates to the caller.
 *
 * Data-shape assertions (10 questions, specific correctOptionIndex values, etc.)
 * belong to integration/contract tests against the real endpoint, not here.
 */
class QuestionApiServiceTest {

    private val retrofitService: QuizRetrofitService = mockk()
    private lateinit var apiService: QuestionApiService

    @Before
    fun setUp() {
        apiService = QuestionApiService(retrofitService)
    }

    private fun buildDtos(count: Int = 3): List<QuestionDto> = (1..count).map { i ->
        QuestionDto(
            id = i,
            question = "Question $i",
            options = listOf("A", "B", "C", "D"),
            correctOptionIndex = 0
        )
    }

    // ─── Delegation ───────────────────────────────────────────────────────────

    @Test
    fun `when fetchQuestions is called, then it delegates to the retrofit service`() =
        runTest {
            coEvery { retrofitService.fetchQuestions() } returns buildDtos()
            apiService.fetchQuestions()
            coVerify(exactly = 1) { retrofitService.fetchQuestions() }
        }

    // ─── Pass-through ─────────────────────────────────────────────────────────

    @Test
    fun `when retrofit service returns a list, then the same list is returned unchanged`() =
        runTest {
            val expected = buildDtos(count = 5)
            coEvery { retrofitService.fetchQuestions() } returns expected
            val result = apiService.fetchQuestions()
            assertEquals(expected, result)
        }

    @Test
    fun `when retrofit service returns an empty list, then an empty list is returned`() =
        runTest {
            coEvery { retrofitService.fetchQuestions() } returns emptyList()
            val result = apiService.fetchQuestions()
            assertEquals(emptyList<QuestionDto>(), result)
        }

    // ─── Exception propagation ────────────────────────────────────────────────

    @Test(expected = RuntimeException::class)
    fun `when retrofit service throws, then the exception propagates to the caller`() =
        runTest {
            coEvery { retrofitService.fetchQuestions() } throws RuntimeException("Network error")
            apiService.fetchQuestions()
        }
}
