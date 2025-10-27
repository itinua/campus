package pl.covenbookingdesk.v7.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ReservationEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class ReservationDatabase : RoomDatabase() {
    
    abstract fun reservationDao(): ReservationDao
    
    companion object {
        @Volatile
        private var INSTANCE: ReservationDatabase? = null
        
        fun getDatabase(context: Context): ReservationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReservationDatabase::class.java,
                    "reservation_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}