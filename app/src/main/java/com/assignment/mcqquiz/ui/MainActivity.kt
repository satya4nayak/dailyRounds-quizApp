package com.assignment.mcqquiz.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.assignment.mcqquiz.feature.quiz.ui.activity.CategorySelectionActivity

/**
 * App entry point — immediately delegates to [CategorySelectionActivity].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, CategorySelectionActivity::class.java))
        finish()
    }
}
