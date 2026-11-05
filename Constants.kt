package com.steptracker.app.util

/**
 * Application-wide constants for StepTracker.
 */
object Constants {

    // ── Notification ──────────────────────────────────────────────
    const val NOTIFICATION_CHANNEL_ID = "step_tracker_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Step Tracking"
    const val NOTIFICATION_ID = 1001

    // ── Notification Actions ──────────────────────────────────────
    const val ACTION_PAUSE_RESUME = "com.steptracker.ACTION_PAUSE_RESUME"
    const val ACTION_STOP_SERVICE = "com.steptracker.ACTION_STOP_SERVICE"

    // ── Service Actions ───────────────────────────────────────────
    const val ACTION_START_TRACKING = "com.steptracker.START_TRACKING"
    const val ACTION_STOP_TRACKING = "com.steptracker.STOP_TRACKING"

    // ── Activity Recognition ──────────────────────────────────────
    const val ACTIVITY_TRANSITION_REQUEST_ID = 42
    const val ACTIVITY_TRANSITION_PENDING_INTENT_CODE = 100

    // ── Schedule Alarm ────────────────────────────────────────────
    const val ALARM_START_REQUEST_CODE = 200
    const val ALARM_STOP_REQUEST_CODE = 201
    const val EXTRA_ALARM_TYPE = "alarm_type"
    const val ALARM_TYPE_START = "start"
    const val ALARM_TYPE_STOP = "stop"

    // ── WorkManager ───────────────────────────────────────────────
    const val WORK_TAG_WIDGET_UPDATE = "widget_update"
    const val WORK_TAG_SCHEDULE_VERIFY = "schedule_verify"
    const val WORK_TAG_MIDNIGHT_RESET = "midnight_reset"
    const val WORK_TAG_HEALTH_SYNC = "health_sync"

    // ── DataStore Keys ────────────────────────────────────────────
    const val PREF_DAILY_GOAL = "daily_goal"
    const val PREF_SCHEDULE_ENABLED = "schedule_enabled"
    const val PREF_SCHEDULE_START_HOUR = "schedule_start_hour"
    const val PREF_SCHEDULE_START_MINUTE = "schedule_start_minute"
    const val PREF_SCHEDULE_END_HOUR = "schedule_end_hour"
    const val PREF_SCHEDULE_END_MINUTE = "schedule_end_minute"
    const val PREF_BIKE_DETECTION_ENABLED = "bike_detection_enabled"
    const val PREF_ONBOARDING_COMPLETE = "onboarding_complete"
    const val PREF_MANUAL_PAUSE = "manual_pause"
    const val PREF_TODAY_STEPS = "today_steps"
    const val PREF_TODAY_DATE = "today_date"
    const val PREF_SENSOR_LAST_VALUE = "sensor_last_value"

    // ── Defaults ──────────────────────────────────────────────────
    const val DEFAULT_DAILY_GOAL = 10_000
    const val DEFAULT_SCHEDULE_START_HOUR = 8
    const val DEFAULT_SCHEDULE_START_MINUTE = 0
    const val DEFAULT_SCHEDULE_END_HOUR = 18
    const val DEFAULT_SCHEDULE_END_MINUTE = 0
    const val DEFAULT_BIKE_DETECTION = true

    // ── Step Detection ────────────────────────────────────────────
    /** Minimum time between steps in milliseconds (prevents rapid false counts). */
    const val MIN_STEP_INTERVAL_MS = 250L
    /** Peak detection threshold for accelerometer magnitude. */
    const val PEAK_THRESHOLD = 12.0f
    /** Minimum valley-to-peak difference for a valid step. */
    const val MIN_PEAK_VALLEY_DELTA = 3.0f
    /** Low-pass filter alpha for accelerometer smoothing. */
    const val LOW_PASS_ALPHA = 0.15f

    // ── Widget ────────────────────────────────────────────────────
    const val WIDGET_UPDATE_INTERVAL_MINUTES = 15L

    // ── Distance ──────────────────────────────────────────────────
    /** Average step length in meters (approximate for adults). */
    const val AVERAGE_STEP_LENGTH_METERS = 0.762
}
