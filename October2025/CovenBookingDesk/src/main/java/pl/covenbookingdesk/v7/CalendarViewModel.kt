package pl.covenbookingdesk.v7

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.covenbookingdesk.v7.database.ReservationDatabase
import pl.covenbookingdesk.v7.database.ReservationEntity
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val calendarMonths: List<CalendarMonth> = emptyList(),
    val reservations: Map<LocalDate, ReservationInfo> = emptyMap(),
    val isDialogOpen: Boolean = false,
    val isBookingDialogOpen: Boolean = false
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = ReservationDatabase.getDatabase(application)
    private val reservationDao = database.reservationDao()
    
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()
    
    init {
        loadReservations()
        addSampleReservations()
    }
    
    private fun loadReservations() {
        viewModelScope.launch {
            reservationDao.getAllReservations().collectLatest { reservationList ->
                val reservationMap = reservationList.associate { reservation ->
                    reservation.date to ReservationInfo(
                        guestName = reservation.guestName,
                        notes = reservation.notes
                    )
                }
                _uiState.value = _uiState.value.copy(reservations = reservationMap)
                loadCalendarMonths()
            }
        }
    }
    
    private fun addSampleReservations() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val sampleReservations = listOf(
                ReservationEntity(
                    date = today.plusDays(3),
                    guestName = "John Smith",
                    notes = "2 guests, late check-in"
                ),
                ReservationEntity(
                    date = today.plusDays(7),
                    guestName = "Emily Johnson",
                    notes = "VIP guest"
                ),
                ReservationEntity(
                    date = today.plusDays(10),
                    guestName = "Michael Brown",
                    notes = "Extended stay"
                ),
                ReservationEntity(
                    date = today.plusDays(15),
                    guestName = "Sarah Davis",
                    notes = "Early check-in requested"
                ),
                ReservationEntity(
                    date = today.plusDays(20),
                    guestName = "Robert Wilson",
                    notes = "Special dietary requirements"
                )
            )
            
            sampleReservations.forEach { reservation ->
                if (reservationDao.getReservationByDate(reservation.date) == null) {
                    reservationDao.insertReservation(reservation)
                }
            }
        }
    }
    
    private fun loadCalendarMonths() {
        val currentYear = _uiState.value.currentYearMonth.year
        val previousYear = currentYear - 1
        val nextYear = currentYear + 1
        
        val allMonths = mutableListOf<CalendarMonth>()
        
        val reservations = _uiState.value.reservations
        
        allMonths.addAll(CalendarUtils.generateMonthsForYear(previousYear, _uiState.value.selectedDate, reservations))
        allMonths.addAll(CalendarUtils.generateMonthsForYear(currentYear, _uiState.value.selectedDate, reservations))
        allMonths.addAll(CalendarUtils.generateMonthsForYear(nextYear, _uiState.value.selectedDate, reservations))
        
        _uiState.value = _uiState.value.copy(calendarMonths = allMonths)
    }
    
    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadCalendarMonths()
    }
    
    fun openDialog() {
        _uiState.value = _uiState.value.copy(isDialogOpen = true)
    }
    
    fun closeDialog() {
        _uiState.value = _uiState.value.copy(isDialogOpen = false)
    }
    
    fun openBookingDialog() {
        _uiState.value = _uiState.value.copy(isBookingDialogOpen = true)
    }
    
    fun closeBookingDialog() {
        _uiState.value = _uiState.value.copy(isBookingDialogOpen = false)
    }
    
    fun confirmSelection() {
        closeDialog()
    }
    
    fun cancelSelection() {
        _uiState.value = _uiState.value.copy(
            selectedDate = LocalDate.now(),
            isDialogOpen = false
        )
        loadCalendarMonths()
    }
    
    fun makeReservation(guestName: String, notes: String?) {
        viewModelScope.launch {
            val reservation = ReservationEntity(
                date = _uiState.value.selectedDate,
                guestName = guestName,
                notes = notes
            )
            reservationDao.insertReservation(reservation)
            closeBookingDialog()
        }
    }
    
    fun cancelReservation(date: LocalDate) {
        viewModelScope.launch {
            reservationDao.deleteReservationByDate(date)
        }
    }
    
    fun clearAllReservations() {
        viewModelScope.launch {
            reservationDao.deleteAllReservations()
        }
    }
}