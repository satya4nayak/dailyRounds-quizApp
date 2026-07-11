package com.assignment.mcqquiz.data.infra.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Unit tests for the [QuestionDto] data transfer object.
 *
 * Confirms correct property storage and equality behaviour before the mapper
 * transforms this into a domain model.
 */
class QuestionDtoTest {

    private val sampleDto = QuestionDto(
        id = 42,
        question = "Which Android class manages the back-stack?",
        options = listOf("Activity", "FragmentManager", "NavController", "ViewModel"),
        correctOptionIndex = 1
    )

    @Test
    fun `given a dto, when id is accessed, then correct id is returned`() {
        assertEquals(42, sampleDto.id)
    }

    @Test
    fun `given a dto, when question is accessed, then correct question text is returned`() {
        assertEquals("Which Android class manages the back-stack?", sampleDto.question)
    }

    @Test
    fun `given a dto, when options are accessed, then full options list is returned`() {
        assertEquals(
            listOf("Activity", "FragmentManager", "NavController", "ViewModel"),
            sampleDto.options
        )
    }

    @Test
    fun `given a dto, when correctOptionIndex is accessed, then correct index is returned`() {
        assertEquals(1, sampleDto.correctOptionIndex)
    }

    @Test
    fun `given two dtos with identical fields, when compared, then they are equal`() {
        val duplicate = QuestionDto(
            id = 42,
            question = "Which Android class manages the back-stack?",
            options = listOf("Activity", "FragmentManager", "NavController", "ViewModel"),
            correctOptionIndex = 1
        )
        assertEquals(sampleDto, duplicate)
    }

    @Test
    fun `given two dtos with different question texts, when compared, then they are not equal`() {
        val different = sampleDto.copy(question = "Different question?")
        assertNotEquals(sampleDto, different)
    }

    @Test
    fun `given a dto, when copied with new correctOptionIndex, then only correctOptionIndex changes`() {
        val updated = sampleDto.copy(correctOptionIndex = 3)
        assertEquals(3, updated.correctOptionIndex)
        assertEquals(sampleDto.id, updated.id)
        assertEquals(sampleDto.question, updated.question)
        assertEquals(sampleDto.options, updated.options)
    }
}

