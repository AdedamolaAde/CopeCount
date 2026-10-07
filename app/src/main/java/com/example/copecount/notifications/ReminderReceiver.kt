package com.example.copecount.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.copecount.models.CountdownProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sharedPref = context.getSharedPreferences("SiwesPrefs", Context.MODE_PRIVATE)
        val activeId = sharedPref.getString("ACTIVE_PROFILE_ID", "")
        val json = sharedPref.getString("PROFILES_JSON", "")
        
        if (activeId.isNullOrEmpty() || json.isNullOrEmpty()) return

        val type = object : TypeToken<List<CountdownProfile>>() {}.type
        val profiles: List<CountdownProfile> = Gson().fromJson(json, type)
        val activeProfile = profiles.find { it.id == activeId } ?: return

        // For specific reminders, we don't necessarily need to check the day here 
        // since the Alarm was set for a specific millisecond.
        // But we can check if it's still enabled.
        if (!activeProfile.isNotificationEnabled) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "cope_count_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "CopeCount Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("CopeCount Reminder: ${activeProfile.name}")
            .setContentText("Reminder for your work project!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(activeId.hashCode(), notification)
    }
}
