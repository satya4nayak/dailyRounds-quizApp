package com.r0.quiz.data.source;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class QuizApiService_Factory implements Factory<QuizApiService> {
  @Override
  public QuizApiService get() {
    return newInstance();
  }

  public static QuizApiService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static QuizApiService newInstance() {
    return new QuizApiService();
  }

  private static final class InstanceHolder {
    private static final QuizApiService_Factory INSTANCE = new QuizApiService_Factory();
  }
}
