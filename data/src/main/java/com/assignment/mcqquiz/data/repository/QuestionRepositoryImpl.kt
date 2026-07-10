package com.assignment.mcqquiz.data.repository

import com.assignment.mcqquiz.data.mapper.toDomain
import com.assignment.mcqquiz.data.source.QuizApiService
import com.assignment.mcqquiz.domain.model.Question
import com.assignment.mcqquiz.domain.repository.QuestionRepository
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val apiService: QuizApiService
) : QuestionRepository {
    override suspend fun getQuestions(): List<Question> =
        apiService.fetchQuestions().map { it.toDomain() }
}

