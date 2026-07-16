package com.assignment.mcqquiz.data.infra.mapper

import com.assignment.mcqquiz.data.domain.model.QuizCategory
import com.assignment.mcqquiz.data.infra.dto.CategoryDto
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the [toDomainModel] extension function on [CategoryDto].
 *
 * Verifies every field mapping at the infrastructure-to-domain boundary.
 */
class CategoryDtoMapperTest {

    // ─── Happy path ───────────────────────────────────────────────────────────

    @Test
    fun `given a valid dto, when mapped, then id is preserved`() {
        val dto = CategoryDto(id = "cat-android", title = "Android", description = "Desc", questionUrl = "https://example.com/q")
        val domain: QuizCategory = dto.toDomainModel()
        assertEquals("cat-android", domain.id)
    }

    @Test
    fun `given a valid dto, when mapped, then title is preserved`() {
        val dto = CategoryDto(id = "cat1", title = "Android Basics", description = "Desc", questionUrl = "https://example.com/q")
        val domain: QuizCategory = dto.toDomainModel()
        assertEquals("Android Basics", domain.title)
    }

    @Test
    fun `given a valid dto, when mapped, then description is preserved`() {
        val dto = CategoryDto(id = "cat1", title = "Android", description = "Learn Android fundamentals", questionUrl = "https://example.com/q")
        val domain: QuizCategory = dto.toDomainModel()
        assertEquals("Learn Android fundamentals", domain.description)
    }

    @Test
    fun `given a valid dto, when mapped, then questionUrl is preserved`() {
        val dto = CategoryDto(id = "cat1", title = "Android", description = "Desc", questionUrl = "https://example.com/android.json")
        val domain: QuizCategory = dto.toDomainModel()
        assertEquals("https://example.com/android.json", domain.questionUrl)
    }

    @Test
    fun `given a valid dto, when mapped, then all fields match the expected domain model`() {
        val dto = CategoryDto(
            id = "cat-kotlin",
            title = "Kotlin",
            description = "Kotlin coroutines and flows",
            questionUrl = "https://example.com/kotlin.json"
        )
        val expected = QuizCategory(
            id = "cat-kotlin",
            title = "Kotlin",
            description = "Kotlin coroutines and flows",
            questionUrl = "https://example.com/kotlin.json"
        )
        assertEquals(expected, dto.toDomainModel())
    }

    // ─── Edge cases ───────────────────────────────────────────────────────────

    @Test
    fun `given a dto with empty title, when mapped, then domain title is empty`() {
        val dto = CategoryDto(id = "cat1", title = "", description = "Desc", questionUrl = "https://example.com")
        val domain: QuizCategory = dto.toDomainModel()
        assertEquals("", domain.title)
    }

    @Test
    fun `given a dto with empty description, when mapped, then domain description is empty`() {
        val dto = CategoryDto(id = "cat1", title = "Title", description = "", questionUrl = "https://example.com")
        val domain: QuizCategory = dto.toDomainModel()
        assertEquals("", domain.description)
    }

    @Test
    fun `given a list of dtos, when each is mapped, then all domain models are correct`() {
        val dtos = listOf(
            CategoryDto(id = "c1", title = "Cat 1", description = "D1", questionUrl = "https://example.com/c1"),
            CategoryDto(id = "c2", title = "Cat 2", description = "D2", questionUrl = "https://example.com/c2"),
            CategoryDto(id = "c3", title = "Cat 3", description = "D3", questionUrl = "https://example.com/c3")
        )
        val domains = dtos.map { it.toDomainModel() }
        assertEquals(3, domains.size)
        assertEquals("c1", domains[0].id)
        assertEquals("c2", domains[1].id)
        assertEquals("c3", domains[2].id)
        assertEquals("Cat 1", domains[0].title)
        assertEquals("https://example.com/c2", domains[1].questionUrl)
    }

    @Test
    fun `given an empty list of dtos, when mapped, then result is empty list`() {
        val domains = emptyList<CategoryDto>().map { it.toDomainModel() }
        assertEquals(emptyList<QuizCategory>(), domains)
    }
}

