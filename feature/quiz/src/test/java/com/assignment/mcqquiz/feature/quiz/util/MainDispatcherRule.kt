package com.assignment.mcqquiz.feature.quiz.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit 4 [TestWatcher] rule that replaces [Dispatchers.Main] with a
 * [StandardTestDispatcher] before every test and restores it afterwards.
 *
 * Sharing the [testScheduler] with [kotlinx.coroutines.test.runTest] ensures
 * that [Dispatchers.Main] (used by [androidx.lifecycle.viewModelScope]) and the
 * test's virtual clock are perfectly in sync — so [advanceUntilIdle],
 * [advanceTimeBy], and Turbine all operate on the same timeline.
 *
 * Usage in test class:
 * ```kotlin
 * @get:Rule val mainDispatcherRule = MainDispatcherRule()
 *
 * @Test
 * fun someTest() = runTest(mainDispatcherRule.testScheduler) { ... }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testScheduler: TestCoroutineScheduler = TestCoroutineScheduler(),
    val testDispatcher: TestDispatcher = StandardTestDispatcher(testScheduler)
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

