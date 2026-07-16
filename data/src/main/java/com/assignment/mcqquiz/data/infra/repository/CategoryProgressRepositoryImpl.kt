package com.assignment.mcqquiz.data.infra.repository

import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.service.CategoryProgressSnapshot
import com.assignment.mcqquiz.data.infra.db.CategoryProgressDao
import com.assignment.mcqquiz.data.infra.db.CategoryProgressEntity
import javax.inject.Inject

class CategoryProgressRepositoryImpl @Inject constructor(
    private val dao: CategoryProgressDao
) : CategoryProgressRepository {

    override suspend fun upsert(categoryId: String, status: CategoryStatus, lastQuestionId: Int?) {
        val existing = dao.getById(categoryId)
        dao.upsert(
            CategoryProgressEntity(
                categoryId = categoryId,
                status = status.name,
                lastQuestionId = lastQuestionId,
                currentStreak = 0,
                allTimeLongestStreak = existing?.allTimeLongestStreak ?: 0
            )
        )
    }

    override suspend fun updateLastQuestionId(categoryId: String, questionId: Int) =
        dao.updateLastQuestionId(categoryId, questionId)

    override suspend fun updateStreaks(categoryId: String, currentStreak: Int, allTimeLongestStreak: Int) =
        dao.updateStreaks(categoryId, currentStreak, allTimeLongestStreak)

    override suspend fun getById(categoryId: String): CategoryProgressSnapshot? =
        dao.getById(categoryId)?.toSnapshot()

    override suspend fun getAll(): List<CategoryProgressSnapshot> =
        dao.getAll().map { it.toSnapshot() }

    private fun CategoryProgressEntity.toSnapshot() = CategoryProgressSnapshot(
        categoryId = categoryId,
        status = CategoryStatus.valueOf(status),
        lastQuestionId = lastQuestionId,
        currentStreak = currentStreak,
        allTimeLongestStreak = allTimeLongestStreak
    )
}
