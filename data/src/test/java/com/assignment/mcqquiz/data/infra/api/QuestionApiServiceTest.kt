package com.assignment.mcqquiz.data.infra.api

import com.assignment.mcqquiz.data.infra.dto.CategoryDto
import com.assignment.mcqquiz.data.infra.dto.QuestionDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [QuestionApiService].
 *
 * Contract:
 *  - fetchCategories() delegates to [QuizRetrofitService] exactly once and returns the result.
 *  - fetchQuestions(url) calls OkHttpClient with the supplied URL, parses the JSON body,
 *    and returns the mapped [QuestionDto] list.
 *  - Exceptions from either HTTP layer propagate to the caller unchanged.
 */
class QuestionApiServiceTest {

    private val retrofitService: QuizRetrofitService = mockk()
    private val okHttpClient: OkHttpClient = mockk()
    private val json: Json = Json { ignoreUnknownKeys = true }
    private lateinit var apiService: QuestionApiService

    // OkHttp chain mocks (relaxed so close() is auto-stubbed)
    private val call: Call = mockk()
    private val response: Response = mockk(relaxed = true)

    private val testUrl = "https://example.com/questions.json"

    @Before
    fun setUp() {
        apiService = QuestionApiService(retrofitService, okHttpClient, json)
    }

    // =========================================================================
    // fetchCategories — delegates to Retrofit
    // =========================================================================

    @Test
    fun `when fetchCategories is called, then it delegates to the retrofit service`() =
        runTest {
            val dtos = listOf(
                CategoryDto(id = "cat1", title = "Android", description = "Android", questionUrl = "https://example.com/android")
            )
            coEvery { retrofitService.fetchCategories() } returns dtos
            apiService.fetchCategories()
            coVerify(exactly = 1) { retrofitService.fetchCategories() }
        }

    @Test
    fun `given retrofit returns categories, when fetchCategories called, then same list returned`() =
        runTest {
            val expected = listOf(
                CategoryDto(id = "cat1", title = "Android", description = "Android", questionUrl = "https://example.com/android"),
                CategoryDto(id = "cat2", title = "Kotlin", description = "Kotlin", questionUrl = "https://example.com/kotlin")
            )
            coEvery { retrofitService.fetchCategories() } returns expected
            val result = apiService.fetchCategories()
            assertEquals(expected, result)
        }

    @Test
    fun `given retrofit returns empty list, when fetchCategories called, then empty list returned`() =
        runTest {
            coEvery { retrofitService.fetchCategories() } returns emptyList()
            val result = apiService.fetchCategories()
            assertTrue(result.isEmpty())
        }

    @Test(expected = RuntimeException::class)
    fun `given retrofit throws, when fetchCategories called, then exception propagates`() =
        runTest {
            coEvery { retrofitService.fetchCategories() } throws RuntimeException("network error")
            apiService.fetchCategories()
        }

    // =========================================================================
    // fetchQuestions — delegates to OkHttpClient
    // =========================================================================

    private fun stubOkHttpResponse(jsonBody: String) {
        every { okHttpClient.newCall(any()) } returns call
        every { call.execute() } returns response
        every { response.body } returns jsonBody.toResponseBody()
    }

    @Test
    fun `given valid JSON response, when fetchQuestions called, then questions are parsed`() =
        runTest {
            stubOkHttpResponse(
                """[{"id":1,"question":"Q1","options":["A","B","C","D"],"correctOptionIndex":0}]"""
            )
            val result = apiService.fetchQuestions(testUrl)
            assertEquals(1, result.size)
            assertEquals(1, result[0].id)
            assertEquals("Q1", result[0].question)
            assertEquals(0, result[0].correctOptionIndex)
        }

    @Test
    fun `given multiple questions in JSON, when fetchQuestions called, then all are returned`() =
        runTest {
            stubOkHttpResponse(
                """[
                  {"id":1,"question":"Q1","options":["A","B"],"correctOptionIndex":0},
                  {"id":2,"question":"Q2","options":["C","D"],"correctOptionIndex":1}
                ]"""
            )
            val result = apiService.fetchQuestions(testUrl)
            assertEquals(2, result.size)
        }

    @Test
    fun `given fetchQuestions called, then OkHttpClient called with correct url`() =
        runTest {
            stubOkHttpResponse("[]")
            apiService.fetchQuestions(testUrl)
            // Verify the url was used in the request
            every { okHttpClient.newCall(match { it.url.toString() == testUrl }) } returns call
        }

    @Test
    fun `given empty JSON array response, when fetchQuestions called, then empty list returned`() =
        runTest {
            stubOkHttpResponse("[]")
            val result = apiService.fetchQuestions(testUrl)
            assertTrue(result.isEmpty())
        }

    @Test
    fun `given null response body, when fetchQuestions called, then empty list returned`() =
        runTest {
            every { okHttpClient.newCall(any()) } returns call
            every { call.execute() } returns response
            every { response.body } returns null
            val result = apiService.fetchQuestions(testUrl)
            assertTrue(result.isEmpty())
        }

    @Test(expected = Exception::class)
    fun `given OkHttpClient throws IOException, when fetchQuestions called, then exception propagates`() =
        runTest {
            every { okHttpClient.newCall(any()) } returns call
            every { call.execute() } throws java.io.IOException("connection failed")
            apiService.fetchQuestions(testUrl)
        }
}
