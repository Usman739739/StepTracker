package com.steptracker.app.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.steptracker.app.data.local.PreferencesManager
import com.steptracker.app.service.StepCounterService
import com.steptracker.app.service.StepCounterService.TrackingStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * BroadcastReceiver that handles activity transition events from Google Play Services.
 * 
 * When biking or driving is detected, it signals the StepCounterService to pause.
 * When the user stops biking/driving, it signals the service to resume.
 */
@AndroidEntryPoint
class ActivityTransitionReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ActivityTransReceiver"
    }

    @Inject lateinit var preferencesManager: PreferencesManager

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        val helper = ActivityRecognitionHelper(context)
        val result = helper.parseTransitionResult(intent) ?: return

        val (isBikingOrDriving, activityName) = result
        Log.d(TAG, "Activity transition: $activityName → ${if (isBikingOrDriving) "START" else "STOP"}")

        receiverScope.launch {
            // Check if bike detection is enabled in settings
            val prefs = preferencesManager.preferencesFlow.first()
            if (!prefs.bikeDetectionEnabled) {
                Log.d(TAG, "Bike/vehicle detection disabled in settings — ignoring")
                return@launch
            }

            // Signal the service
            val status = when {
                activityName == "Biking" && isBikingOrDriving -> TrackingStatus.BIKING
                activityName == "Driving" && isBikingOrDriving -> TrackingStatus.DRIVING
                else -> TrackingStatus.WALKING
            }

            if (StepCounterService.isRunning.value) {
                val serviceIntent = Intent(context, StepCounterService::class.java).apply {
                    action = if (isBikingOrDriving) "AUTO_PAUSE" else "AUTO_RESUME"
                    putExtra("activity_type", activityName.lowercase())
                }
                try {
                    context.startForegroundService(serviceIntent)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to signal service", e)
                }
            }
        }
    }
}
