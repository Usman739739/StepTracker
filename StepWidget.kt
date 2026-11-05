package com.steptracker.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.steptracker.app.MainActivity
import com.steptracker.app.data.local.PreferencesManager
import com.steptracker.app.service.StepCounterService
import com.steptracker.app.service.StepCounterService.TrackingStatus
import com.steptracker.app.util.PermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Elegant home-screen widget built with Jetpack Glance.
 * 
 * Design:
 * - Dark rounded card with subtle depth
 * - Linear progress bar showing goal completion
 * - Large step count display
 * - Tracking status with colored indicator dot
 * - Tap anywhere to open the app
 */
class StepWidget : GlanceAppWidget() {

    companion object {
        private val widgetScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        /**
         * Trigger widget update from suspend context (e.g., inside a coroutine).
         */
        suspend fun updateAsync(context: Context) {
            val glanceIds = GlanceAppWidgetManager(context)
                .getGlanceIds(StepWidget::class.java)
            glanceIds.forEach { id ->
                StepWidget().update(context, id)
            }
        }

        /**
         * Fire-and-forget widget update for Service/Receiver contexts.
         */
        fun updateAll(context: Context) {
            widgetScope.launch {
                updateAsync(context)
            }
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Load current state
        val prefsManager = PreferencesManager(context)
        val prefs = prefsManager.preferencesFlow.first()

        val steps = if (StepCounterService.isRunning.value) {
            StepCounterService.currentSteps.value
        } else {
            prefs.todaySteps
        }

        val goal = prefs.dailyGoal
        val progress = if (goal > 0) (steps.toFloat() / goal).coerceIn(0f, 1f) else 0f
        val isRunning = StepCounterService.isRunning.value
        val isPaused = StepCounterService.isPaused.value
        val status = StepCounterService.trackingStatus.value

        provideContent {
            WidgetContent(
                steps = steps,
                goal = goal,
                progress = progress,
                isRunning = isRunning,
                isPaused = isPaused,
                status = status
            )
        }
    }
}

@Composable
private fun WidgetContent(
    steps: Int,
    goal: Int,
    progress: Float,
    isRunning: Boolean,
    isPaused: Boolean,
    status: TrackingStatus
) {
    val (statusText, statusColor) = when {
        !isRunning -> "Not Tracking" to ColorProvider(android.graphics.Color.parseColor("#9E9E9E"))
        status == TrackingStatus.BIKING -> "Biking — Paused" to ColorProvider(android.graphics.Color.parseColor("#FF9800"))
        status == TrackingStatus.DRIVING -> "Driving — Paused" to ColorProvider(android.graphics.Color.parseColor("#F44336"))
        isPaused -> "Paused" to ColorProvider(android.graphics.Color.parseColor("#9E9E9E"))
        else -> "Walking" to ColorProvider(android.graphics.Color.parseColor("#4CAF50"))
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(24.dp)
            .background(ColorProvider(android.graphics.Color.parseColor("#1A1A2E")))
            .padding(16.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Step Count ────────────────────────────────────────
            Text(
                text = PermissionHelper.formatStepCount(steps),
                style = TextStyle(
                    color = ColorProvider(android.graphics.Color.WHITE),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "steps",
                style = TextStyle(
                    color = ColorProvider(android.graphics.Color.parseColor("#88FFFFFF")),
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // ── Progress Bar ──────────────────────────────────────
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .cornerRadius(3.dp)
                    .background(ColorProvider(android.graphics.Color.parseColor("#33FFFFFF")))
            ) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth(fraction = progress.coerceIn(0.02f, 1f))
                        .height(6.dp)
                        .cornerRadius(3.dp)
                        .background(
                            ColorProvider(
                                if (progress >= 1f) android.graphics.Color.parseColor("#81C784")
                                else android.graphics.Color.parseColor("#4CAF50")
                            )
                        )
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // ── Status Row ────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Colored status dot
                Box(
                    modifier = GlanceModifier
                        .size(8.dp)
                        .cornerRadius(4.dp)
                        .background(statusColor)
                )
                Spacer(modifier = GlanceModifier.width(6.dp))
                Text(
                    text = statusText,
                    style = TextStyle(
                        color = ColorProvider(android.graphics.Color.parseColor("#CCFFFFFF")),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(6.dp))

            // ── Goal Label ────────────────────────────────────────
            Text(
                text = "Goal: ${PermissionHelper.formatStepCount(goal)}",
                style = TextStyle(
                    color = ColorProvider(android.graphics.Color.parseColor("#66FFFFFF")),
                    fontSize = 11.sp
                )
            )
        }
    }
}
