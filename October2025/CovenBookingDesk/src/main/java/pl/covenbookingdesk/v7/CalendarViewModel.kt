package pl.covenbookingdesk.v7

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.covenbookingdesk.v7.database.ReservationDatabase
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

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()
    


    public fun loadCalendarMonths():List<CalendarMonth> {
        val currentYear = 2025
        val previousYear = currentYear - 1
        val nextYear = currentYear + 1
        
        val allMonths = mutableListOf<CalendarMonth>()
        
        val reservations = _uiState.value.reservations
        
        allMonths.addAll(CalendarUtils.generateMonthsForYear(previousYear, _uiState.value.selectedDate, reservations))
        allMonths.addAll(CalendarUtils.generateMonthsForYear(currentYear, _uiState.value.selectedDate, reservations))
        allMonths.addAll(CalendarUtils.generateMonthsForYear(nextYear, _uiState.value.selectedDate, reservations))
        
        _uiState.value = _uiState.value.copy(calendarMonths = allMonths)

         return allMonths
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
    

    

}