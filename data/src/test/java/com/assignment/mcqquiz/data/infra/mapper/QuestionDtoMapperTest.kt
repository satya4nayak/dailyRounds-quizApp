package com.assignment.mcqquiz.data.infra.mapper

import com.assignment.mcqquiz.data.domain.model.Question
import com.assignment.mcqquiz.data.infra.dto.QuestionDto
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the [toDomainModel] extension function.
 *
 * The mapper is the critical boundary between the infrastructure and domain layers.
 * Every field mapping is verified explicitly so regressions are caught immediately.
 */
class QuestionDtoMapperTest {

    // ─── Happy path ───────────────────────────────────────────────────────────

    @Test
    fun `given a valid dto, when mapped, then id is preserved`() {
        val dto = QuestionDto(id = 7, question = "Q?", options = listOf("A", "B"), correctOptionIndex = 0)
        val domain: Question = dto.toDomainModel()
        assertEquals(7, domain.id)
    }

    @Test
    fun `given a valid dto, when mapped, then question field becomes text field`() {
        val dto = QuestionDto(id = 1, question = "What is DI?", options = listOf("A", "B"), correctOptionIndex = 1)
        val domain: Question = dto.toDomainModel()
        assertEquals("What is DI?", domain.question)
    }

    @Test
    fun `given a valid dto, when mapped, then options list is preserved by reference equality`() {
        val options = listOf("Option A", "Option B", "Option C", "Option D")
        val dto = QuestionDto(id = 2, question = "Q?", options = options, correctOptionIndex = 2)
        val domain: Question = dto.toDomainModel()
        assertEquals(options, domain.options)
    }

    @Test
    fun `given a valid dto, when mapped, then correctOptionIndex is preserved`() {
        val dto = QuestionDto(id = 3, question = "Q?", options = listOf("A", "B", "C"), correctOptionIndex = 2)
        val domain: Question = dto.toDomainModel()
        assertEquals(2, domain.correctOptionIndex)
    }

    @Test
    fun `given a valid dto, when mapped, then the resulting domain model has all correct fields`() {
        val dto = QuestionDto(
            id = 10,
            question = "Which keyword makes a Kotlin class final?",
            options = listOf("sealed", "open", "abstract", "data"),
            correctOptionIndex = 0
        )
        val expected = Question(
            id = 10,
            question = "Which keyword makes a Kotlin class final?",
            options = listOf("sealed", "open", "abstract", "data"),
            correctOptionIndex = 0
        )

        val actual = dto.toDomainModel()

        assertEquals(expected, actual)
    }

    // ─── Edge cases ───────────────────────────────────────────────────────────

    @Test
    fun `given a dto with empty question text, when mapped, then text is empty string`() {
        val dto = QuestionDto(id = 11, question = "", options = listOf("A"), correctOptionIndex = 0)
        val domain: Question = dto.toDomainModel()
        assertEquals("", domain.question)
    }

    @Test
    fun `given a dto with empty options list, when mapped, then domain options list is empty`() {
        val dto = QuestionDto(id = 12, question = "Q?", options = emptyList(), correctOptionIndex = 0)
        val domain: Question = dto.toDomainModel()
        assertEquals(emptyList<String>(), domain.options)
    }

    @Test
    fun `given a dto with correctOptionIndex 0, when mapped, then first option is the correct answer`() {
        val dto = QuestionDto(
            id = 13,
            question = "Q?",
            options = listOf("Correct", "Wrong1", "Wrong2", "Wrong3"),
            correctOptionIndex = 0
        )
        val domain: Question = dto.toDomainModel()
        assertEquals("Correct", domain.options[domain.correctOptionIndex])
    }

    @Test
    fun `given a list of dtos, when each is mapped, then all domain models are correct`() {
        val dtos = listOf(
            QuestionDto(id = 1, question = "Q1", options = listOf("A", "B"), correctOptionIndex = 0),
            QuestionDto(id = 2, question = "Q2", options = listOf("C", "D"), correctOptionIndex = 1),
            QuestionDto(id = 3, question = "Q3", options = listOf("E", "F"), correctOptionIndex = 0)
        )

        val domains = dtos.map { it.toDomainModel() }

        assertEquals(3, domains.size)
        assertEquals("Q1", domains[0].question)
        assertEquals("Q2", domains[1].question)
        assertEquals("Q3", domains[2].question)
        assertEquals(0, domains[0].correctOptionIndex)
        assertEquals(1, domains[1].correctOptionIndex)
    }

    @Test
    fun `given an empty list of dtos, when mapped, then result is empty list`() {
        val domains = emptyList<QuestionDto>().map { it.toDomainModel() }
        assertEquals(emptyList<Question>(), domains)
    }
}

