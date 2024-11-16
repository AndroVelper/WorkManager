//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.shubham.alarmassignment.roomManager.AlarmDao
//import com.shubham.alarmassignment.roomManager.AlarmEntity
//
//@Database(entities = [AlarmEntity::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun alarmDao(): AlarmDao
//}
//
//object DatabaseProvider {
//    @Volatile
//    private var appDatabase: AppDatabase? = null
//
//    // Provides a singleton instance of AppDatabase
//    fun provideDatabase(context: Context): AppDatabase {
//        if (appDatabase == null) {
//            val obj = Room.databaseBuilder(
//                context,
//                AppDatabase::class.java,
//                "alarm_database"
//            )
//
//            obj.build()
//        }
//        return appDatabase!!
//    }
//
//    // Provides the AlarmRepository instance using the database instance
//    fun provideAlarmRepository(context: Context): AlarmRepository {
//        return AlarmRepository(provideDatabase(context).alarmDao())
//    }
//}
