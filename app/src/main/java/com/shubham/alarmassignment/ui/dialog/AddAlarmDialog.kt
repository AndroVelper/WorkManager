package com.shubham.alarmassignment.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.shubham.alarmassignment.R
import com.shubham.alarmassignment.data.AlarmModel
import com.shubham.alarmassignment.databinding.ItemAddAlarmBinding
import com.shubham.alarmassignment.utils.SharedPreferencesManager
import com.shubham.alarmassignment.utils.generateId
import com.shubham.alarmassignment.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/***
 * A class to create the alarm
 *  add Create the alarm and store the data in the DataBase
 *  Cancel : cancel the screen and don't create the alarm.
 * ***/

class CustomDialogFragment(
    private val list: MutableList<AlarmModel>,
    private val function: () -> Unit
) : DialogFragment() {


    companion object {
        var userSelectedTime: String = ""
    }

    private val binding: ItemAddAlarmBinding by lazy {
        ItemAddAlarmBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Optionally, set some properties dynamically (e.g., set a message or listener)
        binding.apply {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY) // 24-hour format
            val currentMinute = calendar.get(Calendar.MINUTE)
            tpTimePicker.hour = currentHour
            tpTimePicker.minute = currentMinute
            tpTimePicker.setOnTimeChangedListener { _, i, i2 ->
                userSelectedTime = formatTime(i, i2)
            }

            addButton.setOnClickListener {

                // this will check does user has selected the time or not
                // if not then provide the current time
                if (userSelectedTime.isEmpty()) {
                    userSelectedTime = getCurrentTime(calendar)
                }

                // first check does we contain any alarm with that time
                if (isAlarmAlreadyPresent()) {
                    getString(R.string.alarm_already_present_with_given_date).showToast(binding.root.context)
                    return@setOnClickListener
                }

                list.forEach {
                    it.isActive = false
                }
                list.add(AlarmModel(userSelectedTime.generateId(), userSelectedTime, true))
                CoroutineScope(Dispatchers.IO).launch {
                    SharedPreferencesManager.saveAlarmList(binding.root.context, list)
                }
                function()
                dismiss()
            }

            cancelButton.setOnClickListener {
                dismiss()
            }

        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(false)
            .create()
    }

    private fun isAlarmAlreadyPresent(): Boolean {
        for (i in list) {
            if (i.alarmTime == userSelectedTime) {
                return true
            }
        }
        return false
    }

    private fun formatTime(hourOfDay: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val format = SimpleDateFormat(
            getString(R.string.hh_mm_a),
            Locale.getDefault()
        ) // 12-hour format with AM/PM
        return format.format(calendar.time)
    }

    private fun getCurrentTime(calendar: Calendar): String {
        val format = SimpleDateFormat(
            getString(R.string.hh_mm_a),
            Locale.getDefault()
        ) // 12-hour format with AM/PM
        return format.format(calendar.time)
    }


    override fun onDestroy() {
        super.onDestroy()
        userSelectedTime = ""
    }
}
