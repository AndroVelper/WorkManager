package com.shubham.alarmassignment.communicator

import com.shubham.alarmassignment.data.AlarmModel


/***
 * This is to provide the callback of the delete btn clicked by the user
 *  deleteBtnClicked provide the callback
 *  id : id of the alarm which we want to delete
 *
 * @see
 * ***/
interface IAlarmCallbackProvider {
    fun deleteBtnClicked(data: AlarmModel, position: Int)
    fun switchStateIsChanges(position: Int)
}