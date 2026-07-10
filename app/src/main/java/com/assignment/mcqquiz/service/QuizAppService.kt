package com.assignment.mcqquiz.service

import com.assignment.mcqquiz.domain.model.Question
import com.assignment.mcqquiz.domain.repository.QuestionRepository
import com.assignment.mcqquiz.domain.service.QuizService

class QuizAppService(
    private val repository: QuestionRepository
) : QuizService {
    override suspend fun loadQuestions(): List<Question> = repository.getQuestions()
}

