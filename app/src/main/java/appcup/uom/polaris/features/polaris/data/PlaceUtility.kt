package appcup.uom.polaris.features.polaris.data

import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.Period
import com.google.android.libraries.places.api.model.TimeOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime

fun isPlaceOpenNow(openingHours: OpeningHours?): Boolean {
    if (openingHours == null) {
        return false // Or handle as "unknown" depending on your needs
    }

    val periods = openingHours.periods
    if (periods.isNullOrEmpty()) {
        return false // No opening hour information available
    }

    val now = LocalDateTime.now()
    val currentDayOfWeekPlacesApi = convertJavaDayOfWeekToPlacesApi(now.dayOfWeek)
    val currentTime = now.toLocalTime()

    for (period in periods) {
        val openEvent = period.open
        val closeEvent = period.close

        if (openEvent == null) continue // Should not happen if period is valid

        // Check 1: Simple openNow flag (if available and you trust it for "always open")
        // Note: The direct 'openNow' field on Place.OpeningHours is a boolean you can check first.
        // This function dives deeper into periods if that's not sufficient or for more complex logic.
        // if (openingHours.isOpen(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())) return true // Requires a specific instant

        // Check 2: For periods that span across midnight or are 24/7 for a specific day
        if (closeEvent == null) { // Indicates open 24 hours for the day of openEvent.day
            if (openEvent.day == currentDayOfWeekPlacesApi) {
                return true
            }
            continue // Move to the next period
        }

        // --- Standard Period Check (within the same day or overnight) ---

        val openDay = openEvent.day
        val openTime = LocalTime.of(openEvent.time.hours, openEvent.time.minutes)

        val closeDay = closeEvent.day
        val closeTime = LocalTime.of(closeEvent.time.hours, closeEvent.time.minutes)

        // Scenario A: Opens and Closes on the Same Day
        if (openDay == currentDayOfWeekPlacesApi && closeDay == currentDayOfWeekPlacesApi) {
            if (openTime <= currentTime && currentTime < closeTime) {
                return true
            }
        }
        // Scenario B: Opens on one day, closes on the next (overnight)
        else if (closeDay == getNextDayPlacesApi(openDay)) {
            // Check if current time is after open time on the openDay
            if (openDay == currentDayOfWeekPlacesApi && currentTime >= openTime) {
                return true
            }
            // Check if current time is before close time on the closeDay
            if (closeDay == currentDayOfWeekPlacesApi && currentTime < closeTime) {
                return true
            }
        }
        // Scenario C: Potentially open 24/7 if open day is Sunday 00:00 and close is null (already handled)
        // or if open is Sunday 00:00 and close is Saturday 23:59 (less common representation for 24/7)
        // For simplicity, the 24/7 case is best handled by `closeEvent == null`.

    }

    return false // Not found within any open period
}

/**
 * Converts java.time.DayOfWeek to Places API compatible com.google.android.libraries.places.api.model.DayOfWeek.
 * Note: Places API DayOfWeek enum might have different ordinal values or representations.
 * The Places API uses Sunday as 1 and Saturday as 7.
 * java.time.DayOfWeek uses Monday as 1 and Sunday as 7.
 */
private fun convertJavaDayOfWeekToPlacesApi(javaDayOfWeek: DayOfWeek): com.google.android.libraries.places.api.model.DayOfWeek {
    return when (javaDayOfWeek) {
        DayOfWeek.MONDAY -> com.google.android.libraries.places.api.model.DayOfWeek.MONDAY
        DayOfWeek.TUESDAY -> com.google.android.libraries.places.api.model.DayOfWeek.TUESDAY
        DayOfWeek.WEDNESDAY -> com.google.android.libraries.places.api.model.DayOfWeek.WEDNESDAY
        DayOfWeek.THURSDAY -> com.google.android.libraries.places.api.model.DayOfWeek.THURSDAY
        DayOfWeek.FRIDAY -> com.google.android.libraries.places.api.model.DayOfWeek.FRIDAY
        DayOfWeek.SATURDAY -> com.google.android.libraries.places.api.model.DayOfWeek.SATURDAY
        DayOfWeek.SUNDAY -> com.google.android.libraries.places.api.model.DayOfWeek.SUNDAY
    }
}

/**
 * Gets the next day in Places API's DayOfWeek sequence.
 */
private fun getNextDayPlacesApi(day: com.google.android.libraries.places.api.model.DayOfWeek): com.google.android.libraries.places.api.model.DayOfWeek {
    return when (day) {
        com.google.android.libraries.places.api.model.DayOfWeek.SUNDAY -> com.google.android.libraries.places.api.model.DayOfWeek.MONDAY
        com.google.android.libraries.places.api.model.DayOfWeek.MONDAY -> com.google.android.libraries.places.api.model.DayOfWeek.TUESDAY
        com.google.android.libraries.places.api.model.DayOfWeek.TUESDAY -> com.google.android.libraries.places.api.model.DayOfWeek.WEDNESDAY
        com.google.android.libraries.places.api.model.DayOfWeek.WEDNESDAY -> com.google.android.libraries.places.api.model.DayOfWeek.THURSDAY
        com.google.android.libraries.places.api.model.DayOfWeek.THURSDAY -> com.google.android.libraries.places.api.model.DayOfWeek.FRIDAY
        com.google.android.libraries.places.api.model.DayOfWeek.FRIDAY -> com.google.android.libraries.places.api.model.DayOfWeek.SATURDAY
        com.google.android.libraries.places.api.model.DayOfWeek.SATURDAY -> com.google.android.libraries.places.api.model.DayOfWeek.SUNDAY
    }
}