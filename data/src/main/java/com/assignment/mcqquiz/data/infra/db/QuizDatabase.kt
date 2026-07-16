package com.assignment.mcqquiz.data.infra.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Single Room database for the app.
 */
@Database(entities = [CategoryProgressEntity::class], version = 4, exportSchema = true)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun categoryProgressDao(): CategoryProgressDao
}
