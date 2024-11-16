package com.shubham.alarmassignment.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.shubham.alarmassignment.R

/*this class is called when the periodic request is reached*/
class NotificationWorker(private val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {

        // notification channel for the apps above android 13 required without this the
        // notification will not work.
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
            null
        )
        notificationChannel.enableVibration(true)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        // Crete the notification with the system default alarm ringtone and the vibration

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(context.getString(R.string.alarm_is_ringing))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setVibrate(longArrayOf(0, 1000, 500, 1000)) // Vibrate pattern
            .setContentText(context.getString(R.string.your_alarm_is_ringing))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Here i have added the code for the Snooze button on click of this
        // the Snooze broadcast will work
        val snoozeIntent = Intent(context, SnoozeReceiver::class.java)
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            snoozeIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(R.drawable.ic_alarm,
            context.getString(R.string.snooze), snoozePendingIntent)
        val getCurrentActiveId = SharedPreferencesManager.getActiveAlarmId(context)

        // here is the code for the close btn click on the notification
        // close receiver is Called here
        val closeIntent = Intent(context, CloseReceiver::class.java)
        closeIntent.putExtra(
            EXTRA_NOTIFICATION_ID,
            getCurrentActiveId
        )  // id of the alarm to close.
        val closePendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            closeIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(R.drawable.ic_delete, "Close", closePendingIntent)

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build())

        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "your_channel_id"
        const val CHANNEL_NAME = "Your Channel Name"
        const val EXTRA_NOTIFICATION_ID = "CurrentActiveAlarmId"
        const val NOTIFICATION_ID = 100
    }
}