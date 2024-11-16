//package com.shubham.alarmassignment.ui.mainActivity
//
//import AlarmRepository
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.shubham.alarmassignment.roomManager.AlarmEntity
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.async
//import kotlinx.coroutines.launch
//
//
///***
// * So this repository is working on the constructor injection and is directly returning the response get from the
// * local database
// *
// * This class use the IO dispatcher to launch all the task on the IO thread.
// *
// * @see insertAlarm : insert the data into the Database if the database already contains the data then update it
// * @see getAllAlarms : return the complete list of the alarms database has.
// * @see deleteAlarmById : delete the specific alarm from the Db.on successfully operation provide the updated list.
// *
// * ***/
//
//
//class AlarmViewModel(private val alarmRepository: AlarmRepository) : ViewModel() {
//
//    private val _alarmData: MutableLiveData<List<AlarmEntity>> = MutableLiveData()
//    val alarmData: LiveData<List<AlarmEntity>> get() = _alarmData
//
//    // Insert an alarm into the database
//    fun insertAlarm(alarm: AlarmEntity) {
//
//        viewModelScope.launch {
//            val insertJob = viewModelScope.async(Dispatchers.IO) {
//                alarmRepository.insertAlarm(alarm)
//            }
//            insertJob.await()
//            getAllAlarms()
//        }
//
//    }
//
//    // Get all alarms from the database
//    fun getAllAlarms() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val response = alarmRepository.getAllAlarms()
//            _alarmData.postValue(response)
//        }
//    }
//
//    // Delete an alarm by its ID
//    fun deleteAlarmById(alarmId: String) {
//        viewModelScope.launch {
//            val deleteJob = viewModelScope.async(Dispatchers.IO) {
//                alarmRepository.deleteAlarmById(alarmId)
//            }
//            deleteJob.await()
//            getAllAlarms()
//        }
//
//    }
//}
