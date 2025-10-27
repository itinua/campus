package pl.covenbookingdesk

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val calendarMonths: List<CalendarMonth> = emptyList(),
    val isDialogOpen: Boolean = false
)

class CalendarViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()
    
    init {
        loadCalendarMonths()
    }
    
    private fun loadCalendarMonths() {
        val currentYear = _uiState.value.currentYearMonth.year
        val previousYear = currentYear - 1
        val nextYear = currentYear + 1
        
        val allMonths = mutableListOf<CalendarMonth>()
        
        allMonths.addAll(CalendarUtils.generateMonthsForYear(previousYear, _uiState.value.selectedDate))
        allMonths.addAll(CalendarUtils.generateMonthsForYear(currentYear, _uiState.value.selectedDate))
        allMonths.addAll(CalendarUtils.generateMonthsForYear(nextYear, _uiState.value.selectedDate))
        
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