package com.shubham.alarmassignment.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*
* This is called when the user press the close btn in the notification
* */

class CloseReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        WorkManager.getInstance(context).cancelUniqueWork(Constants.NOTIFICATION_WORK)
        val job = CoroutineScope(Dispatchers.IO).async {
            SharedPreferencesManager.disableAllAlarms(context)
        }

        CoroutineScope(Dispatchers.IO).launch {
            job.await()
            SharedPreferencesManager.communicatorLiveData.postValue(true)
        }
    }
}
