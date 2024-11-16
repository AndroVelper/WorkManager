package com.shubham.alarmassignment.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


/***
 * This broadCast will handle the snooze feature
 * For now i have set the snooze code to 10 sec from the scheduled time
 *
 * **/

class SnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val snoozeDuration = 10 // Seconds to snooze

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(snoozeDuration.toLong(), TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context!!).enqueueUniqueWork(
            Constants.NOTIFICATION_WORK,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        // Close the current notification
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}