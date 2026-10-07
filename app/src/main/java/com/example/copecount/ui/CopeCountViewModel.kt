package com.example.copecount.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.copecount.logic.WorkDayCalculator
import com.example.copecount.models.CountdownProfile
import com.example.copecount.notifications.ReminderReceiver
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar

class CopeCountViewModel(context: Context) : ViewModel() {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("SiwesPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    var profiles by mutableStateOf(loadProfiles())
        private set

    var activeProfileId by mutableStateOf(sharedPref.getString("ACTIVE_PROFILE_ID", "") ?: "")
        private set

    val activeProfile: CountdownProfile?
        get() = profiles.find { it.id == activeProfileId } ?: profiles.firstOrNull()

    // Dashboard values
    val startDate: LocalDate get() = safeParseDate(activeProfile?.startDateStr)
    val endDate: LocalDate get() = safeParseDate(activeProfile?.endDateStr, LocalDate.now().plusMonths(3))
    val workDays: Set<DayOfWeek> get() = activeProfile?.workDaysInts?.mapNotNull { 
        try { DayOfWeek.of(it) } catch (e: Exception) { null } 
    }?.toSet() ?: setOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY)
    
    val totalDays: Int get() = WorkDayCalculator.calculateWorkDays(startDate, endDate, workDays)
    
    val loggedDates: Set<LocalDate> get() = activeProfile?.loggedDatesStrs?.mapNotNull { 
        try { LocalDate.parse(it) } catch (e: Exception) { null } 
    }?.toSet() ?: emptySet()
    
    val loggedDaysCount: Int get() = loggedDates.size
    val daysRemaining: Int get() = (totalDays - loggedDaysCount).coerceAtLeast(0)
    
    var streakCount by mutableStateOf(0)
        private set

    init {
        if (profiles.isEmpty()) {
            val defaultProfile = createDefaultProfile()
            profiles = listOf(defaultProfile)
            activeProfileId = defaultProfile.id
            saveProfiles(profiles)
            sharedPref.edit().putString("ACTIVE_PROFILE_ID", activeProfileId).apply()
        } else {
            val sanitized = profiles.map { it.ensureNonNull() }
            val updated = sanitized.map { profile ->
                val start = safeParseDate(profile.startDateStr)
                val days = profile.workDaysInts.mapNotNull { 
                    try { DayOfWeek.of(it) } catch (e: Exception) { null } 
                }.toSet()
                val backfilled = calculatePastWorkDays(start, days)
                profile.copy(
                    loggedDatesStrs = profile.loggedDatesStrs + backfilled
                )
            }
            profiles = updated
            saveProfiles(profiles)
        }
        calculateStreak()
    }

    private fun safeParseDate(str: String?, default: LocalDate = LocalDate.now()): LocalDate {
        if (str.isNullOrEmpty()) return default
        return try { LocalDate.parse(str) } catch (e: Exception) { default }
    }

    private fun CountdownProfile.ensureNonNull(): CountdownProfile {
        return this.copy(
            workDaysInts = workDaysInts ?: emptyList(),
            loggedDatesStrs = loggedDatesStrs ?: emptySet()
        )
    }

    private fun createDefaultProfile(): CountdownProfile {
        val currentYear = LocalDate.now().year
        val start = LocalDate.of(currentYear, 5, 5)
        val end = LocalDate.of(currentYear, 9, 30)
        val workDays = setOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY)
        
        return CountdownProfile(
            name = "My SIWES",
            startDateStr = start.toString(),
            endDateStr = end.toString(),
            workDaysInts = workDays.map { it.value },
            loggedDatesStrs = calculatePastWorkDays(start, workDays)
        )
    }

    private fun calculatePastWorkDays(start: LocalDate, workDays: Set<DayOfWeek>): Set<String> {
        val today = LocalDate.now()
        val dates = mutableSetOf<String>()
        var curr = start
        
        // If all days are selected, include today in the automatic countdown
        val endLimit = if (workDays.size == 7) today.plusDays(1) else today
        
        while (curr.isBefore(endLimit)) {
            if (workDays.contains(curr.dayOfWeek)) {
                dates.add(curr.toString())
            }
            curr = curr.plusDays(1)
        }
        return dates
    }

    private fun loadProfiles(): List<CountdownProfile> {
        val json = sharedPref.getString("PROFILES_JSON", "")
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<CountdownProfile>>() {}.type
        return try {
            gson.fromJson<List<CountdownProfile>>(json, type).map { it.ensureNonNull() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveProfiles(list: List<CountdownProfile>) {
        val json = gson.toJson(list)
        sharedPref.edit().putString("PROFILES_JSON", json).apply()
    }

    fun addProfile(name: String, start: LocalDate, end: LocalDate, days: Set<DayOfWeek>) {
        val newProfile = CountdownProfile(
            name = name,
            startDateStr = start.toString(),
            endDateStr = end.toString(),
            workDaysInts = days.map { it.value },
            loggedDatesStrs = calculatePastWorkDays(start, days)
        )
        profiles = profiles + newProfile
        activeProfileId = newProfile.id
        saveProfiles(profiles)
        sharedPref.edit().putString("ACTIVE_PROFILE_ID", activeProfileId).apply()
        calculateStreak()
    }

    fun updateProfile(id: String, name: String, start: LocalDate, end: LocalDate, days: Set<DayOfWeek>) {
        profiles = profiles.map { 
            if (it.id == id) {
                val backfilled = calculatePastWorkDays(start, days)
                it.copy(
                    name = name,
                    startDateStr = start.toString(),
                    endDateStr = end.toString(),
                    workDaysInts = days.map { it.value },
                    loggedDatesStrs = (it.loggedDatesStrs ?: emptySet()) + backfilled
                )
            } else it 
        }
        saveProfiles(profiles)
        calculateStreak()
    }

    fun switchProfile(id: String) {
        activeProfileId = id
        sharedPref.edit().putString("ACTIVE_PROFILE_ID", id).apply()
        calculateStreak()
    }

    fun deleteProfile(id: String) {
        if (profiles.size <= 1) return
        profiles = profiles.filter { it.id != id }
        if (activeProfileId == id) {
            activeProfileId = profiles.first().id
            sharedPref.edit().putString("ACTIVE_PROFILE_ID", activeProfileId).apply()
        }
        saveProfiles(profiles)
        calculateStreak()
    }

    fun toggleClockIn(date: LocalDate = LocalDate.now()) {
        val currentActive = activeProfile ?: return
        val dateStr = date.toString()
        val newDates = (currentActive.loggedDatesStrs ?: emptySet()).toMutableSet()
        if (newDates.contains(dateStr)) {
            newDates.remove(dateStr)
        } else {
            newDates.add(dateStr)
        }
        
        val updatedProfile = currentActive.copy(loggedDatesStrs = newDates)
        profiles = profiles.map { if (it.id == updatedProfile.id) updatedProfile else it }
        saveProfiles(profiles)
        calculateStreak()
    }

    fun calculateStreak() {
        val dates = loggedDates
        var streak = 0
        var checkDate = LocalDate.now()
        while (dates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }
        streakCount = streak
    }

    fun scheduleReminder(context: Context, dateTime: LocalDateTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val profileId = activeProfileId.hashCode()
        
        val pendingIntent = PendingIntent.getBroadcast(
            context, profileId, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(dateTime.year, dateTime.monthValue - 1, dateTime.dayOfMonth, dateTime.hour, dateTime.minute, 0)
        }

        if (calendar.timeInMillis > System.currentTimeMillis()) {
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                // Fallback for missing exact alarm permission
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }
        
        val currentActive = activeProfile ?: return
        val updatedProfile = currentActive.copy(
            isNotificationEnabled = true,
            reminderDateTimeStr = dateTime.toString()
        )
        profiles = profiles.map { if (it.id == updatedProfile.id) updatedProfile else it }
        saveProfiles(profiles)
    }

    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val profileId = activeProfileId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context, profileId, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

        val currentActive = activeProfile ?: return
        val updatedProfile = currentActive.copy(isNotificationEnabled = false, reminderDateTimeStr = null)
        profiles = profiles.map { if (it.id == updatedProfile.id) updatedProfile else it }
        saveProfiles(profiles)
    }
}
