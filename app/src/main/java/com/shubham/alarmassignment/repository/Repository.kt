//import com.shubham.alarmassignment.roomManager.AlarmDao
//import com.shubham.alarmassignment.roomManager.AlarmEntity
//
//
///***
// * So this repository is working on the constructor injection and is directly returning the response get from the
// * local database
// *
// * @see insertAlarm : insert the data into the Database if the database already contains the data then update it
// * @see getAllAlarms : return the complete list of the alarms database has.
// * @see deleteAlarmById : delete the specific alarm from the Db.
// *
// * ***/
//class AlarmRepository(private val alarmDao: AlarmDao) {
//
//    // Insert an alarm into the database as this is a upsert query of if the same data exist it will update it
//    suspend fun insertAlarm(alarm: AlarmEntity) = alarmDao.upsert(alarm)
//
//    // Get all alarms from the database
//    suspend fun getAllAlarms(): List<AlarmEntity> = alarmDao.getAllAlarms()
//
//    // Delete an alarm by its ID
//    suspend fun deleteAlarmById(alarmId: String) = alarmDao.deleteAlarmById(alarmId)
//}
//
