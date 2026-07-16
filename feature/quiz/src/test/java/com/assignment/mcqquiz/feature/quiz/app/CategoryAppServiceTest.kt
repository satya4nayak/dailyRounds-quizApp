package com.assignment.mcqquiz.feature.quiz.app

import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.model.QuizCategory
import com.assignment.mcqquiz.data.domain.service.CategoryProgressSnapshot
import com.assignment.mcqquiz.data.infra.repository.CategoryProgressRepository
import com.assignment.mcqquiz.data.infra.repository.QuestionRepository
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [CategoryAppService].
 *
 * [CategoryAppService] implements [com.assignment.mcqquiz.data.domain.service.CategoryService]
 * by delegating to [QuestionRepository] and [CategoryProgressRepository].
 *
 * Key behaviour verified:
 *  - loadCategories returns and caches the repository result.
 *  - Cache is NOT updated on empty responses (so next call retries the network).
 *  - getAllProgress / getCategoryProgress / saveProgress delegate correctly.
 */
class CategoryAppServiceTest {

    private val questionRepository: QuestionRepository = mockk()
    private val categoryProgressRepository: CategoryProgressRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var service: CategoryAppService

    private val categoryId = "cat-android"

    private val sampleCategories = listOf(
        QuizCategory(
            id = "cat1",
            title = "Android",
            description = "Android questions",
            questionUrl = "https://example.com/android"
        ),
        QuizCategory(
            id = "cat2",
            title = "Kotlin",
            description = "Kotlin questions",
            questionUrl = "https://example.com/kotlin"
        )
    )

    @Before
    fun setUp() {
        service = CategoryAppService(questionRepository, categoryProgressRepository, testDispatcher)
    }

    // =========================================================================
    // loadCategories
    // =========================================================================

    @Test
    fun `given repository returns categories, when loadCategories called, then same list returned`() =
        runTest {
            coEvery { questionRepository.getCategories() } returns sampleCategories
            val result = service.loadCategories()
            assertEquals(sampleCategories, result)
        }

    @Test
    fun `given repository returns 2 categories, when loadCategories called, then result has 2 items`() =
        runTest {
            coEvery { questionRepository.getCategories() } returns sampleCategories
            val result = service.loadCategories()
            assertEquals(2, result.size)
        }

    @Test
    fun `given repository returns categories, when loadCategories called, then category titles preserved`() =
        runTest {
            coEvery { questionRepository.getCategories() } returns sampleCategories
            val result = service.loadCategories()
            assertEquals(listOf("Android", "Kotlin"), result.map { it.title })
        }

    @Test
    fun `given repository returns empty, when loadCategories called, then empty list returned`() =
        runTest {
            coEvery { questionRepository.getCategories() } returns emptyList()
            val result = service.loadCategories()
            assertTrue(result.isEmpty())
        }

    @Test(expected = RuntimeException::class)
    fun `given repository throws, when loadCategories called, then exception propagates`() =
        runTest {
            coEvery { questionRepository.getCategories() } throws RuntimeException("network error")
            service.loadCategories()
        }

    // ─── In-memory cache ──────────────────────────────────────────────────────

    @Test
    fun `given first call succeeds, when loadCategories called a second time, then repository called only once`() =
        runTest {
            coEvery { questionRepository.getCategories() } returns sampleCategories
            service.loadCategories()
            service.loadCategories()
            coVerify(exactly = 1) { questionRepository.getCategories() }
        }

    @Test
    fun `given first call succeeds, when loadCategories called again, then cached list is returned`() =
        runTest {
            coEvery { questionRepository.getCategories() } returns sampleCategories
            service.loadCategories()
            val second = service.loadCategories()
            assertEquals(sampleCategories, second)
        }

    @Test
    fun `given repository returns empty, when loadCategories called twice, then repository called twice`() =
        runTest {
            // Empty result must NOT be cached — next call should retry the network.
            coEvery { questionRepository.getCategories() } returns emptyList()
            service.loadCategories()
            service.loadCategories()
            coVerify(exactly = 2) { questionRepository.getCategories() }
        }

    // =========================================================================
    // getCategoryProgress
    // =========================================================================

    @Test
    fun `given progress exists, when getCategoryProgress called, then snapshot returned`() =
        runTest {
            val snapshot = CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.IN_PROGRESS,
                currentStreak = 2,
                allTimeLongestStreak = 5
            )
            coEvery { categoryProgressRepository.getById(categoryId) } returns snapshot
            val result = service.getCategoryProgress(categoryId)
            assertEquals(snapshot, result)
        }

    @Test
    fun `given no progress, when getCategoryProgress called, then null returned`() =
        runTest {
            coEvery { categoryProgressRepository.getById(categoryId) } returns null
            val result = service.getCategoryProgress(categoryId)
            assertNull(result)
        }

    @Test
    fun `given getCategoryProgress called, then delegates to categoryProgressRepository`() =
        runTest {
            coEvery { categoryProgressRepository.getById(categoryId) } returns null
            service.getCategoryProgress(categoryId)
            coVerify(exactly = 1) { categoryProgressRepository.getById(categoryId) }
        }

    // =========================================================================
    // getAllProgress
    // =========================================================================

    @Test
    fun `given progress list exists, when getAllProgress called, then full list returned`() =
        runTest {
            val snapshots = listOf(
                CategoryProgressSnapshot(categoryId = "cat1", status = CategoryStatus.IN_PROGRESS),
                CategoryProgressSnapshot(categoryId = "cat2", status = CategoryStatus.COMPLETED)
            )
            coEvery { categoryProgressRepository.getAll() } returns snapshots
            val result = service.getAllProgress()
            assertEquals(snapshots, result)
        }

    @Test
    fun `given no progress, when getAllProgress called, then empty list returned`() =
        runTest {
            coEvery { categoryProgressRepository.getAll() } returns emptyList()
            val result = service.getAllProgress()
            assertTrue(result.isEmpty())
        }

    @Test
    fun `given getAllProgress called, then delegates to categoryProgressRepository`() =
        runTest {
            coEvery { categoryProgressRepository.getAll() } returns emptyList()
            service.getAllProgress()
            coVerify(exactly = 1) { categoryProgressRepository.getAll() }
        }

    @Test
    fun `given 2 snapshots, when getAllProgress called, then result has 2 items`() =
        runTest {
            coEvery { categoryProgressRepository.getAll() } returns listOf(
                CategoryProgressSnapshot(categoryId = "cat1", status = CategoryStatus.NOT_STARTED),
                CategoryProgressSnapshot(categoryId = "cat2", status = CategoryStatus.COMPLETED)
            )
            val result = service.getAllProgress()
            assertEquals(2, result.size)
        }

    // =========================================================================
    // saveProgress
    // =========================================================================

    @Test
    fun `given saveProgress called, then delegates to categoryProgressRepository`() =
        runTest {
            coJustRun { categoryProgressRepository.upsert(any(), any(), any()) }
            service.saveProgress(categoryId, CategoryStatus.IN_PROGRESS, 3)
            coVerify { categoryProgressRepository.upsert(categoryId, CategoryStatus.IN_PROGRESS, 3) }
        }

    @Test
    fun `given saveProgress called with COMPLETED and no id, then delegates null lastQuestionId`() =
        runTest {
            coJustRun { categoryProgressRepository.upsert(any(), any(), any()) }
            service.saveProgress(categoryId, CategoryStatus.COMPLETED, null)
            coVerify { categoryProgressRepository.upsert(categoryId, CategoryStatus.COMPLETED, null) }
        }

    @Test
    fun `given saveProgress called, then upsert called exactly once`() =
        runTest {
            coJustRun { categoryProgressRepository.upsert(any(), any(), any()) }
            service.saveProgress(categoryId, CategoryStatus.NOT_STARTED, null)
            coVerify(exactly = 1) { categoryProgressRepository.upsert(any(), any(), any()) }
        }
}


