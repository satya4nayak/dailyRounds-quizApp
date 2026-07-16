package com.assignment.mcqquiz.feature.quiz.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.assignment.mcqquiz.data.domain.model.CategoryStatus
import com.assignment.mcqquiz.data.domain.model.QuizCategory
import com.assignment.mcqquiz.feature.quiz.R
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme
import com.assignment.mcqquiz.feature.quiz.domain.state.CategoryListUiState
import com.assignment.mcqquiz.feature.quiz.domain.state.CategoryWithProgress
import com.assignment.mcqquiz.feature.quiz.ui.component.ScreenTopBar
import com.assignment.mcqquiz.feature.quiz.ui.theme.Amber
import com.assignment.mcqquiz.feature.quiz.ui.theme.Background
import com.assignment.mcqquiz.feature.quiz.ui.theme.BorderColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.CorrectGreen
import com.assignment.mcqquiz.feature.quiz.ui.theme.Primary
import com.assignment.mcqquiz.feature.quiz.ui.theme.SurfaceColor
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextPrimary
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextSecondary

/**
 * Displays all available quiz modules with their persisted progress.
 * Fully stateless — events flow up via [onCategorySelected].
 */
@Composable
fun CategoryListScreen(
    state: CategoryListUiState,
    onCategorySelected: (categoryId: String, questionUrl: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        ScreenTopBar(backgroundColor = Primary)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.quiz_categories_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.3).sp
            )
            Text(
                text = stringResource(R.string.quiz_categories_subtitle),
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.categories) { item ->
                CategoryCard(
                    item = item,
                    onTap = { onCategorySelected(item.category.id, item.category.questionUrl) }
                )
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun CategoryCard(
    item: CategoryWithProgress,
    onTap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceColor)
            .border(1.dp, BorderColor, RoundedCornerShape(18.dp))
                .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        // ── Title row ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.category.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            StatusButton(status = item.status, onTap = onTap)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Description ───────────────────────────────────────────────────────
        if (item.category.description.isNotBlank()) {
            Text(
                text = item.category.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // ── Progress summary ──────────────────────────────────────────────────
        val progressText = when (item.status) {
            CategoryStatus.NOT_STARTED -> stringResource(R.string.category_status_not_started)
            CategoryStatus.IN_PROGRESS -> stringResource(R.string.category_status_in_progress)
            CategoryStatus.COMPLETED   -> stringResource(R.string.category_status_completed)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusDot(status = item.status)
            Text(
                text = progressText,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun StatusButton(status: CategoryStatus, onTap: () -> Unit) {
    val (label, bg, textColor) = when (status) {
        CategoryStatus.NOT_STARTED -> Triple(stringResource(R.string.category_button_start), Primary, Color.White)
        CategoryStatus.IN_PROGRESS -> Triple(stringResource(R.string.category_button_continue), Amber, Color.White)
        CategoryStatus.COMPLETED   -> Triple(stringResource(R.string.category_button_review), CorrectGreen, Color.White)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable { onTap() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun StatusDot(status: CategoryStatus) {
    val color = when (status) {
        CategoryStatus.NOT_STARTED -> TextSecondary
        CategoryStatus.IN_PROGRESS -> Amber
        CategoryStatus.COMPLETED   -> CorrectGreen
    }
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(RoundedCornerShape(50))
            .background(color)
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewCategories = listOf(
    CategoryWithProgress(
        category = QuizCategory("1", "Android Basics", "Fundamentals of Android development", ""),
        status = CategoryStatus.NOT_STARTED
    ),
    CategoryWithProgress(
        category = QuizCategory("2", "Jetpack Compose", "Modern UI toolkit for Android", ""),
        status = CategoryStatus.IN_PROGRESS
    ),
    CategoryWithProgress(
        category = QuizCategory("3", "Activity & Lifecycle", "Understanding activity lifecycle and related components", ""),
        status = CategoryStatus.COMPLETED
    ),
    CategoryWithProgress(
        category = QuizCategory("4", "Networking", "Handling API calls, Retrofit, and caching", ""),
        status = CategoryStatus.NOT_STARTED
    ),
    CategoryWithProgress(
        category = QuizCategory("5", "Architecture Patterns", "MVVM, MVI, Clean Architecture, and more", ""),
        status = CategoryStatus.COMPLETED
    )
)

@Preview(showSystemUi = true)
@Composable
private fun CategoryListScreenPreview() {
    QuizAppTheme {
        CategoryListScreen(
            state = CategoryListUiState(categories = previewCategories),
            onCategorySelected = { _, _ -> }
        )
    }
}
