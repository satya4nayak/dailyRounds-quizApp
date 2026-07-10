package com.assignment.mcqquiz.data.repository;

import com.assignment.mcqquiz.data.source.QuizApiService;
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
public final class QuestionRepositoryImpl_Factory implements Factory<QuestionRepositoryImpl> {
  private final Provider<QuizApiService> apiServiceProvider;

  public QuestionRepositoryImpl_Factory(Provider<QuizApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public QuestionRepositoryImpl get() {
    return newInstance(apiServiceProvider.get());
  }

  public static QuestionRepositoryImpl_Factory create(Provider<QuizApiService> apiServiceProvider) {
    return new QuestionRepositoryImpl_Factory(apiServiceProvider);
  }

  public static QuestionRepositoryImpl newInstance(QuizApiService apiService) {
    return new QuestionRepositoryImpl(apiService);
  }
}
