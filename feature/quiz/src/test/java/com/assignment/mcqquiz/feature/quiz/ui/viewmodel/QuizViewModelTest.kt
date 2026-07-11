package com.assignment.mcqquiz.feature.quiz.ui.viewmodel

import app.cash.turbine.test
import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.domain.service.QuizService
import com.assignment.mcqquiz.feature.quiz.domain.contract.QuizAppContract
import com.assignment.mcqquiz.feature.quiz.util.MainDispatcherRule
import io.mockk.coEvery
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
 * Comprehensive MVI unit tests for [QuizViewModel].
 *
 * Strategy:
 * - [MainDispatcherRule] replaces [kotlinx.coroutines.Dispatchers.Main] with a
 *   [kotlinx.coroutines.test.StandardTestDispatcher] so [androidx.lifecycle.viewModelScope]
 *   is fully controlled by the test scheduler.
 * - All [runTest] invocations share the same scheduler via [MainDispatcherRule.testScheduler],
 *   guaranteeing that [advanceUntilIdle] and [advanceTimeBy] affect ViewModel coroutines.
 * - [app.cash.turbine.test] is used for asserting emissions from [kotlinx.coroutines.flow.StateFlow]
 *   and [kotlinx.coroutines.flow.SharedFlow].
 * - [QuizService] is mocked with MockK; no real network or disk I/O occurs.
 *
 * Test naming convention: `given_<precondition>_when_<action>_then_<expected outcome>`
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    // ─── Rules ────────────────────────────────────────────────────────────────

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // ─── Mocks ────────────────────────────────────────────────────────────────

    private val quizService: QuizService = mockk()

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    /**
     * Creates a list of [count] test questions where every correct answer is option 0,
     * except [alternateCorrectAt] indices whose correct answer is option 1.
     */
    private fun buildQuestions(
        count: Int = 5,
        alternateCorrectAt: Set<Int> = emptySet()
    ): List<Question> = (0 until count).map { i ->
        Question(
            id = i + 1,
            question = "Question ${i + 1}",
            options = listOf("Option A", "Option B", "Option C", "Option D"),
            correctOptionIndex = if (i in alternateCorrectAt) 1 else 0
        )
    }

    // ─── ViewModel factory (after mocks are configured) ───────────────────────

    private fun createViewModel(): QuizViewModel = QuizViewModel(quizService)

    // =========================================================================
    // 1. Initialisation & Question Loading
    // =========================================================================

    @Test
    fun `given new ViewModel, when initialised, then initial state has isLoading true and screen is Splash`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions()

            val viewModel = createViewModel()

            // Before advancing — the init coroutine has not run yet (StandardTestDispatcher is lazy)
            assertEquals(true, viewModel.uiState.value.isLoading)
            assertEquals(QuizAppContract.Splash, viewModel.uiState.value.screen)
        }

    @Test
    fun `given quiz service returns questions, when loading completes, then state has correct questions`() =
        runTest(mainDispatcherRule.testScheduler) {
            val questions = buildQuestions(count = 10)
            coEvery { quizService.loadQuestions() } returns questions

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertEquals(questions, viewModel.uiState.value.questions)
        }

    @Test
    fun `given quiz service returns questions, when loading completes, then isLoading is false`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions()

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `given quiz service returns questions, when loading completes, then screen transitions to Quiz`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions()

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertEquals(QuizAppContract.Quiz, viewModel.uiState.value.screen)
        }

    @Test
    fun `given quiz service returns questions, when loading completes, then StateFlow emits Splash then Quiz state`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions()

            val viewModel = createViewModel()

            viewModel.uiState.test {
                // Splash / loading state
                val splashState = awaitItem()
                assertEquals(QuizAppContract.Splash, splashState.screen)
                assertTrue(splashState.isLoading)

                advanceUntilIdle()

                // Quiz / loaded state
                val quizState = awaitItem()
                assertEquals(QuizAppContract.Quiz, quizState.screen)
                assertFalse(quizState.isLoading)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given quiz service returns questions, when loading completes, then quizService is called exactly once`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions()

            val viewModel = createViewModel()
            advanceUntilIdle()

            coVerify(exactly = 1) { quizService.loadQuestions() }
        }

    @Test
    fun `given quiz service returns questions, when loading completes, then currentIndex starts at 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions()

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertEquals(0, viewModel.uiState.value.currentQuestionIndex)
        }

    // ─── Loading failure ──────────────────────────────────────────────────────

    @Test
    fun `given quiz service throws exception, when loading fails, then error effect is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } throws RuntimeException("Network error")

            val viewModel = createViewModel()

            viewModel.effects.test {
                advanceUntilIdle()

                val effect = awaitItem()
                assertTrue(effect is QuizViewModel.Effect.ShowQuestionLoadError)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given quiz service throws exception, when loading fails, then error message is correct`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } throws RuntimeException("Network error")

            val viewModel = createViewModel()

            viewModel.effects.test {
                advanceUntilIdle()

                val effect = awaitItem() as QuizViewModel.Effect.ShowQuestionLoadError
                assertEquals("Failed to load questions. Please try again.", effect.message)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given quiz service throws exception, when loading fails, then isLoading becomes false`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } throws RuntimeException("Network error")

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `given quiz service throws exception, when loading fails, then screen remains Splash`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } throws RuntimeException("Network error")

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertEquals(QuizAppContract.Splash, viewModel.uiState.value.screen)
        }

    @Test
    fun `given quiz service throws exception, when loading fails, then questions list is empty`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } throws RuntimeException("Network error")

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.questions.isEmpty())
        }

    // =========================================================================
    // 2. Option Selection — Correct Answer
    // =========================================================================

    @Before
    fun setUp() {
        // Reset the cross-session streak counter so each test starts clean.
        QuizViewModel.resetAllTimeLongestStreakForTest()
        // Default stub — can be overridden per test
        coEvery { quizService.loadQuestions() } returns buildQuestions()
    }

    @Test
    fun `given loaded quiz, when correct option is selected, then isAnswerRevealed is true`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0)) // option 0 is correct

            assertTrue(viewModel.uiState.value.isAnswerRevealed)
        }

    @Test
    fun `given loaded quiz, when correct option is selected, then selectedOptionIndex matches the selection`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))

            assertEquals(0, viewModel.uiState.value.selectedOptionIndex)
        }

    @Test
    fun `given loaded quiz, when correct option is selected, then correctCount increments to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))

            assertEquals(1, viewModel.uiState.value.correctCount)
        }

    @Test
    fun `given loaded quiz, when correct option is selected, then currentStreak increments to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))

            assertEquals(1, viewModel.uiState.value.currentStreak)
        }

    @Test
    fun `given loaded quiz, when correct option is selected, then longestStreak is updated to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))

            assertEquals(1, viewModel.uiState.value.longestStreak)
        }

    // =========================================================================
    // 3. Option Selection — Wrong Answer
    // =========================================================================

    @Test
    fun `given loaded quiz, when incorrect option is selected, then correctCount stays at 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(3)) // 3 is wrong (correct is 0)

            assertEquals(0, viewModel.uiState.value.correctCount)
        }

    @Test
    fun `given loaded quiz, when incorrect option is selected, then currentStreak resets to 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(3)) // wrong

            assertEquals(0, viewModel.uiState.value.currentStreak)
        }

    @Test
    fun `given loaded quiz, when incorrect option is selected, then isAnswerRevealed becomes true`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(3))

            assertTrue(viewModel.uiState.value.isAnswerRevealed)
        }

    @Test
    fun `given loaded quiz, when incorrect option is selected, then longestStreak stays at 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(2)) // wrong

            assertEquals(0, viewModel.uiState.value.longestStreak)
        }

    // =========================================================================
    // 4. Guard: Selection Ignored When Answer Already Revealed
    // =========================================================================

    @Test
    fun `given answer already revealed, when option is selected again, then correctCount does not change`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0)) // correct, reveals answer
            val countAfterFirst = viewModel.uiState.value.correctCount

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0)) // should be ignored
            assertEquals(countAfterFirst, viewModel.uiState.value.correctCount)
        }

    @Test
    fun `given answer already revealed, when different option is selected, then selectedOptionIndex does not change`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            val selectedAfterFirst = viewModel.uiState.value.selectedOptionIndex

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(2)) // should be ignored
            assertEquals(selectedAfterFirst, viewModel.uiState.value.selectedOptionIndex)
        }

    // =========================================================================
    // 5. Streak Milestones & Auto-Dismiss
    //
    // Celebration shows for any streak >= 3 (not just multiples of 3).
    // The ViewModel owns both showing AND dismissing: after 1 500 ms it sets
    // showStreakCelebration = false, then the remaining 500 ms elapse before
    // advancing to the next question (2 000 ms total — same as the no-
    // celebration path).  No StreakCelebrationDismissed event exists any more.
    // =========================================================================

    @Test
    fun `given 2 correct answers already, when third correct option selected, then showStreakCelebration is immediately true`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Reach Q3 with a running streak of 2
            repeat(2) {
                viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }

            // Third correct answer (streak = 3) — celebration must appear immediately
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))

            assertTrue(viewModel.uiState.value.showStreakCelebration)
            assertEquals(3, viewModel.uiState.value.currentStreak)
        }

    @Test
    fun `given streak celebration showing, when 1500ms pass, then ViewModel auto-dismisses showStreakCelebration`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val viewModel = createViewModel()
            advanceUntilIdle()

            repeat(2) {
                viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0)) // streak = 3, celebration starts

            assertTrue(viewModel.uiState.value.showStreakCelebration)

            // Auto-dismiss fires at exactly STREAK_CELEBRATION_DISMISS_MS = 1 500 ms
            advanceTimeBy(1_500L)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.showStreakCelebration)
        }

    @Test
    fun `given streak celebration showing, when 2 seconds pass, then question advances and celebration is cleared`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val viewModel = createViewModel()
            advanceUntilIdle()

            repeat(2) {
                viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            // Q3, streak = 3
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            val indexBeforeAdvance = viewModel.uiState.value.currentQuestionIndex

            advanceTimeBy(2_001L)  // 1 500 ms dismiss + 500 ms remaining → advance
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.showStreakCelebration)
            assertEquals(indexBeforeAdvance + 1, viewModel.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given streak already at 3, when fourth correct option selected, then showStreakCelebration is true again`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Build streak of 3 and let the question advance
            repeat(3) {
                viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }

            // Streak is still 3 after advancing; 4th correct answer keeps >= 3
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))

            assertTrue(viewModel.uiState.value.showStreakCelebration)
            assertEquals(4, viewModel.uiState.value.currentStreak)
        }

    @Test
    fun `given streak of 2, when second correct option is selected, then showStreakCelebration is false`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val viewModel = createViewModel()
            advanceUntilIdle()

            // First correct answer — streak = 1 (below threshold)
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertFalse(viewModel.uiState.value.showStreakCelebration)
            assertEquals(1, viewModel.uiState.value.currentStreak)

            advanceTimeBy(2_001L)
            advanceUntilIdle()

            // Second correct answer — streak = 2 (still below threshold)
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertFalse(viewModel.uiState.value.showStreakCelebration)
            assertEquals(2, viewModel.uiState.value.currentStreak)
        }

    @Test
    fun `given streak of 3, when incorrect answer given next, then currentStreak resets to 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val viewModel = createViewModel()
            advanceUntilIdle()

            repeat(3) {
                viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            assertEquals(3, viewModel.uiState.value.longestStreak)

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(3)) // wrong
            assertEquals(0, viewModel.uiState.value.currentStreak)
        }

    @Test
    fun `given streak of 3 then wrong answer, when streak broken, then longestStreak is preserved at 3`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val viewModel = createViewModel()
            advanceUntilIdle()

            repeat(3) {
                viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(3)) // wrong

            assertEquals(3, viewModel.uiState.value.longestStreak)
            assertEquals(0, viewModel.uiState.value.currentStreak)
        }

    // =========================================================================
    // 6. Skip Question
    // =========================================================================

    @Test
    fun `given loaded quiz, when question is skipped, then skippedCount increments to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)

            assertEquals(1, viewModel.uiState.value.skippedCount)
        }

    @Test
    fun `given loaded quiz, when question is skipped, then currentIndex advances to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)

            assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given loaded quiz, when question is skipped, then isAnswerRevealed remains false`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)

            assertFalse(viewModel.uiState.value.isAnswerRevealed)
        }

    @Test
    fun `given loaded quiz, when question is skipped, then selectedOptionIndex remains null`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)

            assertNull(viewModel.uiState.value.selectedOptionIndex)
        }

    @Test
    fun `given loaded quiz, when all questions are skipped, then screen transitions to Results`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 3)
            val viewModel = createViewModel()
            advanceUntilIdle()

            repeat(3) {
                viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)
            }

            assertEquals(QuizAppContract.Results, viewModel.uiState.value.screen)
        }

    @Test
    fun `given loaded quiz, when all questions are skipped, then skippedCount equals total questions`() =
        runTest(mainDispatcherRule.testScheduler) {
            val questionCount = 3
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = questionCount)
            val viewModel = createViewModel()
            advanceUntilIdle()

            repeat(questionCount) {
                viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)
            }

            assertEquals(questionCount, viewModel.uiState.value.skippedCount)
        }

    @Test
    fun `given multiple skips, when skipping, then skippedCount accumulates correctly`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)
            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)
            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)

            assertEquals(3, viewModel.uiState.value.skippedCount)
            assertEquals(3, viewModel.uiState.value.currentQuestionIndex)
        }

    // =========================================================================
    // 7. Auto-Advance After Delay
    // =========================================================================

    @Test
    fun `given correct option selected, when 2 seconds pass, then currentIndex advances to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 5)
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0)) // correct
            assertEquals(0, viewModel.uiState.value.currentQuestionIndex) // still on Q1

            advanceTimeBy(2_001L) // past the 2-second delay
            advanceUntilIdle()

            assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given incorrect option selected, when 2 seconds pass, then currentIndex still advances to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 5)
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(3)) // wrong
            assertEquals(0, viewModel.uiState.value.currentQuestionIndex)

            advanceTimeBy(2_001L)
            advanceUntilIdle()

            assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given option selected, when 2 seconds pass, then isAnswerRevealed resets to false on next question`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 5)
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertTrue(viewModel.uiState.value.isAnswerRevealed)

            advanceTimeBy(2_001L)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isAnswerRevealed)
        }

    @Test
    fun `given option selected, when 2 seconds pass, then selectedOptionIndex resets to null on next question`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 5)
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))

            advanceTimeBy(2_001L)
            advanceUntilIdle()

            assertNull(viewModel.uiState.value.selectedOptionIndex)
        }

    @Test
    fun `given option selected on last question, when 2 seconds pass, then screen transitions to Results`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 1)
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))

            advanceTimeBy(2_001L)
            advanceUntilIdle()

            assertEquals(QuizAppContract.Results, viewModel.uiState.value.screen)
        }

    // ─── Skip cancels pending auto-advance ────────────────────────────────────

    @Test
    fun `given auto-advance pending, when question is skipped, then auto-advance is cancelled and index jumps immediately`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 5)
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0)) // starts auto-advance timer

            // Skip before 2 seconds — cancels the timer and advances immediately
            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)
            assertEquals(1, viewModel.uiState.value.currentQuestionIndex)

            // Advancing past the original 2-second delay — the cancelled job must NOT fire
            advanceTimeBy(2_001L)
            advanceUntilIdle()

            // Index stays at 1: the skip advanced once; the cancelled auto-advance does NOT fire again
            assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
        }

    // =========================================================================
    // 8. Last Question Navigation
    // =========================================================================

    @Test
    fun `given last question, when skipped, then screen transitions to Results immediately`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 2)
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion) // skip Q1
            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion) // skip Q2 → Results

            assertEquals(QuizAppContract.Results, viewModel.uiState.value.screen)
        }

    @Test
    fun `given last question answered correctly, when 2 seconds pass, then correctCount is preserved on Results`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 1)
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0)) // correct

            advanceTimeBy(2_001L)
            advanceUntilIdle()

            assertEquals(QuizAppContract.Results, viewModel.uiState.value.screen)
            assertEquals(1, viewModel.uiState.value.correctCount)
        }

    // =========================================================================
    // 9. Restart Quiz — finish-and-restart via Effect
    //
    // onRestart() no longer resets state in-place. Instead it:
    //  1. Saves the session's longestStreak into allTimeLongestStreak (companion) — synchronously.
    //  2. Launches a coroutine that calls _effects.emit(Effect.RestartApp) — asynchronously.
    //     advanceUntilIdle() is required after firing RestartQuiz so the launched
    //     coroutine runs and delivers the effect before awaitItem() is called.
    // A fresh ViewModel then picks up allTimeLongestStreak in loadQuestions().
    // =========================================================================

    @Test
    fun `given quiz in progress, when RestartQuiz event fired, then Effect RestartApp is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.effects.test {
                viewModel.handleEvent(QuizViewModel.Event.RestartQuiz)
                advanceUntilIdle() // drive the viewModelScope.launch { emit(...) } coroutine

                val effect = awaitItem()
                assertTrue(effect is QuizViewModel.Effect.RestartApp)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given quiz with longestStreak 4, when RestartQuiz event fired, then allTimeLongestStreak is updated`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Build a streak of 4
            repeat(4) {
                viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            assertEquals(4, viewModel.uiState.value.longestStreak)

            viewModel.handleEvent(QuizViewModel.Event.RestartQuiz)

            assertEquals(4, QuizViewModel.allTimeLongestStreak)
        }

    @Test
    fun `given prior allTimeLongestStreak of 5, when new questions load, then longestStreak starts at 5`() =
        runTest(mainDispatcherRule.testScheduler) {
            // Simulate a prior session that achieved streak 5
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val firstVm = createViewModel()
            advanceUntilIdle()
            repeat(5) {
                firstVm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            firstVm.handleEvent(QuizViewModel.Event.RestartQuiz)
            assertEquals(5, QuizViewModel.allTimeLongestStreak)

            // New ViewModel (simulates Activity restart)
            val newVm = createViewModel()
            advanceUntilIdle()

            assertEquals(5, newVm.uiState.value.longestStreak)
        }

    @Test
    fun `given current session longestStreak 3 and prior allTimeLongestStreak 5, when RestartQuiz fired, then allTimeLongestStreak stays 5`() =
        runTest(mainDispatcherRule.testScheduler) {
            // Seed a prior high of 5
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val firstVm = createViewModel()
            advanceUntilIdle()
            repeat(5) {
                firstVm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            firstVm.handleEvent(QuizViewModel.Event.RestartQuiz)

            // New session — achieve only streak 3
            val secondVm = createViewModel()
            advanceUntilIdle()
            repeat(3) {
                secondVm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            secondVm.handleEvent(QuizViewModel.Event.RestartQuiz)

            // The all-time high should remain 5, not drop to 3
            assertEquals(5, QuizViewModel.allTimeLongestStreak)
        }

    @Test
    fun `given quiz on Results screen, when RestartQuiz event fired, then Effect RestartApp is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 1)
            val viewModel = createViewModel()
            advanceUntilIdle()

            // Complete the quiz to reach Results screen
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceTimeBy(2_001L)
            advanceUntilIdle()
            assertEquals(QuizAppContract.Results, viewModel.uiState.value.screen)

            viewModel.effects.test {
                viewModel.handleEvent(QuizViewModel.Event.RestartQuiz)
                advanceUntilIdle() // drive the viewModelScope.launch { emit(...) } coroutine

                val effect = awaitItem()
                assertTrue(effect is QuizViewModel.Effect.RestartApp)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // =========================================================================
    // 10. StateFlow Turbine — Full State Emission Sequence
    // =========================================================================

    @Test
    fun `given full happy-path quiz with 2 questions, when played completely, then state sequence is correct`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 2)
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // 1. Initial loading state
                val loading = awaitItem()
                assertTrue(loading.isLoading)
                assertEquals(QuizAppContract.Splash, loading.screen)

                // 2. Questions loaded
                advanceUntilIdle()
                val loaded = awaitItem()
                assertFalse(loaded.isLoading)
                assertEquals(QuizAppContract.Quiz, loaded.screen)
                assertEquals(0, loaded.currentQuestionIndex)

                // 3. Answer Q1 correctly
                viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
                val answered = awaitItem()
                assertTrue(answered.isAnswerRevealed)
                assertEquals(1, answered.correctCount)
                assertEquals(1, answered.currentStreak)

                // 4. Auto-advance after 2s — Q2
                advanceTimeBy(2_001L)
                advanceUntilIdle()
                val q2 = awaitItem()
                assertEquals(1, q2.currentQuestionIndex)
                assertFalse(q2.isAnswerRevealed)
                assertNull(q2.selectedOptionIndex)

                // 5. Answer Q2 correctly
                viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
                val answered2 = awaitItem()
                assertTrue(answered2.isAnswerRevealed)
                assertEquals(2, answered2.correctCount)
                assertEquals(2, answered2.currentStreak)

                // 6. Auto-advance → Results
                advanceTimeBy(2_001L)
                advanceUntilIdle()
                val results = awaitItem()
                assertEquals(QuizAppContract.Results, results.screen)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // =========================================================================
    // 11. Effects SharedFlow — correct effect sequence
    // =========================================================================

    @Test
    fun `given quiz service throws, when load fails and RestartQuiz is fired, then error effect then RestartApp effect are emitted in order`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } throws RuntimeException("Always fails")
            val viewModel = createViewModel()

            viewModel.effects.test {
                advanceUntilIdle() // first load fails → ShowQuestionLoadError (tryEmit, synchronous)

                val firstEffect = awaitItem()
                assertTrue(firstEffect is QuizViewModel.Effect.ShowQuestionLoadError)

                viewModel.handleEvent(QuizViewModel.Event.RestartQuiz)
                advanceUntilIdle() // drive the viewModelScope.launch { emit(...) } coroutine

                val secondEffect = awaitItem()
                assertTrue(secondEffect is QuizViewModel.Effect.RestartApp)

                cancelAndIgnoreRemainingEvents()
            }
        }
}


