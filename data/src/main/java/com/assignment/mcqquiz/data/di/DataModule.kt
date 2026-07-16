package com.assignment.mcqquiz.data.di

import android.content.Context
import androidx.room.Room
import com.assignment.mcqquiz.data.infra.api.QuestionApiClient
import com.assignment.mcqquiz.data.infra.api.QuestionApiService
import com.assignment.mcqquiz.data.infra.api.QuizRetrofitService
import com.assignment.mcqquiz.data.infra.db.CategoryProgressDao
import com.assignment.mcqquiz.data.infra.db.QuizDatabase
import com.assignment.mcqquiz.data.infra.repository.CategoryProgressRepository
import com.assignment.mcqquiz.data.infra.repository.CategoryProgressRepositoryImpl
import com.assignment.mcqquiz.data.infra.repository.QuestionRepository
import com.assignment.mcqquiz.data.infra.repository.QuestionRepositoryImpl
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Dagger module for the :data layer.
 *
 * Binding chains:
 *   [QuestionApiClient]          ← [QuestionApiService]
 *   [QuestionRepository]         ← [QuestionRepositoryImpl]
 *   [CategoryProgressRepository] ← [CategoryProgressRepositoryImpl] ← [CategoryProgressDao]
 */
@Module
class DataModule {

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideQuizDatabase(context: Context): QuizDatabase =
        Room.databaseBuilder(context, QuizDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideCategoryProgressDao(db: QuizDatabase): CategoryProgressDao =
        db.categoryProgressDao()

    @Provides
    @Singleton
    fun provideQuestionApiClient(
        retrofitService: QuizRetrofitService,
        okHttpClient: OkHttpClient,
        json: Json
    ): QuestionApiClient = QuestionApiService(retrofitService, okHttpClient, json)

    @Provides
    @Singleton
    fun provideQuestionRepository(
        apiClient: QuestionApiClient
    ): QuestionRepository = QuestionRepositoryImpl(apiClient)

    @Provides
    @Singleton
    fun provideCategoryProgressRepository(
        dao: CategoryProgressDao
    ): CategoryProgressRepository = CategoryProgressRepositoryImpl(dao)

    companion object {
        private const val DB_NAME = "quiz.db"
    }
}
