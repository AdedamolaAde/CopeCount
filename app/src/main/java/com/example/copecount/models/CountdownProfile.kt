package com.example.copecount.models

import java.util.UUID

data class CountdownProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val startDateStr: String, // ISO-8601
    val endDateStr: String,   // ISO-8601
    val workDaysInts: List<Int> = emptyList(), // 1 (Mon) to 7 (Sun)
    val reminderDateTimeStr: String? = null, // Specific ISO-8601 LocalDateTime
    val isNotificationEnabled: Boolean = false,
    val loggedDatesStrs: Set<String> = emptySet()
)
