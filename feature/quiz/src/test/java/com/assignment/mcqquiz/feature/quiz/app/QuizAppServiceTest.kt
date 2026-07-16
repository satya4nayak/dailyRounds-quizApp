package com.assignment.mcqquiz.feature.quiz.app

import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.domain.service.CategoryProgressSnapshot
import com.assignment.mcqquiz.data.infra.repository.CategoryProgressRepository
import com.assignment.mcqquiz.data.infra.repository.QuestionRepository
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [QuizAppService].
 *
 * [QuizAppService] implements [com.assignment.mcqquiz.data.domain.service.QuizService]
 * by delegating to [QuestionRepository] and [CategoryProgressRepository].
 *
 * Tests verify correct delegation, transparent pass-through, and exception propagation.
 * [UnconfinedTestDispatcher] is used so withContext() in the service runs eagerly.
 */
class QuizAppServiceTest {

    private val questionRepo: QuestionRepository = mockk()
    private val categoryProgressRepository: CategoryProgressRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var service: QuizAppService

    private val url = "https://example.com/questions.json"
    private val categoryId = "cat-android"

    private val sampleQuestions = listOf(
        Question(id = 1, question = "Q1", options = listOf("A", "B", "C", "D"), correctOptionIndex = 0),
        Question(id = 2, question = "Q2", options = listOf("A", "B", "C", "D"), correctOptionIndex = 1),
        Question(id = 3, question = "Q3", options = listOf("A", "B", "C", "D"), correctOptionIndex = 2)
    )

    @Before
    fun setUp() {
        service = QuizAppService(questionRepo, categoryProgressRepository, testDispatcher)
    }

    // =========================================================================
    // loadQuestions
    // =========================================================================

    @Test
    fun `given repository returns questions, when loadQuestions called, then same list returned`() =
        runTest {
            coEvery { questionRepo.getQuestions(url) } returns sampleQuestions
            val result = service.loadQuestions(url)
            assertEquals(sampleQuestions, result)
        }

    @Test
    fun `given loadQuestions called, then repository is called with the exact url`() =
        runTest {
            coEvery { questionRepo.getQuestions(url) } returns sampleQuestions
            service.loadQuestions(url)
            coVerify(exactly = 1) { questionRepo.getQuestions(url) }
        }

    @Test
    fun `given repository returns 3 questions, when loadQuestions called, then result has 3 questions`() =
        runTest {
            coEvery { questionRepo.getQuestions(url) } returns sampleQuestions
            val result = service.loadQuestions(url)
            assertEquals(3, result.size)
        }

    @Test
    fun `given repository returns questions, when loadQuestions called, then question ids preserved`() =
        runTest {
            coEvery { questionRepo.getQuestions(url) } returns sampleQuestions
            val result = service.loadQuestions(url)
            assertEquals(listOf(1, 2, 3), result.map { it.id })
        }

    @Test
    fun `given repository returns empty list, when loadQuestions called, then empty list returned`() =
        runTest {
            coEvery { questionRepo.getQuestions(url) } returns emptyList()
            val result = service.loadQuestions(url)
            assertTrue(result.isEmpty())
        }

    @Test(expected = RuntimeException::class)
    fun `given repository throws RuntimeException, when loadQuestions called, then exception propagates`() =
        runTest {
            coEvery { questionRepo.getQuestions(url) } throws RuntimeException("network failure")
            service.loadQuestions(url)
        }

    @Test(expected = IllegalStateException::class)
    fun `given repository throws IllegalStateException, when loadQuestions called, then exception propagates`() =
        runTest {
            coEvery { questionRepo.getQuestions(url) } throws IllegalStateException("state error")
            service.loadQuestions(url)
        }

    @Test
    fun `given repository throws, when loadQuestions fails, then repository still called once`() =
        runTest {
            coEvery { questionRepo.getQuestions(url) } throws RuntimeException("boom")
            runCatching { service.loadQuestions(url) }
            coVerify(exactly = 1) { questionRepo.getQuestions(url) }
        }

    // =========================================================================
    // saveProgress
    // =========================================================================

    @Test
    fun `given saveProgress called with IN_PROGRESS, then delegates to categoryProgressRepository`() =
        runTest {
            coJustRun { categoryProgressRepository.upsert(any(), any(), any()) }
            service.saveProgress(categoryId, CategoryStatus.IN_PROGRESS, 3)
            coVerify { categoryProgressRepository.upsert(categoryId, CategoryStatus.IN_PROGRESS, 3) }
        }

    @Test
    fun `given saveProgress called with COMPLETED and null id, then delegates with null lastQuestionId`() =
        runTest {
            coJustRun { categoryProgressRepository.upsert(any(), any(), any()) }
            service.saveProgress(categoryId, CategoryStatus.COMPLETED, null)
            coVerify { categoryProgressRepository.upsert(categoryId, CategoryStatus.COMPLETED, null) }
        }

    @Test
    fun `given saveProgress called, then categoryProgressRepository upsert called exactly once`() =
        runTest {
            coJustRun { categoryProgressRepository.upsert(any(), any(), any()) }
            service.saveProgress(categoryId, CategoryStatus.IN_PROGRESS, 1)
            coVerify(exactly = 1) { categoryProgressRepository.upsert(any(), any(), any()) }
        }

    // =========================================================================
    // updateLastQuestionId
    // =========================================================================

    @Test
    fun `given updateLastQuestionId called, then delegates to categoryProgressRepository`() =
        runTest {
            coJustRun { categoryProgressRepository.updateLastQuestionId(any(), any()) }
            service.updateLastQuestionId(categoryId, 5)
            coVerify { categoryProgressRepository.updateLastQuestionId(categoryId, 5) }
        }

    @Test
    fun `given updateLastQuestionId called, then called exactly once`() =
        runTest {
            coJustRun { categoryProgressRepository.updateLastQuestionId(any(), any()) }
            service.updateLastQuestionId(categoryId, 7)
            coVerify(exactly = 1) { categoryProgressRepository.updateLastQuestionId(any(), any()) }
        }

    // =========================================================================
    // updateStreaks
    // =========================================================================

    @Test
    fun `given updateStreaks called, then delegates to categoryProgressRepository with correct values`() =
        runTest {
            coJustRun { categoryProgressRepository.updateStreaks(any(), any(), any()) }
            service.updateStreaks(categoryId, 4, 8)
            coVerify { categoryProgressRepository.updateStreaks(categoryId, 4, 8) }
        }

    @Test
    fun `given updateStreaks called with zeros, then delegates zeros to repository`() =
        runTest {
            coJustRun { categoryProgressRepository.updateStreaks(any(), any(), any()) }
            service.updateStreaks(categoryId, 0, 0)
            coVerify { categoryProgressRepository.updateStreaks(categoryId, 0, 0) }
        }

    // =========================================================================
    // getProgress
    // =========================================================================

    @Test
    fun `given progress exists, when getProgress called, then snapshot returned`() =
        runTest {
            val snapshot = CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.IN_PROGRESS,
                lastQuestionId = 3,
                currentStreak = 2,
                allTimeLongestStreak = 4
            )
            coEvery { categoryProgressRepository.getById(categoryId) } returns snapshot
            val result = service.getProgress(categoryId)
            assertEquals(snapshot, result)
        }

    @Test
    fun `given no progress, when getProgress called, then null returned`() =
        runTest {
            coEvery { categoryProgressRepository.getById(categoryId) } returns null
            val result = service.getProgress(categoryId)
            assertNull(result)
        }

    @Test
    fun `given getProgress called, then categoryProgressRepository getById called exactly once`() =
        runTest {
            coEvery { categoryProgressRepository.getById(categoryId) } returns null
            service.getProgress(categoryId)
            coVerify(exactly = 1) { categoryProgressRepository.getById(categoryId) }
        }

    @Test
    fun `given getProgress called, then snapshot fields are preserved`() =
        runTest {
            val snapshot = CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.COMPLETED,
                lastQuestionId = null,
                currentStreak = 0,
                allTimeLongestStreak = 7
            )
            coEvery { categoryProgressRepository.getById(categoryId) } returns snapshot
            val result = service.getProgress(categoryId)
            assertEquals(CategoryStatus.COMPLETED, result?.status)
            assertEquals(7, result?.allTimeLongestStreak)
            assertNull(result?.lastQuestionId)
        }
}

