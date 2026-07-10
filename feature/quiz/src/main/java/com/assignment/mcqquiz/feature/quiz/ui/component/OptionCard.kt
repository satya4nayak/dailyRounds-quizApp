package com.assignment.mcqquiz.feature.quiz.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.assignment.mcqquiz.feature.quiz.ui.theme.CorrectGreen
import com.assignment.mcqquiz.feature.quiz.ui.theme.Surface
import com.assignment.mcqquiz.feature.quiz.ui.theme.WrongRed

@Composable
fun OptionCard(
    optionText: String,
    optionIndex: Int,
    selectedOptionIndex: Int?,
    correctOptionIndex: Int,
    isAnswerRevealed: Boolean,
    onClick: (Int) -> Unit
) {
    val isSelected = selectedOptionIndex == optionIndex
    val isCorrect = optionIndex == correctOptionIndex

    val backgroundColor by animateColorAsState(
        targetValue = when {
            !isAnswerRevealed -> Surface
            isSelected && isCorrect -> CorrectGreen
            isSelected && !isCorrect -> WrongRed
            !isSelected && isCorrect -> Surface
            else -> Surface
        },
        animationSpec = tween(durationMillis = 300),
        label = "optionBackground"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !isAnswerRevealed -> Color.Transparent
            isSelected && isCorrect -> CorrectGreen
            isSelected && !isCorrect -> WrongRed
            !isSelected && isCorrect -> CorrectGreen
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 300),
        label = "optionBorder"
    )

    val showIcon = isAnswerRevealed && isSelected

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = if (isAnswerRevealed && !isSelected && isCorrect) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !isAnswerRevealed) { onClick(optionIndex) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = optionText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            if (showIcon) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = if (isCorrect) "Correct" else "Wrong",
                    tint = Color.White
                )
            }
        }
    }
}

