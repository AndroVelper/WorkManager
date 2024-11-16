package com.shubham.alarmassignment.ui.mainActivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.shubham.alarmassignment.R
import com.shubham.alarmassignment.communicator.IAlarmCallbackProvider
import com.shubham.alarmassignment.data.AlarmModel
import com.shubham.alarmassignment.databinding.ActivityMainBinding
import com.shubham.alarmassignment.ui.adapter.AlarmAdapter
import com.shubham.alarmassignment.ui.dialog.CustomDialogFragment
import com.shubham.alarmassignment.utils.Constants
import com.shubham.alarmassignment.utils.NotificationWorker
import com.shubham.alarmassignment.utils.SharedPreferencesManager
import com.shubham.alarmassignment.utils.hideView
import com.shubham.alarmassignment.utils.showView
import com.shubham.alarmassignment.utils.workOnBackgroundThread
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), IAlarmCallbackProvider {

    //Make this variable life cycle aware such that it should destroy itself as the lifecycle of this class finished
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var alarmAdapter: AlarmAdapter
    private var alarmList: MutableList<AlarmModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // don't do any heavy operation when activity has just starting created.
    override fun onStart() {
        super.onStart()
        alarmList = SharedPreferencesManager.getAlarmList(this)
        setUpVariables()
        clickListener()
        setUpObserver()
    }

    // set up the observer to manage the list state when the app is active
    private fun setUpObserver() {
        SharedPreferencesManager.communicatorLiveData.observe(this) {
            alarmList.forEach {
                it.isActive = false
            }
            updateTheRecycler()
        }
    }


    // initialize the local variable of the app
    private fun setUpVariables() {
        alarmAdapter = AlarmAdapter(alarmList, this)
        binding.apply {
            rvAlarmRecycler.layoutManager = LinearLayoutManager(this@MainActivity)
            rvAlarmRecycler.adapter = alarmAdapter
            if (alarmList.isNotEmpty()) {
                tvNoAlarmFoundText.hideView()
                deleteAllBtn.showView()
            } else {
                tvNoAlarmFoundText.showView()
                deleteAllBtn.hideView()
            }
        }

    }


    private fun clickListener() {
        binding.apply {
            faAddAlarmButton.setOnClickListener {

                // first check has user provided the permission or not
                // work for android 13 and above.
                if (!isNotificationPermissionGranted(this@MainActivity) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestNotificationPermission()
                    return@setOnClickListener
                }
                openTheAddAlarmDialog()
            }

//            delete all btn in the top bar
            deleteAllBtn.setOnClickListener {
                SharedPreferencesManager.clearAlarmList(this@MainActivity)
                deleteAllBtn.hideView()
                tvNoAlarmFoundText.showView()
                alarmList.clear()
                updateTheRecycler()
                WorkManager.getInstance(this@MainActivity)
                    .cancelUniqueWork(Constants.NOTIFICATION_WORK)
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }
        }
    }

    private fun openTheAddAlarmDialog() {
        val customDialog = CustomDialogFragment(alarmList) {
            binding.progress.hideView()
            binding.tvNoAlarmFoundText.hideView()
            binding.deleteAllBtn.showView()
            updateTheRecycler()
            scheduleTask(getTheTargetInMillis(alarmList[alarmList.size - 1]))
        }
        customDialog.show(supportFragmentManager, "customDialog")
    }


    private fun getTheTargetInMillis(alarmModel: AlarmModel): Long {
        val time = alarmModel.alarmTime  // e.g., "10:30 AM"
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.US)

        // Parse the target time
        val targetTime = dateFormat.parse(time)

        // Get the current time in milliseconds
        val currentTimeMillis = System.currentTimeMillis()

        // Get the target time in milliseconds for today
        val calendar = Calendar.getInstance()
        calendar.time = targetTime
        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        // Check if the target time is already passed for today
        if (calendar.timeInMillis < currentTimeMillis) {
            // If it's passed, set it to the same time tomorrow
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Calculate the difference in milliseconds
        return calendar.timeInMillis - currentTimeMillis
    }


    override fun deleteBtnClicked(data: AlarmModel, position: Int) {
        alarmList.remove(data)
        binding.rvAlarmRecycler.adapter?.notifyItemRemoved(position)
        this.workOnBackgroundThread { SharedPreferencesManager.saveAlarmList(this, alarmList) }
        if (alarmList.isEmpty()) {
            binding.tvNoAlarmFoundText.showView()
            binding.deleteAllBtn.hideView()
            WorkManager.getInstance(this@MainActivity).cancelUniqueWork(Constants.NOTIFICATION_WORK)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }
    }

    override fun switchStateIsChanges(position: Int) {
        if (!alarmList[position].isActive) {
            scheduleTask(getTheTargetInMillis(alarmList[position]))

        }
        alarmList[position].isActive = !alarmList[position].isActive
        for (i in 0..<alarmList.size) {
            if (i != position) alarmList[i].isActive = false
        }
        updateTheRecycler()
        this.workOnBackgroundThread { SharedPreferencesManager.saveAlarmList(this, alarmList) }


    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openTheAddAlarmDialog()
            } else {
                requestNotificationPermission()
            }
        }

    // Show an explanation dialog and prompt user to enable notification permission in settings
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionDialog(needToMoveToSettings: Boolean) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.notification_permission_needed))
            .setMessage(getString(R.string.to_receive_notifications_related_to_alarms_please_enable_the_notification_permission))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.grant_permission)) { _, _ ->
                if (needToMoveToSettings) {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    startActivity(intent)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                // Show a rationale to the user
                showPermissionDialog(false)
            } else {
                showPermissionDialog(true)
            }
        } else {
            openTheAddAlarmDialog()
        }
    }

    /*
   * Check for does user has granted the permission for the notification in android 13 device or above
   * */
    private fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.areNotificationsEnabled()
        } else {
            true // For Android versions below 13, assume permission is granted
        }
    }


    private fun scheduleTask(targetTimeInMillis: Long) {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(targetTimeInMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            Constants.NOTIFICATION_WORK,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateTheRecycler() {
        binding.rvAlarmRecycler.adapter?.notifyDataSetChanged()
    }

}