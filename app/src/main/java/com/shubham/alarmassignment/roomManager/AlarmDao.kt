//package com.shubham.alarmassignment.roomManager
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//import androidx.room.Upsert
//
//@Dao
//interface AlarmDao {
//
//    @Insert
//    suspend fun upsert(alarm: AlarmEntity)
//
//    @Query("SELECT * FROM alarms")
//    suspend fun getAllAlarms(): List<AlarmEntity>
//
//    @Query("DELETE FROM alarms WHERE uid = :alarmId")
//    suspend fun deleteAlarmById(alarmId: String)
//}