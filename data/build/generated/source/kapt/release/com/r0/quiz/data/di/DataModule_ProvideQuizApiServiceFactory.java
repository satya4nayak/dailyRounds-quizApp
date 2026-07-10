package com.r0.quiz.data.di;

import com.r0.quiz.data.source.QuizApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class DataModule_ProvideQuizApiServiceFactory implements Factory<QuizApiService> {
  private final DataModule module;

  public DataModule_ProvideQuizApiServiceFactory(DataModule module) {
    this.module = module;
  }

  @Override
  public QuizApiService get() {
    return provideQuizApiService(module);
  }

  public static DataModule_ProvideQuizApiServiceFactory create(DataModule module) {
    return new DataModule_ProvideQuizApiServiceFactory(module);
  }

  public static QuizApiService provideQuizApiService(DataModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideQuizApiService());
  }
}
