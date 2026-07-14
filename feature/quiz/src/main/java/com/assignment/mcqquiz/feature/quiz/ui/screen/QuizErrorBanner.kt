package com.assignment.mcqquiz.feature.quiz.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignment.mcqquiz.feature.quiz.R
import com.assignment.mcqquiz.feature.quiz.ui.theme.Background
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextPrimary
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextSecondary
import com.assignment.mcqquiz.feature.quiz.ui.theme.WrongRed

/**
 * Full-screen error state shown when a network or data error occurs.
 * Tapping **Retry** triggers a new API request.
 */
@Composable
fun QuizErrorBanner(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ── Warning icon ──────────────────────────────────────────────────────
        Text(
            text = "⚠️",
            fontSize = 72.sp,
            color = WrongRed,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Error message ─────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.quiz_error_message),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.quiz_error_subtitle),
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Retry button ──────────────────────────────────────────────────────
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WrongRed)
        ) {
            Text(
                text = stringResource(R.string.quiz_error_retry),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QuizErrorBannerPreview() {
    QuizAppTheme {
        QuizErrorBanner(onRetry = {})
    }
}
