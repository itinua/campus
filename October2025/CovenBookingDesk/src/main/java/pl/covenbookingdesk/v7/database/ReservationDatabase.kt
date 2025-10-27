package pl.covenbookingdesk.v7.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ReservationEntity::class, BookingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class,
        SlotTypeConverter::class)
abstract class ReservationDatabase : RoomDatabase() {
    
    abstract fun reservationDao(): ReservationDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: ReservationDatabase? = null
        
        fun getDatabase(context: Context): ReservationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReservationDatabase::class.java,
                    "room1"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}