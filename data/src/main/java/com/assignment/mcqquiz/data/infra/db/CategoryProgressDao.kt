package com.assignment.mcqquiz.data.infra.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO for reading and writing [CategoryProgressEntity] records.
 */
@Dao
interface CategoryProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CategoryProgressEntity)

    /** Targeted update — only touches lastQuestionId, leaves all other columns intact. */
    @Query("UPDATE category_progress SET lastQuestionId = :questionId WHERE categoryId = :categoryId")
    suspend fun updateLastQuestionId(categoryId: String, questionId: Int)

    /** Targeted update — persists live streak counters after each answered question. */
    @Query("UPDATE category_progress SET currentStreak = :currentStreak, allTimeLongestStreak = :allTimeLongestStreak WHERE categoryId = :categoryId")
    suspend fun updateStreaks(categoryId: String, currentStreak: Int, allTimeLongestStreak: Int)

    @Query("SELECT * FROM category_progress WHERE categoryId = :categoryId")
    suspend fun getById(categoryId: String): CategoryProgressEntity?

    @Query("SELECT * FROM category_progress")
    suspend fun getAll(): List<CategoryProgressEntity>
}
