package com.steptracker.app.activity

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.steptracker.app.util.Constants
import kotlinx.coroutines.tasks.await

/**
 * Manages Activity Recognition Transition API registration and monitoring.
 * 
 * Detects transitions to/from BICYCLE and VEHICLE activities to
 * automatically pause/resume step counting.
 */
class ActivityRecognitionHelper(private val context: Context) {

    companion object {
        private const val TAG = "ActivityRecHelper"
    }

    private val client = ActivityRecognition.getClient(context)

    /**
     * Check if activity recognition permission is granted.
     */
    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Register for activity transition updates.
     * Listens for enter/exit of ON_BICYCLE and IN_VEHICLE.
     */
    suspend fun registerTransitions(): Boolean {
        if (!hasPermission()) {
            Log.w(TAG, "Activity recognition permission not granted")
            return false
        }

        val transitions = listOf(
            // Bicycle transitions
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            // Vehicle transitions
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )

        val request = ActivityTransitionRequest(transitions, createPendingIntent())

        return try {
            client.requestActivityTransitionUpdates(request, createPendingIntent()).await()
            Log.d(TAG, "Activity transition updates registered")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register activity transitions", e)
            false
        }
    }

    /**
     * Unregister activity transition updates.
     */
    suspend fun unregisterTransitions() {
        try {
            client.removeActivityTransitionUpdates(createPendingIntent()).await()
            Log.d(TAG, "Activity transition updates unregistered")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unregister activity transitions", e)
        }
    }

    /**
     * Parse an intent containing activity transition results.
     * Returns a pair of (isBikingOrDriving, activityName).
     */
    fun parseTransitionResult(intent: Intent): Pair<Boolean, String>? {
        if (!ActivityTransitionResult.hasResult(intent)) return null

        val result = ActivityTransitionResult.extractResult(intent) ?: return null

        for (event in result.transitionEvents) {
            Log.d(TAG, "Transition: activity=${activityName(event.activityType)}, " +
                    "transition=${if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) "ENTER" else "EXIT"}")

            when (event.activityType) {
                DetectedActivity.ON_BICYCLE -> {
                    val isEntering = event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER
                    return Pair(isEntering, "Biking")
                }
                DetectedActivity.IN_VEHICLE -> {
                    val isEntering = event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER
                    return Pair(isEntering, "Driving")
                }
            }
        }
        return null
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, ActivityTransitionReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            Constants.ACTIVITY_TRANSITION_PENDING_INTENT_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun activityName(type: Int): String = when (type) {
        DetectedActivity.IN_VEHICLE -> "IN_VEHICLE"
        DetectedActivity.ON_BICYCLE -> "ON_BICYCLE"
        DetectedActivity.ON_FOOT -> "ON_FOOT"
        DetectedActivity.STILL -> "STILL"
        DetectedActivity.WALKING -> "WALKING"
        DetectedActivity.RUNNING -> "RUNNING"
        else -> "UNKNOWN($type)"
    }
}
