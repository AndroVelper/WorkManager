package com.shubham.alarmassignment.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shubham.alarmassignment.data.AlarmModel

import java.lang.reflect.Type

// this is the data storage manager of the class.

object SharedPreferencesManager {


    var communicatorLiveData : MutableLiveData<Boolean> = MutableLiveData()

    private const val PREF_NAME = "alarm_preferences"
    private const val ALARM_LIST_KEY = "alarm_list"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Save list of AlarmEntity to SharedPreferences
    fun saveAlarmList(context: Context, alarmList: MutableList<AlarmModel>) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()

        // Convert the MutableList to JSON using Gson
        val gson = Gson()
        val json = gson.toJson(alarmList)

        editor.putString(ALARM_LIST_KEY, json)
        editor.apply() // Apply the changes asynchronously
    }

    fun getActiveAlarmId(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        val json = sharedPreferences.getString(ALARM_LIST_KEY, null)

        if (json != null) {
            val gson = Gson()
            val alarmList: List<AlarmModel> =
                gson.fromJson(json, Array<AlarmModel>::class.java).toList()

            for (alarm in alarmList) {
                if (alarm.isActive) {
                    return alarm.id
                }
            }
        }
        return null
    }


    fun disableAllAlarms(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        val json = sharedPreferences.getString(ALARM_LIST_KEY, null)

        if (json != null) {
            val gson = Gson()
            val alarmList: MutableList<AlarmModel> =
                gson.fromJson(json, Array<AlarmModel>::class.java).toMutableList()

            // Loop through all alarms and disable them
            for (alarm in alarmList) {
                alarm.isActive = false
            }

            // Save the updated alarm list
            val updatedJson = gson.toJson(alarmList)
            val editor = sharedPreferences.edit()
            editor.putString(ALARM_LIST_KEY, updatedJson)
            editor.apply()
        }
    }

    // Retrieve the list of AlarmEntity from SharedPreferences
    fun getAlarmList(context: Context): MutableList<AlarmModel> {
        val sharedPreferences = getSharedPreferences(context)

        // Get the stored JSON string
        val json = sharedPreferences.getString(ALARM_LIST_KEY, null) ?: return mutableListOf()

        // If JSON is null, return an empty list

        // Convert the JSON back to a MutableList<AlarmEntity> using Gson
        val gson = Gson()
        val type: Type = object : TypeToken<MutableList<AlarmModel>>() {}.type
        return gson.fromJson(json, type)
    }

    // Clear the saved alarm list in SharedPreferences
    fun clearAlarmList(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.remove(ALARM_LIST_KEY)
        editor.apply()
    }


}
