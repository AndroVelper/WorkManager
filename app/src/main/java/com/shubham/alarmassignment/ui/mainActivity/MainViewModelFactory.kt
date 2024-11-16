//package com.shubham.alarmassignment.ui.mainActivity
//
//import AlarmRepository
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//
//class AlarmViewModelFactory(private val alarmRepository: AlarmRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return modelClass.getConstructor(AlarmRepository::class.java).newInstance(alarmRepository)
//    }
//}