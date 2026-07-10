package com.assignment.mcqquiz.feature.quiz.state;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\"\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B{\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\f\u001a\u00020\t\u0012\b\b\u0002\u0010\r\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\t\u00a2\u0006\u0002\u0010\u0012J\u000f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010#\u001a\u00020\u0010H\u00c6\u0003J\t\u0010$\u001a\u00020\tH\u00c6\u0003J\t\u0010%\u001a\u00020\u0006H\u00c6\u0003J\u0010\u0010&\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u001eJ\t\u0010'\u001a\u00020\tH\u00c6\u0003J\t\u0010(\u001a\u00020\u0006H\u00c6\u0003J\t\u0010)\u001a\u00020\u0006H\u00c6\u0003J\t\u0010*\u001a\u00020\tH\u00c6\u0003J\t\u0010+\u001a\u00020\u0006H\u00c6\u0003J\t\u0010,\u001a\u00020\u0006H\u00c6\u0003J\u0084\u0001\u0010-\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00062\b\b\u0002\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\t2\b\b\u0002\u0010\r\u001a\u00020\u00062\b\b\u0002\u0010\u000e\u001a\u00020\u00062\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\tH\u00c6\u0001\u00a2\u0006\u0002\u0010.J\u0013\u0010/\u001a\u00020\t2\b\u00100\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00101\u001a\u00020\u0006H\u00d6\u0001J\t\u00102\u001a\u000203H\u00d6\u0001R\u0011\u0010\r\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0011\u0010\n\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0017R\u0011\u0010\u0011\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0017R\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0015\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\f\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0017R\u0011\u0010\u000e\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0014\u00a8\u00064"}, d2 = {"Lcom/assignment/mcqquiz/feature/quiz/state/QuizUiState;", "", "questions", "", "Lcom/assignment/mcqquiz/domain/model/Question;", "currentIndex", "", "selectedOptionIndex", "isAnswerRevealed", "", "currentStreak", "longestStreak", "showStreakCelebration", "correctCount", "skippedCount", "screen", "Lcom/assignment/mcqquiz/feature/quiz/state/AppScreen;", "isLoading", "(Ljava/util/List;ILjava/lang/Integer;ZIIZIILcom/assignment/mcqquiz/feature/quiz/state/AppScreen;Z)V", "getCorrectCount", "()I", "getCurrentIndex", "getCurrentStreak", "()Z", "getLongestStreak", "getQuestions", "()Ljava/util/List;", "getScreen", "()Lcom/assignment/mcqquiz/feature/quiz/state/AppScreen;", "getSelectedOptionIndex", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getShowStreakCelebration", "getSkippedCount", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/util/List;ILjava/lang/Integer;ZIIZIILcom/assignment/mcqquiz/feature/quiz/state/AppScreen;Z)Lcom/assignment/mcqquiz/feature/quiz/state/QuizUiState;", "equals", "other", "hashCode", "toString", "", "quiz_debug"})
public final class QuizUiState {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.assignment.mcqquiz.domain.model.Question> questions = null;
    private final int currentIndex = 0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer selectedOptionIndex = null;
    private final boolean isAnswerRevealed = false;
    private final int currentStreak = 0;
    private final int longestStreak = 0;
    private final boolean showStreakCelebration = false;
    private final int correctCount = 0;
    private final int skippedCount = 0;
    @org.jetbrains.annotations.NotNull()
    private final com.assignment.mcqquiz.feature.quiz.state.AppScreen screen = null;
    private final boolean isLoading = false;
    
    public QuizUiState(@org.jetbrains.annotations.NotNull()
    java.util.List<com.assignment.mcqquiz.domain.model.Question> questions, int currentIndex, @org.jetbrains.annotations.Nullable()
    java.lang.Integer selectedOptionIndex, boolean isAnswerRevealed, int currentStreak, int longestStreak, boolean showStreakCelebration, int correctCount, int skippedCount, @org.jetbrains.annotations.NotNull()
    com.assignment.mcqquiz.feature.quiz.state.AppScreen screen, boolean isLoading) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.assignment.mcqquiz.domain.model.Question> getQuestions() {
        return null;
    }
    
    public final int getCurrentIndex() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getSelectedOptionIndex() {
        return null;
    }
    
    public final boolean isAnswerRevealed() {
        return false;
    }
    
    public final int getCurrentStreak() {
        return 0;
    }
    
    public final int getLongestStreak() {
        return 0;
    }
    
    public final boolean getShowStreakCelebration() {
        return false;
    }
    
    public final int getCorrectCount() {
        return 0;
    }
    
    public final int getSkippedCount() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.assignment.mcqquiz.feature.quiz.state.AppScreen getScreen() {
        return null;
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    public QuizUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.assignment.mcqquiz.domain.model.Question> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.assignment.mcqquiz.feature.quiz.state.AppScreen component10() {
        return null;
    }
    
    public final boolean component11() {
        return false;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    public final int component5() {
        return 0;
    }
    
    public final int component6() {
        return 0;
    }
    
    public final boolean component7() {
        return false;
    }
    
    public final int component8() {
        return 0;
    }
    
    public final int component9() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.assignment.mcqquiz.feature.quiz.state.QuizUiState copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.assignment.mcqquiz.domain.model.Question> questions, int currentIndex, @org.jetbrains.annotations.Nullable()
    java.lang.Integer selectedOptionIndex, boolean isAnswerRevealed, int currentStreak, int longestStreak, boolean showStreakCelebration, int correctCount, int skippedCount, @org.jetbrains.annotations.NotNull()
    com.assignment.mcqquiz.feature.quiz.state.AppScreen screen, boolean isLoading) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}