package com.assignment.mcqquiz.data.infra.repository

import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.infra.db.CategoryProgressDao
import com.assignment.mcqquiz.data.infra.db.CategoryProgressEntity
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [CategoryProgressRepositoryImpl].
 *
 * Verifies:
 *  - upsert reads existing row first so allTimeLongestStreak is preserved.
 *  - upsert always resets currentStreak to 0.
 *  - Targeted updates (updateLastQuestionId, updateStreaks) delegate directly to the DAO.
 *  - getById maps entity → snapshot correctly for all [CategoryStatus] values.
 *  - getAll maps a list of entities to snapshots.
 *  - null lastQuestionId is preserved through the mapping.
 */
class CategoryProgressRepositoryImplTest {

    private val dao: CategoryProgressDao = mockk()
    private lateinit var repository: CategoryProgressRepositoryImpl

    private val categoryId = "cat-android"

    @Before
    fun setUp() {
        repository = CategoryProgressRepositoryImpl(dao)
    }

    // =========================================================================
    // upsert
    // =========================================================================

    @Test
    fun `given new category, when upsert called, then dao upsert called with correct status`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns null
            coJustRun { dao.upsert(any()) }
            repository.upsert(categoryId, CategoryStatus.IN_PROGRESS, 1)
            coVerify {
                dao.upsert(match { it.status == "IN_PROGRESS" })
            }
        }

    @Test
    fun `given new category, when upsert called, then allTimeLongestStreak is 0`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns null
            coJustRun { dao.upsert(any()) }
            repository.upsert(categoryId, CategoryStatus.IN_PROGRESS, 1)
            coVerify { dao.upsert(match { it.allTimeLongestStreak == 0 }) }
        }

    @Test
    fun `given new category, when upsert called, then currentStreak is reset to 0`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns null
            coJustRun { dao.upsert(any()) }
            repository.upsert(categoryId, CategoryStatus.IN_PROGRESS, 1)
            coVerify { dao.upsert(match { it.currentStreak == 0 }) }
        }

    @Test
    fun `given new category, when upsert called, then lastQuestionId is preserved`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns null
            coJustRun { dao.upsert(any()) }
            repository.upsert(categoryId, CategoryStatus.IN_PROGRESS, 42)
            coVerify { dao.upsert(match { it.lastQuestionId == 42 }) }
        }

    @Test
    fun `given existing category with allTimeLongest 5, when upsert called, then allTimeLongestStreak preserved`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns CategoryProgressEntity(
                categoryId = categoryId,
                status = "COMPLETED",
                currentStreak = 3,
                allTimeLongestStreak = 5
            )
            coJustRun { dao.upsert(any()) }
            repository.upsert(categoryId, CategoryStatus.IN_PROGRESS, 1)
            coVerify { dao.upsert(match { it.allTimeLongestStreak == 5 }) }
        }

    @Test
    fun `given existing category with allTimeLongest 5, when upsert called, then currentStreak reset to 0`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns CategoryProgressEntity(
                categoryId = categoryId,
                status = "IN_PROGRESS",
                currentStreak = 4,
                allTimeLongestStreak = 4
            )
            coJustRun { dao.upsert(any()) }
            repository.upsert(categoryId, CategoryStatus.IN_PROGRESS, 2)
            coVerify { dao.upsert(match { it.currentStreak == 0 }) }
        }

    @Test
    fun `given upsert called, then dao getById is called first to read existing record`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns null
            coJustRun { dao.upsert(any()) }
            repository.upsert(categoryId, CategoryStatus.NOT_STARTED, null)
            coVerify { dao.getById(categoryId) }
        }

    @Test
    fun `given upsert called, then dao upsert is called with correct categoryId`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns null
            coJustRun { dao.upsert(any()) }
            repository.upsert(categoryId, CategoryStatus.IN_PROGRESS, null)
            coVerify { dao.upsert(match { it.categoryId == categoryId }) }
        }

    @Test
    fun `given null lastQuestionId, when upsert called, then entity has null lastQuestionId`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns null
            coJustRun { dao.upsert(any()) }
            repository.upsert(categoryId, CategoryStatus.COMPLETED, null)
            coVerify { dao.upsert(match { it.lastQuestionId == null }) }
        }

    // =========================================================================
    // updateLastQuestionId
    // =========================================================================

    @Test
    fun `given updateLastQuestionId called, then delegates to dao with correct params`() =
        runTest {
            coJustRun { dao.updateLastQuestionId(any(), any()) }
            repository.updateLastQuestionId(categoryId, 3)
            coVerify(exactly = 1) { dao.updateLastQuestionId(categoryId, 3) }
        }

    @Test
    fun `given updateLastQuestionId called, then dao called exactly once`() =
        runTest {
            coJustRun { dao.updateLastQuestionId(any(), any()) }
            repository.updateLastQuestionId(categoryId, 7)
            coVerify(exactly = 1) { dao.updateLastQuestionId(any(), any()) }
        }

    // =========================================================================
    // updateStreaks
    // =========================================================================

    @Test
    fun `given updateStreaks called, then delegates to dao with correct params`() =
        runTest {
            coJustRun { dao.updateStreaks(any(), any(), any()) }
            repository.updateStreaks(categoryId, 4, 8)
            coVerify { dao.updateStreaks(categoryId, 4, 8) }
        }

    @Test
    fun `given updateStreaks called with zeros, then delegates zeros to dao`() =
        runTest {
            coJustRun { dao.updateStreaks(any(), any(), any()) }
            repository.updateStreaks(categoryId, 0, 0)
            coVerify { dao.updateStreaks(categoryId, 0, 0) }
        }

    // =========================================================================
    // getById
    // =========================================================================

    @Test
    fun `given entity exists, when getById called, then snapshot returned with correct categoryId`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns CategoryProgressEntity(
                categoryId = categoryId,
                status = "IN_PROGRESS"
            )
            val result = repository.getById(categoryId)
            assertEquals(categoryId, result?.categoryId)
        }

    @Test
    fun `given IN_PROGRESS entity, when getById called, then status maps to IN_PROGRESS`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns CategoryProgressEntity(
                categoryId = categoryId,
                status = "IN_PROGRESS"
            )
            val result = repository.getById(categoryId)
            assertEquals(CategoryStatus.IN_PROGRESS, result?.status)
        }

    @Test
    fun `given COMPLETED entity, when getById called, then status maps to COMPLETED`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns CategoryProgressEntity(
                categoryId = categoryId,
                status = "COMPLETED"
            )
            val result = repository.getById(categoryId)
            assertEquals(CategoryStatus.COMPLETED, result?.status)
        }

    @Test
    fun `given NOT_STARTED entity, when getById called, then status maps to NOT_STARTED`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns CategoryProgressEntity(
                categoryId = categoryId,
                status = "NOT_STARTED"
            )
            val result = repository.getById(categoryId)
            assertEquals(CategoryStatus.NOT_STARTED, result?.status)
        }

    @Test
    fun `given entity with streak values, when getById called, then streak fields are preserved`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns CategoryProgressEntity(
                categoryId = categoryId,
                status = "IN_PROGRESS",
                lastQuestionId = 2,
                currentStreak = 1,
                allTimeLongestStreak = 3
            )
            val result = repository.getById(categoryId)
            assertEquals(2, result?.lastQuestionId)
            assertEquals(1, result?.currentStreak)
            assertEquals(3, result?.allTimeLongestStreak)
        }

    @Test
    fun `given entity with null lastQuestionId, when getById called, then null is preserved`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns CategoryProgressEntity(
                categoryId = categoryId,
                status = "NOT_STARTED",
                lastQuestionId = null
            )
            val result = repository.getById(categoryId)
            assertNull(result?.lastQuestionId)
        }

    @Test
    fun `given no entity, when getById called, then null returned`() =
        runTest {
            coEvery { dao.getById(categoryId) } returns null
            val result = repository.getById(categoryId)
            assertNull(result)
        }

    // =========================================================================
    // getAll
    // =========================================================================

    @Test
    fun `given two entities, when getAll called, then two snapshots returned`() =
        runTest {
            coEvery { dao.getAll() } returns listOf(
                CategoryProgressEntity(categoryId = "cat1", status = "IN_PROGRESS", currentStreak = 2, allTimeLongestStreak = 4),
                CategoryProgressEntity(categoryId = "cat2", status = "COMPLETED", currentStreak = 0, allTimeLongestStreak = 6)
            )
            val result = repository.getAll()
            assertEquals(2, result.size)
        }

    @Test
    fun `given two entities, when getAll called, then categoryIds are preserved in order`() =
        runTest {
            coEvery { dao.getAll() } returns listOf(
                CategoryProgressEntity(categoryId = "cat1", status = "IN_PROGRESS"),
                CategoryProgressEntity(categoryId = "cat2", status = "COMPLETED")
            )
            val result = repository.getAll()
            assertEquals("cat1", result[0].categoryId)
            assertEquals("cat2", result[1].categoryId)
        }

    @Test
    fun `given two entities, when getAll called, then statuses are mapped correctly`() =
        runTest {
            coEvery { dao.getAll() } returns listOf(
                CategoryProgressEntity(categoryId = "cat1", status = "IN_PROGRESS", allTimeLongestStreak = 4),
                CategoryProgressEntity(categoryId = "cat2", status = "COMPLETED", allTimeLongestStreak = 6)
            )
            val result = repository.getAll()
            assertEquals(CategoryStatus.IN_PROGRESS, result[0].status)
            assertEquals(CategoryStatus.COMPLETED, result[1].status)
        }

    @Test
    fun `given two entities, when getAll called, then allTimeLongestStreak fields are preserved`() =
        runTest {
            coEvery { dao.getAll() } returns listOf(
                CategoryProgressEntity(categoryId = "cat1", status = "IN_PROGRESS", allTimeLongestStreak = 4),
                CategoryProgressEntity(categoryId = "cat2", status = "COMPLETED", allTimeLongestStreak = 6)
            )
            val result = repository.getAll()
            assertEquals(4, result[0].allTimeLongestStreak)
            assertEquals(6, result[1].allTimeLongestStreak)
        }

    @Test
    fun `given empty database, when getAll called, then empty list returned`() =
        runTest {
            coEvery { dao.getAll() } returns emptyList()
            val result = repository.getAll()
            assertTrue(result.isEmpty())
        }
}

