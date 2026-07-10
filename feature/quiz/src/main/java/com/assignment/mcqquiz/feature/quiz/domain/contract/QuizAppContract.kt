package com.assignment.mcqquiz.feature.quiz.domain.contract

/**
 * Defines the navigation state machine contract for the Quiz feature.
 *
 * Each object represents a distinct, navigable screen destination.
 * The ViewModel drives transitions between these states; the UI reacts to them.
 *
 * Lives in domain because it is pure Kotlin and represents the app's
 * logical state — not a UI implementation detail.
 */
sealed interface QuizAppContract {
    data object Splash : QuizAppContract
    data object Quiz : QuizAppContract
    data object Results : QuizAppContract
}

