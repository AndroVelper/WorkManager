package com.shubham.alarmassignment.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random


/*
* These are the extensions used in the App
* */

fun View.showView() {
    this.visibility = View.VISIBLE
}

fun View.hideView() {
    this.visibility = View.GONE
}


fun String.generateId(): String {
    val random = Random.nextInt()
    return this + random
}

fun Context.workOnBackgroundThread(task: () -> Unit) {
    CoroutineScope(Dispatchers.IO)
        .launch {
            task()
        }
}

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}