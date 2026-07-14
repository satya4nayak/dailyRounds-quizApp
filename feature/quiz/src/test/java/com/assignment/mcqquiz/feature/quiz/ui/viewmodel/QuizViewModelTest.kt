package com.assignment.mcqquiz.feature.quiz.ui.viewmodel

import app.cash.turbine.test
import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.domain.service.QuizService
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

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val quizService: QuizService = mockk()

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

    private fun createViewModel(): QuizViewModel = QuizViewModel(quizService).also {
        it.handleEvent(QuizViewModel.Event.InitialLoad)
    }

    // =========================================================================
    // 1. Initialisation & Question Loading
    // =========================================================================

    @Test
    fun `given new ViewModel, when initialised, then currentScreen is Loader`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions()
            val viewModel = createViewModel()
            viewModel.effects.test {
                assertEquals(QuizViewModel.Effect.ShowLoader, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given new ViewModel, when initialised, then initial state has empty questions`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions()
            val viewModel = createViewModel()
            assertTrue(viewModel.uiState.value.questions.isEmpty())
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
    fun `given quiz service returns questions, when loading completes, then currentScreen is Quiz`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions()
            val viewModel = createViewModel()
            advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.NavigateToQuiz, viewModel.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given quiz service returns questions, when loading completes, then screen transitions Loader then Quiz`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions()
            val viewModel = createViewModel()
            viewModel.effects.test {
                assertEquals(QuizViewModel.Effect.ShowLoader, awaitItem())
                advanceUntilIdle()
                assertEquals(QuizViewModel.Effect.NavigateToQuiz, awaitItem())
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
    fun `given quiz service throws exception, when loading fails, then currentScreen is Error`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } throws RuntimeException("Network error")
            val viewModel = createViewModel()
            advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.ShowError, viewModel.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given quiz service throws exception, when loading fails, then questions list is empty`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } throws RuntimeException("Network error")
            val viewModel = createViewModel()
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.questions.isEmpty())
        }

    @Test
    fun `given quiz service throws exception, when loading fails, then currentScreen is not Quiz`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } throws RuntimeException("Network error")
            val viewModel = createViewModel()
            advanceUntilIdle()
            assertTrue(viewModel.effects.replayCache.firstOrNull() !is QuizViewModel.Effect.NavigateToQuiz)
        }

    // =========================================================================
    // 2. Option Selection — Correct Answer
    // =========================================================================

    @Before
    fun setUp() {
        QuizViewModel.resetAllTimeLongestStreakForTest()
        coEvery { quizService.loadQuestions() } returns buildQuestions()
    }

    @Test
    fun `given loaded quiz, when correct option is selected, then isAnswerRevealed is true`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
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
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(3))
            assertEquals(0, viewModel.uiState.value.correctCount)
        }

    @Test
    fun `given loaded quiz, when incorrect option is selected, then currentStreak resets to 0`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(3))
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
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(2))
            assertEquals(0, viewModel.uiState.value.longestStreak)
        }

    // =========================================================================
    // 4. UI enforces single-selection — no ViewModel guard needed
    //
    // The OptionCard composable uses clickable(enabled = !isAnswerRevealed), so
    // onOptionSelected is never called a second time after an answer is revealed.
    // There is therefore no ViewModel-level guard to test here.
    // =========================================================================


    // =========================================================================
    // 5. Streak Milestones & Auto-Dismiss
    // =========================================================================

    @Test
    fun `given 2 correct answers already, when third correct option selected, then showStreakCelebration is immediately true`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val viewModel = createViewModel()
            advanceUntilIdle()
            repeat(2) {
                viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
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
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertTrue(viewModel.uiState.value.showStreakCelebration)
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
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            val indexBeforeAdvance = viewModel.uiState.value.currentQuestionIndex
            advanceTimeBy(2_001L)
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
            repeat(3) {
                viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
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
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertFalse(viewModel.uiState.value.showStreakCelebration)
            assertEquals(1, viewModel.uiState.value.currentStreak)
            advanceTimeBy(2_001L)
            advanceUntilIdle()
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertFalse(viewModel.uiState.value.showStreakCelebration)
            assertEquals(2, viewModel.uiState.value.currentStreak)
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
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(3))
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
    fun `given loaded quiz, when all questions are skipped, then currentScreen is Results`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 3)
            val viewModel = createViewModel()
            advanceUntilIdle()
            repeat(3) { viewModel.handleEvent(QuizViewModel.Event.SkipQuestion) }
            advanceUntilIdle() // let the viewModelScope.launch { emit(NavigateToResults) } coroutine run
            assertEquals(QuizViewModel.Effect.NavigateToResults, viewModel.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given loaded quiz, when all questions are skipped, then skippedCount equals total questions`() =
        runTest(mainDispatcherRule.testScheduler) {
            val questionCount = 3
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = questionCount)
            val viewModel = createViewModel()
            advanceUntilIdle()
            repeat(questionCount) { viewModel.handleEvent(QuizViewModel.Event.SkipQuestion) }
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
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            assertEquals(0, viewModel.uiState.value.currentQuestionIndex)
            advanceTimeBy(2_001L)
            advanceUntilIdle()
            assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
        }

    @Test
    fun `given incorrect option selected, when 2 seconds pass, then currentIndex still advances to 1`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 5)
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(3))
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
    fun `given option selected on last question, when 2 seconds pass, then currentScreen is Results`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 1)
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceTimeBy(2_001L)
            advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.NavigateToResults, viewModel.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given auto-advance pending, when question is skipped, then auto-advance is cancelled and index jumps immediately`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 5)
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)
            assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
            advanceTimeBy(2_001L)
            advanceUntilIdle()
            assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
        }

    // =========================================================================
    // 8. Last Question Navigation
    // =========================================================================

    @Test
    fun `given last question, when skipped, then currentScreen is Results immediately`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 2)
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)
            viewModel.handleEvent(QuizViewModel.Event.SkipQuestion)
            advanceUntilIdle() // let the viewModelScope.launch { emit(NavigateToResults) } coroutine run
            assertEquals(QuizViewModel.Effect.NavigateToResults, viewModel.effects.replayCache.firstOrNull())
        }

    @Test
    fun `given last question answered correctly, when 2 seconds pass, then correctCount is preserved`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 1)
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceTimeBy(2_001L)
            advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.NavigateToResults, viewModel.effects.replayCache.firstOrNull())
            assertEquals(1, viewModel.uiState.value.correctCount)
        }

    // =========================================================================
    // 9. Restart Quiz — finish-and-restart via Effect
    // =========================================================================

    @Test
    fun `given quiz in progress, when RestartQuiz event fired, then Effect RestartApp is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            val viewModel = createViewModel()
            advanceUntilIdle()
            viewModel.effects.test {
                awaitItem() // consume replayed NavigateToQuiz (replay=1 replays last navigation effect)
                viewModel.handleEvent(QuizViewModel.Event.RestartQuiz)
                advanceUntilIdle()
                assertTrue(awaitItem() is QuizViewModel.Effect.RestartGame)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `given quiz with longestStreak 4, when RestartQuiz event fired, then allTimeLongestStreak is updated`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val viewModel = createViewModel()
            advanceUntilIdle()
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

            val newVm = createViewModel()
            advanceUntilIdle()
            assertEquals(5, newVm.uiState.value.longestStreak)
        }

    @Test
    fun `given current session longestStreak 3 and prior allTimeLongestStreak 5, when RestartQuiz fired, then allTimeLongestStreak stays 5`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 10)
            val firstVm = createViewModel()
            advanceUntilIdle()
            repeat(5) {
                firstVm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            firstVm.handleEvent(QuizViewModel.Event.RestartQuiz)

            val secondVm = createViewModel()
            advanceUntilIdle()
            repeat(3) {
                secondVm.handleEvent(QuizViewModel.Event.OptionSelected(0))
                advanceTimeBy(2_001L)
                advanceUntilIdle()
            }
            secondVm.handleEvent(QuizViewModel.Event.RestartQuiz)
            assertEquals(5, QuizViewModel.allTimeLongestStreak)
        }

    @Test
    fun `given quiz completed, when RestartQuiz event fired, then Effect RestartApp is emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 1)
            val viewModel = createViewModel()
            // Complete the quiz (answer last question + wait for auto-advance)
            advanceUntilIdle()
            viewModel.handleEvent(QuizViewModel.Event.OptionSelected(0))
            advanceTimeBy(2_001L)
            advanceUntilIdle()

            viewModel.effects.test {
                awaitItem() // consume replayed NavigateToResults (replay=1)
                viewModel.handleEvent(QuizViewModel.Event.RestartQuiz)
                advanceUntilIdle()
                assertTrue(awaitItem() is QuizViewModel.Effect.RestartGame)
                cancelAndIgnoreRemainingEvents()
            }
        }

    // =========================================================================
    // 10. StateFlow — Full State Emission Sequence
    // =========================================================================

    @Test
    fun `given full happy-path quiz with 2 questions, when played completely, then state sequence is correct`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } returns buildQuestions(count = 2)
            val viewModel = createViewModel()

            viewModel.uiState.test {
                // 1. Initial state — questions empty
                val initial = awaitItem()
                assertTrue(initial.questions.isEmpty())

                // 2. Questions loaded
                advanceUntilIdle()
                val loaded = awaitItem()
                assertEquals(2, loaded.questions.size)
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

                // 6. Auto-advance → clears answer state, currentScreen transitions to Results
                advanceTimeBy(2_001L)
                advanceUntilIdle()
                val cleared = awaitItem()
                assertFalse(cleared.isAnswerRevealed)
                assertNull(cleared.selectedOptionIndex)

                cancelAndIgnoreRemainingEvents()
            }
        }

    // =========================================================================
    // 11. Navigation state + RestartGame effect — combined failure + restart
    // =========================================================================

    @Test
    fun `given quiz service throws, when load fails and RestartQuiz is fired, then screen is Error then RestartGame effect emitted`() =
        runTest(mainDispatcherRule.testScheduler) {
            coEvery { quizService.loadQuestions() } throws RuntimeException("Always fails")
            val viewModel = createViewModel()
            advanceUntilIdle()
            assertEquals(QuizViewModel.Effect.ShowError, viewModel.effects.replayCache.firstOrNull())

            viewModel.effects.test {
                awaitItem() // consume replayed ShowError (replay=1)
                viewModel.handleEvent(QuizViewModel.Event.RestartQuiz)
                advanceUntilIdle()
                assertTrue(awaitItem() is QuizViewModel.Effect.RestartGame)

                cancelAndIgnoreRemainingEvents()
            }
        }
}

