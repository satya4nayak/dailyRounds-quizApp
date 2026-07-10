package com.r0.quiz.data.di;

import com.r0.quiz.data.source.QuizApiService;
import com.r0.quiz.domain.repository.QuestionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DataModule_ProvideQuestionRepositoryFactory implements Factory<QuestionRepository> {
  private final DataModule module;

  private final Provider<QuizApiService> apiServiceProvider;

  public DataModule_ProvideQuestionRepositoryFactory(DataModule module,
      Provider<QuizApiService> apiServiceProvider) {
    this.module = module;
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public QuestionRepository get() {
    return provideQuestionRepository(module, apiServiceProvider.get());
  }

  public static DataModule_ProvideQuestionRepositoryFactory create(DataModule module,
      Provider<QuizApiService> apiServiceProvider) {
    return new DataModule_ProvideQuestionRepositoryFactory(module, apiServiceProvider);
  }

  public static QuestionRepository provideQuestionRepository(DataModule instance,
      QuizApiService apiService) {
    return Preconditions.checkNotNullFromProvides(instance.provideQuestionRepository(apiService));
  }
}
