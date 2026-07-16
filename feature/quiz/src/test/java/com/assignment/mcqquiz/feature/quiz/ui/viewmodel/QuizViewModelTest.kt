package com.assignment.mcqquiz.feature.quiz.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.domain.service.CategoryProgressSnapshot
import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.feature.quiz.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [QuizViewModel].
 *
 * The ViewModel coordinates quiz session state via MVI:
 *  - [QuizViewModel.Event] drives all state changes.
 *  - [QuizViewModel.Effect] drives one-shot navigation and UI side-effects.
 *  - [QuizViewModel.uiState] is the observable session state.
 *
 * [QuizService] is mocked so tests are pure JVM with no I/O.
 * [MainDispatcherRule] replaces Dispatchers.Main with a test dispatcher so
 * viewModelScope coroutines run under virtual time.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val quizService: QuizService = mockk()

    private val categoryId = "cat-android"
    private val questionUrl = "https://example.com/questions.json"

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private fun buildQuestions(count: Int = 5): List<Question> = (0 until count).map { i ->
        Question(
            id = i + 1,
            question = "Question ${i + 1}",
            options = listOf("Option A", "Option B", "Option C", "Option D"),
            correctOptionIndex = 0  // Option A is always the correct answer
        )
    }

    private fun buildSavedStateHandle(
        catId: String = categoryId,
        url: String = questionUrl
    ) = SavedStateHandle(
        mapOf(
            QuizViewModel.KEY_CATEGORY_ID to catId,
            QuizViewModel.KEY_QUESTION_URL to url
        )
    )

    private fun createViewModel(
        catId: String = categoryId,
        url: String = questionUrl
    ) = QuizViewModel(
        savedStateHandle = buildSavedStateHandle(catId, url),
        quizService = quizService
    )

    @Before
    fun setUp() {
        // Sensible defaults — individual tests override as needed.
        coEvery { quizService.loadQuestions(any()) } returns buildQuestions()
        coEvery { quizService.getProgress(any()) } returns null
        coJustRun { quizService.saveProgress(any(), any(), any()) }
        coJustRun { quizService.updateLastQuestionId(any(), any()) }
        coJustRun { quizService.updateStreaks(any(), any(), any()) }
    }

    // =========================================================================
    // 1. Initialisation & Question Loading
    // =========================================================================

    @Test
    fun `given new ViewModel, when InitialLoad fired, then ShowLoader is first effect`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.effects.test {
                vm.handleEvent(QuizViewModel.Event.InitialLoad)
                assertEquals(QuizViewModel.Effect.ShowLoader, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given questions returned, when InitialLoad completes, then questions are populated`() =
        runTest(mainDispatcherRule.testScheduler) {
            val questions = buildQuestions(count = 3)
            coEvery { quizService.loadQuestions(questionUrl) } returns questions
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(questions, vm.uiState.value.questions)
        }

    @Test
    fun `given questions loaded, when load completes, then NavigateToQuiz effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.NavigateToQuiz, vm.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given questions loaded, when load completes, then effects sequence is ShowLoader then NavigateToQuiz`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.effects.test {
                vm.handleEvent(QuizViewModel.Event.InitialLoad)
                assertEquals(QuizViewModel.Effect.ShowLoader, awaitItem())
                advanceUntilIdle()
                assertEquals(QuizViewModel.Effect.NavigateToQuiz, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given no prior progress, when load completes, then progress saved as IN_PROGRESS`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns null
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            coVerify {
                quizService.saveProgress(
                    categoryId = categoryId,
                    status = CategoryStatus.IN_PROGRESS,
                    lastQuestionId = 1
                )
            }
        }

    @Test
    fun `given no prior progress, when load completes, then index starts at 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(0, vm.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given loadQuestions called, then url from SavedStateHandle is used`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            coVerify(exactly = 1) { quizService.loadQuestions(questionUrl) }
        }

    @Test
    fun `given InitialLoad already fired, when fired again, then loadQuestions called only once`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            coVerify(exactly = 1) { quizService.loadQuestions(any()) }
        }

    // ─── Resume from IN_PROGRESS ──────────────────────────────────────────────

    @Test
    fun `given IN_PROGRESS with savedQuestionId, when loaded, then resumes at correct index`() =
        runTest(mainDispatcherRule.testScheduler) {
            val questions = buildQuestions(count = 5)
            coEvery { quizService.loadQuestions(any()) } returns questions
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.IN_PROGRESS,
                lastQuestionId = 3,  // question with id=3 is at index 2
                currentStreak = 2,
                allTimeLongestStreak = 4
            )
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(2, vm.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given IN_PROGRESS, when loaded, then currentStreak restored from DB`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.IN_PROGRESS,
                lastQuestionId = 1,
                currentStreak = 3,
                allTimeLongestStreak = 5
            )
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(3, vm.uiState.value.currentStreak)
        }

    @Test
    fun `given IN_PROGRESS, when loaded, then allTimeLongestStreak restored from DB`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.IN_PROGRESS,
                lastQuestionId = 1,
                currentStreak = 2,
                allTimeLongestStreak = 7
            )
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(7, vm.uiState.value.allTimeLongestStreak)
        }

    @Test
    fun `given IN_PROGRESS with unknown savedId, when loaded, then falls back to index 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.IN_PROGRESS,
                lastQuestionId = 999  // not in the question list
            )
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(0, vm.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given NOT_STARTED progress, when loaded, then starts at index 0 same as new`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.NOT_STARTED
            )
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(0, vm.uiState.value.currentQuestionIndex)
        }

    // ─── Review mode (COMPLETED) ──────────────────────────────────────────────

    @Test
    fun `given COMPLETED progress, when loaded in review mode, then starts at index 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.COMPLETED,
                allTimeLongestStreak = 5
            )
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(0, vm.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given COMPLETED progress, when loaded in review mode, then allTimeLongestStreak seeded from DB`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.COMPLETED,
                allTimeLongestStreak = 8
            )
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(8, vm.uiState.value.allTimeLongestStreak)
        }

    @Test
    fun `given COMPLETED progress, when loaded in review mode, then currentStreak is 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.COMPLETED,
                currentStreak = 0,
                allTimeLongestStreak = 5
            )
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(0, vm.uiState.value.currentStreak)
        }

    // ─── Error cases ──────────────────────────────────────────────────────────

    @Test
    fun `given service throws, when loading, then ShowError effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } throws RuntimeException("network error")
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.ShowError, vm.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given service returns empty list, when loading, then ShowError effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns emptyList()
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.ShowError, vm.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given service throws, when loading fails, then questions list stays empty`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } throws RuntimeException("error")
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            assertTrue(vm.uiState.value.questions.isEmpty())
        }

    @Test
    fun `given error state, when RetryApiCall fired, then loadQuestions is called again`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } throws RuntimeException("error")
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions()
            vm.handleEvent(QuizViewModel.Event.RetryApiCall)
            advanceUntilIdle()
            coVerify(exactly = 2) { quizService.loadQuestions(any()) }
        }

    @Test
    fun `given error state, when RetryApiCall succeeds, then NavigateToQuiz is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } throws RuntimeException("error")
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions()
            vm.handleEvent(QuizViewModel.Event.RetryApiCall)
            advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.NavigateToQuiz, vm.effects.replayCache.firstOrNull())
        }

    // =========================================================================
    // 2. Option Selection — Correct Answer
    // =========================================================================

    @Test
    fun `given loaded quiz, when correct option selected, then isAnswerRevealed is true`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertTrue(vm.uiState.value.isAnswerRevealed)
        }

    @Test
    fun `given loaded quiz, when correct option selected, then selectedOptionIndex matches`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertEquals(0, vm.uiState.value.selectedOptionIndex)
        }

    @Test
    fun `given loaded quiz, when correct option selected, then correctCount increments to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertEquals(1, vm.uiState.value.correctCount)
        }

    @Test
    fun `given loaded quiz, when correct option selected, then currentStreak increments to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertEquals(1, vm.uiState.value.currentStreak)
        }

    @Test
    fun `given loaded quiz, when correct option selected, then longestStreak updates to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertEquals(1, vm.uiState.value.longestStreak)
        }

    @Test
    fun `given loaded quiz, when correct option selected, then updateStreaks is called with new streak`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceUntilIdle()
            coVerify { quizService.updateStreaks(categoryId, 1, 1) }
        }

    // =========================================================================
    // 3. Option Selection — Wrong Answer
    // =========================================================================

    @Test
    fun `given loaded quiz, when wrong option selected, then correctCount stays 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(3))
            assertEquals(0, vm.uiState.value.correctCount)
        }

    @Test
    fun `given loaded quiz, when wrong option selected, then currentStreak resets to 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(3))
            assertEquals(0, vm.uiState.value.currentStreak)
        }

    @Test
    fun `given loaded quiz, when wrong option selected, then isAnswerRevealed is true`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(3))
            assertTrue(vm.uiState.value.isAnswerRevealed)
        }

    @Test
    fun `given loaded quiz, when wrong option selected, then longestStreak stays 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(3))
            assertEquals(0, vm.uiState.value.longestStreak)
        }

    @Test
    fun `given streak of 2 then wrong answer, when streak broken, then longestStreak preserved at 2`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 10)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            repeat(2) {
                vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            vm.handleEvent(QuizViewModel.Event.OptionSelected(3)) // wrong
            assertEquals(2, vm.uiState.value.longestStreak)
            assertEquals(0, vm.uiState.value.currentStreak)
        }

    @Test
    fun `given wrong answer, when updateStreaks called, then streak is 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(3))
            advanceUntilIdle()
            coVerify { quizService.updateStreaks(categoryId, 0, 0) }
        }

    // =========================================================================
    // 4. Streak Milestones & Celebration
    // =========================================================================

    @Test
    fun `given 2 correct answers, when third correct selected, then showStreakCelebration is true`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 10)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            repeat(2) {
                vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertTrue(vm.uiState.value.showStreakCelebration)
            assertEquals(3, vm.uiState.value.currentStreak)
        }

    @Test
    fun `given only 2 correct answers, when second selected, then showStreakCelebration is false`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 10)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertFalse(vm.uiState.value.showStreakCelebration)
            advanceTimeBy(2_001L); advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertFalse(vm.uiState.value.showStreakCelebration)
            assertEquals(2, vm.uiState.value.currentStreak)
        }

    @Test
    fun `given celebration showing, when 1500ms pass, then ViewModel auto-dismisses showStreakCelebration`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 10)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            repeat(2) {
                vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L); advanceUntilIdle()
            }
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertTrue(vm.uiState.value.showStreakCelebration)
            advanceTimeBy(1_500L); advanceUntilIdle()
            assertFalse(vm.uiState.value.showStreakCelebration)
        }

    @Test
    fun `given celebration showing, when full 2s pass, then question advances and celebration cleared`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 10)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            repeat(2) {
                vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L); advanceUntilIdle()
            }
            val indexBefore = vm.uiState.value.currentQuestionIndex
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceTimeBy(2_001L); advanceUntilIdle()
            assertFalse(vm.uiState.value.showStreakCelebration)
            assertEquals(indexBefore + 1, vm.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given streak already at 3, when 4th correct selected, then showStreakCelebration is false at streak 4`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 10)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            repeat(3) {
                vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L); advanceUntilIdle()
            }
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            // Celebration only triggers at exactly streak == 3; streak 4 does NOT re-trigger it
            assertFalse(vm.uiState.value.showStreakCelebration)
            assertEquals(4, vm.uiState.value.currentStreak)
        }

    // =========================================================================
    // 5. All-Time Longest Streak
    // =========================================================================

    @Test
    fun `given allTimeLongest 3 in DB, when 4th correct answer, then allTimeLongestStreak updates to 4`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.IN_PROGRESS,
                lastQuestionId = 1,
                currentStreak = 3,
                allTimeLongestStreak = 3
            )
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 10)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0)) // streak: 3 → 4
            assertEquals(4, vm.uiState.value.allTimeLongestStreak)
        }

    @Test
    fun `given allTimeLongest 5 in DB, when current streak reaches only 3, then allTimeLongest stays 5`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.COMPLETED,
                allTimeLongestStreak = 5
            )
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 10)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            // First answer wrong (resets streak), then 3 correct (streak = 3 < 5)
            vm.handleEvent(QuizViewModel.Event.OptionSelected(3))
            advanceTimeBy(2_001L); advanceUntilIdle()
            repeat(3) {
                vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L); advanceUntilIdle()
            }
            assertEquals(5, vm.uiState.value.allTimeLongestStreak)
        }

    @Test
    fun `given allTimeLongest 0, when 3 correct answers, then allTimeLongest updates to 3`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 10)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            repeat(3) {
                vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L); advanceUntilIdle()
            }
            assertEquals(3, vm.uiState.value.allTimeLongestStreak)
        }

    // =========================================================================
    // 6. Review Mode — COMPLETED → first answer re-saves as IN_PROGRESS
    // =========================================================================

    @Test
    fun `given COMPLETED review mode, when first option selected, then saveProgress called with IN_PROGRESS`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.COMPLETED,
                allTimeLongestStreak = 0
            )
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceUntilIdle()
            coVerify {
                quizService.saveProgress(
                    categoryId = categoryId,
                    status = CategoryStatus.IN_PROGRESS,
                    lastQuestionId = any()
                )
            }
        }

    @Test
    fun `given COMPLETED review mode, when second option selected, then saveProgress NOT called again`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.getProgress(categoryId) } returns CategoryProgressSnapshot(
                categoryId = categoryId,
                status = CategoryStatus.COMPLETED
            )
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 10)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceUntilIdle()
            advanceTimeBy(2_001L); advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0)) // second answer
            advanceUntilIdle()
            // saveProgress(IN_PROGRESS) should be called exactly once (for the first answer)
            coVerify(exactly = 1) {
                quizService.saveProgress(categoryId, CategoryStatus.IN_PROGRESS, any())
            }
        }

    // =========================================================================
    // 7. Skip Question
    // =========================================================================

    @Test
    fun `given loaded quiz, when SkipQuestion fired, then skippedCount increments`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.SkipQuestion)
            assertEquals(1, vm.uiState.value.skippedCount)
        }

    @Test
    fun `given loaded quiz, when SkipQuestion fired, then index advances immediately without delay`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.SkipQuestion)
            // No time advancement needed — skip is immediate
            advanceUntilIdle()
            assertEquals(1, vm.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given loaded quiz, when SkipQuestion fired, then isAnswerRevealed stays false`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.SkipQuestion)
            advanceUntilIdle()
            // Skip does NOT reveal the answer — question just moves on
            assertFalse(vm.uiState.value.isAnswerRevealed)
        }

    @Test
    fun `given loaded quiz, when SkipQuestion fired, then selectedOptionIndex stays null`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.SkipQuestion)
            advanceUntilIdle()
            assertNull(vm.uiState.value.selectedOptionIndex)
        }

    @Test
    fun `given 2-question quiz, when both skipped, then NavigateToResults emitted immediately`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 2)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.SkipQuestion) // immediately advances to Q2
            vm.handleEvent(QuizViewModel.Event.SkipQuestion) // immediately goes to results
            advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.NavigateToResults, vm.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given 3 questions, when 2 skipped, then skippedCount is 2 and index is 2`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 3)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.SkipQuestion)
            vm.handleEvent(QuizViewModel.Event.SkipQuestion)
            advanceUntilIdle()
            assertEquals(2, vm.uiState.value.skippedCount)
            assertEquals(2, vm.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given pending auto-advance from option selection, when SkipQuestion fired, then auto-advance is cancelled`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 5)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0)) // answer Q1, schedules 2s auto-advance
            assertEquals(0, vm.uiState.value.currentQuestionIndex)
            vm.handleEvent(QuizViewModel.Event.SkipQuestion) // cancels pending advance, advances immediately
            advanceUntilIdle()
            // Skip advanced from Q1 (index 0) to Q2 (index 1).
            // The previously scheduled 2s auto-advance was cancelled so no double-advance.
            assertEquals(1, vm.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given 1-question quiz, when skipped, then NavigateToResults is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 1)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.SkipQuestion)
            advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.NavigateToResults, vm.effects.replayCache.firstOrNull())
        }

    // =========================================================================
    // 8. Auto-Advance After Delay
    // =========================================================================

    @Test
    fun `given correct option selected, when 2s pass, then currentIndex advances to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertEquals(0, vm.uiState.value.currentQuestionIndex)
            advanceTimeBy(2_001L); advanceUntilIdle()
            assertEquals(1, vm.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given wrong option selected, when 2s pass, then index still advances`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(3))
            advanceTimeBy(2_001L); advanceUntilIdle()
            assertEquals(1, vm.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given option selected, when 2s pass, then isAnswerRevealed resets to false`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertTrue(vm.uiState.value.isAnswerRevealed)
            advanceTimeBy(2_001L); advanceUntilIdle()
            assertFalse(vm.uiState.value.isAnswerRevealed)
        }

    @Test
    fun `given option selected, when 2s pass, then selectedOptionIndex resets to null`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceTimeBy(2_001L); advanceUntilIdle()
            assertNull(vm.uiState.value.selectedOptionIndex)
        }

    @Test
    fun `given 1-question quiz, when option selected and 2s pass, then NavigateToResults emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 1)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceTimeBy(2_001L); advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.NavigateToResults, vm.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given advance to next question, when advancing, then updateLastQuestionId called with next question id`() =
        runTest(mainDispatcherRule.testScheduler) {
            val questions = buildQuestions(count = 3)
            coEvery { quizService.loadQuestions(any()) } returns questions
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceTimeBy(2_001L); advanceUntilIdle()
            coVerify { quizService.updateLastQuestionId(categoryId, questions[1].id) }
        }

    // =========================================================================
    // 9. Finish Module
    // =========================================================================

    @Test
    fun `given loaded quiz, when FinishModule fired, then saveProgress called with COMPLETED`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.FinishModule)
            advanceUntilIdle()
            coVerify { quizService.saveProgress(categoryId, CategoryStatus.COMPLETED, null) }
        }

    @Test
    fun `given loaded quiz, when FinishModule fired, then FinishModule effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.effects.test {
                awaitItem() // consume replayed NavigateToQuiz
                vm.handleEvent(QuizViewModel.Event.FinishModule)
                advanceUntilIdle()
                assertEquals(QuizViewModel.Effect.FinishModule, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    // =========================================================================
    // 10. Navigate Back
    // =========================================================================

    @Test
    fun `given loaded quiz at Q1, when NavigateBack fired, then updateLastQuestionId called with Q1 id`() =
        runTest(mainDispatcherRule.testScheduler) {
            val questions = buildQuestions(count = 5)
            coEvery { quizService.loadQuestions(any()) } returns questions
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.NavigateBack)
            advanceUntilIdle()
            coVerify { quizService.updateLastQuestionId(categoryId, questions[0].id) }
        }

    @Test
    fun `given loaded quiz, when NavigateBack fired, then NavigateBack effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.effects.test {
                awaitItem() // consume replayed effect
                vm.handleEvent(QuizViewModel.Event.NavigateBack)
                advanceUntilIdle()
                assertEquals(QuizViewModel.Effect.NavigateBack, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given empty questions, when NavigateBack fired, then updateLastQuestionId NOT called`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns emptyList()
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.NavigateBack)
            advanceUntilIdle()
            coVerify(exactly = 0) { quizService.updateLastQuestionId(any(), any()) }
        }

    // =========================================================================
    // 11. StateFlow — Full Happy-Path Sequence
    // =========================================================================

    @Test
    fun `given 2-question quiz, when played completely, then state sequence is correct`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 2)
            val vm = createViewModel()
            vm.uiState.test {
                // 1. Initial empty state
                val initial = awaitItem()
                assertTrue(initial.questions.isEmpty())

                // 2. Load questions
                vm.handleEvent(QuizViewModel.Event.InitialLoad)
                advanceUntilIdle()
                val loaded = awaitItem()
                assertEquals(2, loaded.questions.size)
                assertEquals(0, loaded.currentQuestionIndex)

                // 3. Answer Q1 correctly
                vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                val answered1 = awaitItem()
                assertTrue(answered1.isAnswerRevealed)
                assertEquals(1, answered1.correctCount)
                assertEquals(1, answered1.currentStreak)

                // 4. Auto-advance to Q2
                advanceTimeBy(2_001L); advanceUntilIdle()
                val q2 = awaitItem()
                assertEquals(1, q2.currentQuestionIndex)
                assertFalse(q2.isAnswerRevealed)
                assertNull(q2.selectedOptionIndex)

                // 5. Answer Q2 correctly
                vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                val answered2 = awaitItem()
                assertEquals(2, answered2.correctCount)
                assertEquals(2, answered2.currentStreak)

                // 6. Auto-advance → clears answer state, NavigateToResults emitted
                advanceTimeBy(2_001L); advanceUntilIdle()
                val cleared = awaitItem()
                assertFalse(cleared.isAnswerRevealed)
                assertNull(cleared.selectedOptionIndex)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given last question answered correctly, when result reached, then correctCount preserved`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions(any()) } returns buildQuestions(count = 1)
            val vm = createViewModel()
            vm.handleEvent(QuizViewModel.Event.InitialLoad)
            advanceUntilIdle()
            vm.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceTimeBy(2_001L); advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.NavigateToResults, vm.effects.replayCache.firstOrNull())
            assertEquals(1, vm.uiState.value.correctCount)
        }
}

