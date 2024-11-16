package com.shubham.alarmassignment.data

data class AlarmModel(
    val id: String, // id of the alarm
    val alarmTime: String, // time of the alarm
    var isActive: Boolean = true // by default every added alarm is active
)