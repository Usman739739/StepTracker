package com.steptracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.steptracker.app.ui.navigation.StepTrackerNavGraph
import com.steptracker.app.ui.theme.StepTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the StepTracker app.
 * Uses Jetpack Compose for all UI with Hilt for dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StepTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StepTrackerNavGraph()
                }
            }
        }
    }
}
