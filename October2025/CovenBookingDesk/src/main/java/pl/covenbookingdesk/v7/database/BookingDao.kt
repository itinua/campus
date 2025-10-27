package pl.covenbookingdesk.v7.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(item: BookingEntity)

    @Delete
    suspend fun deleteBooking(item: BookingEntity)

    @Query("SELECT * FROM booking WHERE date = :date")
    fun getAllBookingByDate(date: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM booking WHERE witch = :witch")
    fun getAllBookingByWitch(witch: String): Flow<List<BookingEntity>>

}