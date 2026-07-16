package com.assignment.mcqquiz.feature.quiz.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignment.mcqquiz.feature.quiz.R
import com.assignment.mcqquiz.feature.quiz.ui.theme.Background
import com.assignment.mcqquiz.feature.quiz.ui.theme.QuizAppTheme
import com.assignment.mcqquiz.feature.quiz.ui.theme.TextPrimary

/**
 * Top bar supporting devices with camera cutouts.
 * [backgroundColor] defaults to the app background; pass a different color to theme a specific screen.
 * When [onBack] is provided a tappable ← back arrow is shown on the left.
 */
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ScreenTopBar(
    backgroundColor: Color = Background,
    onBack: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
            .height(50.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        if (onBack != null) {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp, bottom = 6.dp)
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Transparent)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.top_bar_back),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun ScreenTopBarPreview() {
    QuizAppTheme {
        ScreenTopBar(onBack = {})
    }
}





