package com.assignment.mcqquiz.feature.quiz.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.assignment.mcqquiz.feature.quiz.di.QuizFeatureDependencies
import com.assignment.mcqquiz.feature.quiz.ui.factory.QuizViewModelFactory
import com.assignment.mcqquiz.feature.quiz.ui.screen.CategoryListScreen
import com.assignment.mcqquiz.feature.quiz.ui.screen.NetworkLoaderScreen
import com.assignment.mcqquiz.feature.quiz.ui.screen.QuizErrorBanner
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme
import com.assignment.mcqquiz.feature.quiz.ui.viewmodel.CategoryListViewModel

/**
 * Displays the category list and launches [QuizActivity] when a category is selected.
 * Refreshes the list from the local DB whenever [QuizActivity] returns.
 */
class CategorySelectionActivity : ComponentActivity() {

    private lateinit var categoryListVm: CategoryListViewModel

    private val quizLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // QuizActivity finished — refresh progress from DB (no new network call)
        categoryListVm.handleEvent(CategoryListViewModel.Event.Retry)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deps = application as QuizFeatureDependencies
        categoryListVm = ViewModelProvider(
            this,
            QuizViewModelFactory(
                owner           = this,
                quizService     = deps.quizService(),
                categoryService = deps.categoryService()
                // categoryId / questionUrl left at defaults — not needed for CategoryListViewModel
            )
        )[CategoryListViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            val categoryListState by categoryListVm.uiState.collectAsStateWithLifecycle()
            val categoryListEffect by categoryListVm.effects.collectAsStateWithLifecycle(
                initialValue = categoryListVm.effects.replayCache.firstOrNull()
                    ?: CategoryListViewModel.Effect.ShowLoader
            )

            LaunchedEffect(Unit) {
                categoryListVm.handleEvent(CategoryListViewModel.Event.InitialLoad)
                categoryListVm.effects.collect { effect ->
                    if (effect is CategoryListViewModel.Effect.NavigateToQuiz) {
                        quizLauncher.launch(
                            QuizActivity.createIntent(
                                context     = this@CategorySelectionActivity,
                                categoryId  = effect.categoryId,
                                questionUrl = effect.questionUrl
                            )
                        )
                    }
                }
            }

            BackHandler { finish() }

            QuizAppTheme {
                when (categoryListEffect) {
                    CategoryListViewModel.Effect.ShowLoader -> NetworkLoaderScreen()
                    CategoryListViewModel.Effect.ShowError  -> QuizErrorBanner(
                        onRetry = { categoryListVm.handleEvent(CategoryListViewModel.Event.Retry) },
                        onBack  = { finish() }
                    )
                    CategoryListViewModel.Effect.ShowList,
                    is CategoryListViewModel.Effect.NavigateToQuiz -> CategoryListScreen(
                        state = categoryListState,
                        onCategorySelected = { id, url ->
                            categoryListVm.handleEvent(
                                CategoryListViewModel.Event.CategorySelected(id, url)
                            )
                        }
                    )
                }
            }
        }
    }
}



