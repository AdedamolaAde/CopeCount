package com.example.copecount.logic

import java.time.DayOfWeek
import java.time.LocalDate

object WorkDayCalculator {
    /**
     * Calculates the number of work days between two dates based on selected days of the week.
     */
    fun calculateWorkDays(
        startDate: LocalDate,
        endDate: LocalDate,
        workDays: Set<DayOfWeek> = setOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY)
    ): Int {
        var count = 0
        var currentDate = startDate

        while (!currentDate.isAfter(endDate)) {
            if (workDays.contains(currentDate.dayOfWeek)) {
                count++
            }
            currentDate = currentDate.plusDays(1)
        }
        return count
    }
}
