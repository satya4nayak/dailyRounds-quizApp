package com.assignment.mcqquiz.feature.quiz.viewmodel;

import com.assignment.mcqquiz.domain.service.QuizService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class QuizViewModel_Factory implements Factory<QuizViewModel> {
  private final Provider<QuizService> quizServiceProvider;

  public QuizViewModel_Factory(Provider<QuizService> quizServiceProvider) {
    this.quizServiceProvider = quizServiceProvider;
  }

  @Override
  public QuizViewModel get() {
    return newInstance(quizServiceProvider.get());
  }

  public static QuizViewModel_Factory create(Provider<QuizService> quizServiceProvider) {
    return new QuizViewModel_Factory(quizServiceProvider);
  }

  public static QuizViewModel newInstance(QuizService quizService) {
    return new QuizViewModel(quizService);
  }
}
