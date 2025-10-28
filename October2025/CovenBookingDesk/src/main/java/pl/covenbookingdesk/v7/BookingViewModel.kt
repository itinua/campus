package pl.covenbookingdesk.v7

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pl.covenbookingdesk.v7.database.BookingEntity
import pl.covenbookingdesk.v7.database.ReservationDatabase
import pl.covenbookingdesk.v7.database.SlotTime
import java.time.LocalDate

class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val bookingDao = ReservationDatabase.getDatabase(application).bookingDao()

    fun getBookingsByWitch(witch: String): Flow<List<BookingEntity>> =
        bookingDao.getAllBookingByWitch(witch)

    fun getBookingsByDate(date: LocalDate): Flow<List<BookingEntity>> =
        bookingDao.getAllBookingByDate(date)

    fun insertBookings(date: LocalDate, slot: SlotTime, witch: String) {
        viewModelScope.launch {
            bookingDao.insertBooking(BookingEntity(date, slot, witch))
        }
    }

    fun deleteBookings(entity: BookingEntity) {
        viewModelScope.launch {
            bookingDao.deleteBooking(entity)
        }
    }
}


