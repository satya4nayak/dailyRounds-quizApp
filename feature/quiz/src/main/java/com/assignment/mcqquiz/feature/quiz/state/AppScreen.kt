package com.assignment.mcqquiz.feature.quiz.state

sealed interface AppScreen {
    data object Splash : AppScreen
    data object Quiz : AppScreen
    data object Results : AppScreen
}

