package com.r0.quiz.data.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\b\u0010\u0007\u001a\u00020\u0006H\u0007\u00a8\u0006\b"}, d2 = {"Lcom/r0/quiz/data/di/DataModule;", "", "()V", "provideQuestionRepository", "Lcom/r0/quiz/domain/repository/QuestionRepository;", "apiService", "Lcom/r0/quiz/data/source/QuizApiService;", "provideQuizApiService", "data_release"})
public final class DataModule {
    
    public DataModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.r0.quiz.data.source.QuizApiService provideQuizApiService() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.r0.quiz.domain.repository.QuestionRepository provideQuestionRepository(@org.jetbrains.annotations.NotNull()
    com.r0.quiz.data.source.QuizApiService apiService) {
        return null;
    }
}