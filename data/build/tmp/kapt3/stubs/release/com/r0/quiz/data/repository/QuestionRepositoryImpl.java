package com.r0.quiz.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0096@\u00a2\u0006\u0002\u0010\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/r0/quiz/data/repository/QuestionRepositoryImpl;", "Lcom/r0/quiz/domain/repository/QuestionRepository;", "apiService", "Lcom/r0/quiz/data/source/QuizApiService;", "(Lcom/r0/quiz/data/source/QuizApiService;)V", "getQuestions", "", "Lcom/r0/quiz/domain/model/Question;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "data_release"})
public final class QuestionRepositoryImpl implements com.r0.quiz.domain.repository.QuestionRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.r0.quiz.data.source.QuizApiService apiService = null;
    
    @javax.inject.Inject()
    public QuestionRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.r0.quiz.data.source.QuizApiService apiService) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getQuestions(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.r0.quiz.domain.model.Question>> $completion) {
        return null;
    }
}