package com.steptracker.app.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission

/**
 * Centralized permission helper for StepTracker.
 */
object PermissionHelper {

    /**
     * Runtime permissions required by the app (varies by API level).
     */
    fun getRequiredPermissions(): List<String> = buildList {
        add(Manifest.permission.ACTIVITY_RECOGNITION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }.distinct()

    /**
     * Health Connect permissions needed.
     */
    fun getHealthPermissions(): Set<String> = setOf(
        HealthPermission.getReadPermission(androidx.health.connect.client.records.StepsRecord::class),
        HealthPermission.getWritePermission(androidx.health.connect.client.records.StepsRecord::class),
    )

    /**
     * Check if all runtime permissions are granted.
     */
    fun hasRuntimePermissions(context: Context): Boolean {
        return getRequiredPermissions().all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check if Health Connect is available on this device.
     */
    fun isHealthConnectAvailable(context: Context): Boolean {
        return HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
    }

    /**
     * Get Health Connect install intent if not available.
     */
    fun getHealthConnectInstallIntent(): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            setPackage("com.android.vending")
            data = Uri.parse("market://details?id=com.google.android.apps.healthdata")
        }
    }

    /**
     * Check if the app is exempt from battery optimization.
     */
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    /**
     * Get intent to request battery optimization exemption.
     */
    fun getBatteryOptimizationIntent(context: Context): Intent {
        return Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
    }

    /**
     * Format step count with commas: 12345 → "12,345"
     */
    fun formatStepCount(steps: Int): String {
        return String.format("%,d", steps)
    }

    /**
     * Calculate distance from steps in kilometers.
     */
    fun stepsToDistanceKm(steps: Int): Double {
        return steps * Constants.AVERAGE_STEP_LENGTH_METERS / 1000.0
    }

    /**
     * Format distance in km with one decimal.
     */
    fun formatDistance(km: Double): String {
        return String.format("%.1f km", km)
    }
}
