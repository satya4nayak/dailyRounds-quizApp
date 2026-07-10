package com.assignment.mcqquiz.domain.repository

import com.assignment.mcqquiz.domain.model.Question

interface QuestionRepository {
    suspend fun getQuestions(): List<Question>
}

