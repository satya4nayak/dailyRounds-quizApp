package com.assignment.mcqquiz.data.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\b\u0010\u0007\u001a\u00020\u0006H\u0007\u00a8\u0006\b"}, d2 = {"Lcom/assignment/mcqquiz/data/di/DataModule;", "", "()V", "provideQuestionRepository", "Lcom/assignment/mcqquiz/domain/repository/QuestionRepository;", "apiService", "Lcom/assignment/mcqquiz/data/source/QuizApiService;", "provideQuizApiService", "data_debug"})
public final class DataModule {
    
    public DataModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.assignment.mcqquiz.data.source.QuizApiService provideQuizApiService() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.assignment.mcqquiz.domain.repository.QuestionRepository provideQuestionRepository(@org.jetbrains.annotations.NotNull()
    com.assignment.mcqquiz.data.source.QuizApiService apiService) {
        return null;
    }
}