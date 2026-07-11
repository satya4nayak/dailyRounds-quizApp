package com.assignment.mcqquiz.feature.quiz.domain.contract

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the [QuizAppContract] navigation state machine sealed interface.
 *
 * Although these are simple data objects, explicitly testing the contract
 * ensures refactors never accidentally merge or remove a navigation destination.
 */
class QuizAppContractTest {

    @Test
    fun `given QuizAppContract Splash, when type checked, then it is an instance of QuizAppContract`() {
        assertTrue(QuizAppContract.Splash is QuizAppContract)
    }

    @Test
    fun `given QuizAppContract Quiz, when type checked, then it is an instance of QuizAppContract`() {
        assertTrue(QuizAppContract.Quiz is QuizAppContract)
    }

    @Test
    fun `given QuizAppContract Results, when type checked, then it is an instance of QuizAppContract`() {
        assertTrue(QuizAppContract.Results is QuizAppContract)
    }

    @Test
    fun `given Splash and Quiz, when compared, then they are not equal`() {
        assertNotEquals(QuizAppContract.Splash, QuizAppContract.Quiz)
    }

    @Test
    fun `given Splash and Results, when compared, then they are not equal`() {
        assertNotEquals(QuizAppContract.Splash, QuizAppContract.Results)
    }

    @Test
    fun `given Quiz and Results, when compared, then they are not equal`() {
        assertNotEquals(QuizAppContract.Quiz, QuizAppContract.Results)
    }

    @Test
    fun `given Splash, when compared to itself, then they are equal`() {
        val a: QuizAppContract = QuizAppContract.Splash
        val b: QuizAppContract = QuizAppContract.Splash
        assertTrue(a == b)
    }

    @Test
    fun `given three distinct screen states, when exhaustively matched, then all branches are reachable`() {
        val states: List<QuizAppContract> = listOf(
            QuizAppContract.Splash,
            QuizAppContract.Quiz,
            QuizAppContract.Results
        )
        val visited = mutableSetOf<String>()
        states.forEach { state ->
            when (state) {
                QuizAppContract.Splash  -> visited.add("Splash")
                QuizAppContract.Quiz    -> visited.add("Quiz")
                QuizAppContract.Results -> visited.add("Results")
            }
        }
        assertTrue("Splash not visited",  "Splash"  in visited)
        assertTrue("Quiz not visited",    "Quiz"    in visited)
        assertTrue("Results not visited", "Results" in visited)
    }

    @Test
    fun `given contract states, when Splash is not Quiz, then splash screen check is correct`() {
        val current: QuizAppContract = QuizAppContract.Splash
        assertFalse(current is QuizAppContract.Quiz)
        assertFalse(current is QuizAppContract.Results)
    }
}

