package com.steptracker.app

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.steptracker.app.scheduling.ScheduleWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for StepTracker.
 * 
 * Initializes Hilt, configures WorkManager with HiltWorkerFactory,
 * and sets up global workers.
 */
@HiltAndroidApp
class StepTrackerApp : Application(), Configuration.Provider {

    companion object {
        private const val TAG = "StepTrackerApp"
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "StepTracker application initialized")

        // Enqueue periodic schedule verification worker
        ScheduleWorker.enqueue(this)
    }
}
