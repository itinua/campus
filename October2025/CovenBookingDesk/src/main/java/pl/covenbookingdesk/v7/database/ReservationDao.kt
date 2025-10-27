package pl.covenbookingdesk.v7.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ReservationDao {
    
    @Query("SELECT * FROM reservations ORDER BY date ASC")
    fun getAllReservations(): Flow<List<ReservationEntity>>
    
    @Query("SELECT * FROM reservations WHERE date = :date")
    suspend fun getReservationByDate(date: LocalDate): ReservationEntity?
    
    @Query("SELECT * FROM reservations WHERE date BETWEEN :startDate AND :endDate")
    fun getReservationsBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<ReservationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity)
    
    @Update
    suspend fun updateReservation(reservation: ReservationEntity)
    
    @Delete
    suspend fun deleteReservation(reservation: ReservationEntity)
    
    @Query("DELETE FROM reservations WHERE date = :date")
    suspend fun deleteReservationByDate(date: LocalDate)
    
    @Query("DELETE FROM reservations")
    suspend fun deleteAllReservations()
}