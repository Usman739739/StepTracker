package com.steptracker.app.widget

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.steptracker.app.util.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker that periodically updates the home-screen widget.
 * Runs every 15 minutes (minimum WorkManager interval) and also gets
 * triggered manually when step count changes significantly.
 */
@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "WidgetUpdateWorker"

        /**
         * Enqueue periodic widget updates.
         */
        fun enqueuePeriodic(context: Context) {
            val request = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                Constants.WIDGET_UPDATE_INTERVAL_MINUTES, TimeUnit.MINUTES
            )
                .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.WORK_TAG_WIDGET_UPDATE,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
            Log.d(TAG, "Periodic widget update enqueued")
        }

        /**
         * Trigger a one-time immediate widget update.
         */
        fun enqueueOnce(context: Context) {
            val request = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Updating widget")
        return try {
            StepWidget.updateAll(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Widget update failed", e)
            Result.retry()
        }
    }
}
