package com.wordloop.germana1.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun today(): String {
        return dateFormat.format(Date())
    }

    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    fun daysBetween(date1: String, date2: String): Long {
        val d1 = parseDate(date1) ?: return 0
        val d2 = parseDate(date2) ?: return 0
        val diff = d2.time - d1.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }

    fun calculateStreak(completedDates: List<String>): Int {
        if (completedDates.isEmpty()) return 0

        val sortedDates = completedDates.sortedDescending()
        var streak = 0
        var expectedDate = today()

        for (date in sortedDates) {
            if (date == expectedDate || (streak == 0 && daysBetween(date, today()) <= 1)) {
                streak++
                expectedDate = dateMinusDays(date, 1)
            } else {
                break
            }
        }
        return streak
    }

    private fun dateMinusDays(dateString: String, days: Int): String {
        val date = parseDate(dateString) ?: return today()
        val newTime = date.time - TimeUnit.DAYS.toMillis(days.toLong())
        return formatDate(Date(newTime))
    }

    fun addReviewInterval(masteryLevel: Int): Long {
        return when (masteryLevel) {
            0 -> TimeUnit.HOURS.toMillis(1)      // Hard - 1 hour
            1 -> TimeUnit.DAYS.toMillis(1)       // Good - 1 day
            2 -> TimeUnit.DAYS.toMillis(3)       // Easy - 3 days
            else -> TimeUnit.HOURS.toMillis(1)
        }
    }
}