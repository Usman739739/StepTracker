package com.steptracker.app.util

import android.content.Context
import android.text.format.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Useful extension functions and formatters.
 */

/** Format a LocalTime as "h:mm a" (e.g. "8:30 AM") or 24-hour based on user preference. */
fun LocalTime.formatDisplay(context: Context): String {
    val is24Hour = DateFormat.is24HourFormat(context)
    val pattern = if (is24Hour) "HH:mm" else "h:mm a"
    return format(DateTimeFormatter.ofPattern(pattern))
}

/** Convert epoch millis to LocalDate in system default zone. */
fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

/** Get start-of-day epoch millis for today. */
fun todayStartMillis(): Long =
    LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

/** Get end-of-day epoch millis for today. */
fun todayEndMillis(): Long =
    LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1

/** Get today's date as "yyyy-MM-dd" string. */
fun todayDateString(): String =
    LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

/** Convert LocalTime to minutes since midnight. */
fun LocalTime.toMinutesOfDay(): Int = hour * 60 + minute

/** Check if current time is within the given range. */
fun isWithinSchedule(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int): Boolean {
    val now = LocalTime.now()
    val start = LocalTime.of(startHour, startMinute)
    val end = LocalTime.of(endHour, endMinute)
    return if (start <= end) {
        now in start..end
    } else {
        // Wraps past midnight: e.g. 22:00 to 06:00
        now >= start || now <= end
    }
}
