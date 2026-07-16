package com.assignment.mcqquiz.feature.quiz.ui.viewmodel

import app.cash.turbine.test
import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.model.QuizCategory
import com.assignment.mcqquiz.data.domain.service.CategoryProgressSnapshot
import com.assignment.mcqquiz.data.domain.service.CategoryService
import com.assignment.mcqquiz.feature.quiz.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [CategoryListViewModel].
 *
 * Covers:
 *  - InitialLoad success and error paths
 *  - Progress merging (NOT_STARTED / IN_PROGRESS / COMPLETED)
 *  - Idempotency guard (InitialLoad fires loadCategories only once)
 *  - Retry re-fires loadCategories
 *  - CategorySelected emits NavigateToQuiz with correct args
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CategoryListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val categoryService: CategoryService = mockk()

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private val sampleCategories = listOf(
        QuizCategory(id = "cat1", title = "Android", description = "Android questions", questionUrl = "https://example.com/android"),
        QuizCategory(id = "cat2", title = "Kotlin", description = "Kotlin questions", questionUrl = "https://example.com/kotlin")
    )

    @Before
    fun setUp() {
        coEvery { categoryService.loadCategories() } returns sampleCategories
        coEvery { categoryService.getAllProgress() } returns emptyList()
    }

    private fun createViewModel() = CategoryListViewModel(categoryService)

    // =========================================================================
    // 1. InitialLoad — Happy Path
    // =========================================================================

    @Test
    fun `given InitialLoad fired, when loading starts, then ShowLoader is first effect`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.effects.test {
                vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
                assertEquals(CategoryListViewModel.Effect.ShowLoader, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given categories returned, when InitialLoad completes, then ShowList effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(CategoryListViewModel.Effect.ShowList, vm.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given categories returned, when InitialLoad completes, then uiState has correct count`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(2, vm.uiState.value.categories.size)
        }

    @Test
    fun `given categories returned, when InitialLoad completes, then category titles are preserved`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            val titles = vm.uiState.value.categories.map { it.category.title }
            assertEquals(listOf("Android", "Kotlin"), titles)
        }

    @Test
    fun `given effects sequence, when loading completes, then order is ShowLoader then ShowList`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.effects.test {
                vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
                assertEquals(CategoryListViewModel.Effect.ShowLoader, awaitItem())
                advanceUntilIdle()
                assertEquals(CategoryListViewModel.Effect.ShowList, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    // ─── Progress merging ─────────────────────────────────────────────────────

    @Test
    fun `given no progress saved, when categories load, then all categories have NOT_STARTED status`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.getAllProgress() } returns emptyList()
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertTrue(vm.uiState.value.categories.all { it.status == CategoryStatus.NOT_STARTED })
        }

    @Test
    fun `given one category COMPLETED, when categories load, then that category has COMPLETED status`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.getAllProgress() } returns listOf(
                CategoryProgressSnapshot(categoryId = "cat1", status = CategoryStatus.COMPLETED)
            )
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            val cat1 = vm.uiState.value.categories.first { it.category.id == "cat1" }
            assertEquals(CategoryStatus.COMPLETED, cat1.status)
        }

    @Test
    fun `given one category COMPLETED, when categories load, then other category stays NOT_STARTED`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.getAllProgress() } returns listOf(
                CategoryProgressSnapshot(categoryId = "cat1", status = CategoryStatus.COMPLETED)
            )
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            val cat2 = vm.uiState.value.categories.first { it.category.id == "cat2" }
            assertEquals(CategoryStatus.NOT_STARTED, cat2.status)
        }

    @Test
    fun `given category IN_PROGRESS, when categories load, then that category has IN_PROGRESS status`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.getAllProgress() } returns listOf(
                CategoryProgressSnapshot(categoryId = "cat2", status = CategoryStatus.IN_PROGRESS)
            )
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            val cat2 = vm.uiState.value.categories.first { it.category.id == "cat2" }
            assertEquals(CategoryStatus.IN_PROGRESS, cat2.status)
        }

    @Test
    fun `given all categories have progress, when categories load, then all statuses are merged`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.getAllProgress() } returns listOf(
                CategoryProgressSnapshot(categoryId = "cat1", status = CategoryStatus.IN_PROGRESS),
                CategoryProgressSnapshot(categoryId = "cat2", status = CategoryStatus.COMPLETED)
            )
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            val categories = vm.uiState.value.categories
            assertEquals(CategoryStatus.IN_PROGRESS, categories.first { it.category.id == "cat1" }.status)
            assertEquals(CategoryStatus.COMPLETED, categories.first { it.category.id == "cat2" }.status)
        }

    // ─── Idempotency ──────────────────────────────────────────────────────────

    @Test
    fun `given InitialLoad already fired, when fired again, then loadCategories called only once`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            coVerify(exactly = 1) { categoryService.loadCategories() }
        }

    // =========================================================================
    // 2. InitialLoad — Error Paths
    // =========================================================================

    @Test
    fun `given loadCategories throws, when InitialLoad fires, then ShowError effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.loadCategories() } throws RuntimeException("network error")
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(CategoryListViewModel.Effect.ShowError, vm.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given loadCategories returns empty list, when InitialLoad fires, then ShowError effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.loadCategories() } returns emptyList()
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(CategoryListViewModel.Effect.ShowError, vm.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given loadCategories throws, when InitialLoad fails, then categories list stays empty`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.loadCategories() } throws RuntimeException("error")
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertTrue(vm.uiState.value.categories.isEmpty())
        }

    @Test
    fun `given getAllProgress throws, when InitialLoad fires, then ShowError effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.getAllProgress() } throws RuntimeException("db error")
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(CategoryListViewModel.Effect.ShowError, vm.effects.replayCache.firstOrNull())
        }

    // =========================================================================
    // 3. Retry
    // =========================================================================

    @Test
    fun `given error state, when Retry fired, then loadCategories is called again`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.loadCategories() } throws RuntimeException("error")
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            coEvery { categoryService.loadCategories() } returns sampleCategories
            vm.handleEvent(CategoryListViewModel.Event.Retry)
            advanceUntilIdle()
            coVerify(exactly = 2) { categoryService.loadCategories() }
        }

    @Test
    fun `given error state, when Retry succeeds, then ShowList effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.loadCategories() } throws RuntimeException("error")
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            coEvery { categoryService.loadCategories() } returns sampleCategories
            vm.handleEvent(CategoryListViewModel.Event.Retry)
            advanceUntilIdle()
            assertEquals(CategoryListViewModel.Effect.ShowList, vm.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given error state, when Retry succeeds, then ShowLoader emitted before ShowList`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { categoryService.loadCategories() } throws RuntimeException("error")
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            coEvery { categoryService.loadCategories() } returns sampleCategories
            vm.effects.test {
                awaitItem() // consume replayed ShowError
                vm.handleEvent(CategoryListViewModel.Event.Retry)
                assertEquals(CategoryListViewModel.Effect.ShowLoader, awaitItem())
                advanceUntilIdle()
                assertEquals(CategoryListViewModel.Effect.ShowList, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    // =========================================================================
    // 4. CategorySelected
    // =========================================================================

    @Test
    fun `given loaded categories, when CategorySelected fired, then NavigateToQuiz effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.effects.test {
                awaitItem() // consume replayed ShowList
                vm.handleEvent(
                    CategoryListViewModel.Event.CategorySelected("cat1", "https://example.com/android")
                )
                advanceUntilIdle()
                val effect = awaitItem()
                assertTrue(effect is CategoryListViewModel.Effect.NavigateToQuiz)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given CategorySelected fired, then NavigateToQuiz carries correct categoryId`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.effects.test {
                awaitItem() // consume replayed ShowList
                vm.handleEvent(
                    CategoryListViewModel.Event.CategorySelected("cat2", "https://example.com/kotlin")
                )
                advanceUntilIdle()
                val effect = awaitItem() as CategoryListViewModel.Effect.NavigateToQuiz
                assertEquals("cat2", effect.categoryId)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given CategorySelected fired, then NavigateToQuiz carries correct questionUrl`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(CategoryListViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.effects.test {
                awaitItem() // consume replayed ShowList
                vm.handleEvent(
                    CategoryListViewModel.Event.CategorySelected("cat1", "https://example.com/android")
                )
                advanceUntilIdle()
                val effect = awaitItem() as CategoryListViewModel.Effect.NavigateToQuiz
                assertEquals("https://example.com/android", effect.questionUrl)
                cancelAndIgnoreRemainingEvents()
            }
        }
}

