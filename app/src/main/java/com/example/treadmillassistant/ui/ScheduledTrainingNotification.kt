package com.example.treadmillassistant.ui


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.treadmillassistant.R

const val notificationID = "notificationID"
const val channelID = "UpcomingTrainingNotification"
const val trainingNotificationTime = "trainingNotificationTime"

class ScheduledTrainingNotification: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val time = intent.getStringExtra(trainingNotificationTime)
        val id = intent.getIntExtra(notificationID, 1)
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
            .setContentTitle("Upcoming training")
            .setContentText("You have training scheduled at $time")
            .build()

        val notificationChannel = NotificationChannel(
            channelID,
            "TrainingNotification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(notificationChannel)

        val managerCompat = NotificationManagerCompat.from(context)
        managerCompat.notify(id, notification)
    }
}