package com.r0.quiz.feature.quiz.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\b\u0010\u0010\u001a\u00020\u000fH\u0002J\u000e\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010\u0014\u001a\u00020\u000fJ\u0006\u0010\u0015\u001a\u00020\u000fJ\u0006\u0010\u0016\u001a\u00020\u000fR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0017"}, d2 = {"Lcom/r0/quiz/feature/quiz/viewmodel/QuizViewModel;", "Landroidx/lifecycle/ViewModel;", "quizService", "Lcom/r0/quiz/domain/service/QuizService;", "(Lcom/r0/quiz/domain/service/QuizService;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/r0/quiz/feature/quiz/state/QuizUiState;", "autoAdvanceJob", "Lkotlinx/coroutines/Job;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "advanceQuestion", "", "loadQuestions", "onOptionSelected", "index", "", "onRestart", "onSkip", "onStreakCelebrationDismissed", "quiz_release"})
public final class QuizViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.r0.quiz.domain.service.QuizService quizService = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.r0.quiz.feature.quiz.state.QuizUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.r0.quiz.feature.quiz.state.QuizUiState> uiState = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job autoAdvanceJob;
    
    @javax.inject.Inject()
    public QuizViewModel(@org.jetbrains.annotations.NotNull()
    com.r0.quiz.domain.service.QuizService quizService) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.r0.quiz.feature.quiz.state.QuizUiState> getUiState() {
        return null;
    }
    
    private final void loadQuestions() {
    }
    
    public final void onOptionSelected(int index) {
    }
    
    public final void onSkip() {
    }
    
    public final void onStreakCelebrationDismissed() {
    }
    
    public final void onRestart() {
    }
    
    private final void advanceQuestion() {
    }
}