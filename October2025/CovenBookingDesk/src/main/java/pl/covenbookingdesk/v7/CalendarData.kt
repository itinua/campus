package pl.covenbookingdesk.v7

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isSelected: Boolean = false,
    val isToday: Boolean = false,
    val isReserved: Boolean = false,
    val reservation: ReservationInfo? = null
)

data class ReservationInfo(
    val guestName: String,
    val notes: String? = null
)

data class CalendarMonth(
    val yearMonth: YearMonth,
    val days: List<CalendarDay>
)

object CalendarUtils {
    
    fun generateCalendarDays(
        yearMonth: YearMonth,
        selectedDate: LocalDate? = null,
        reservations: Map<LocalDate, ReservationInfo> = emptyMap()
    ): List<CalendarDay> {
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val today = LocalDate.now()
        
        val days = mutableListOf<CalendarDay>()
        
        val previousMonth = yearMonth.minusMonths(1)
        val daysInPreviousMonth = previousMonth.lengthOfMonth()
        for (i in firstDayOfWeek downTo 1) {
            val date = previousMonth.atDay(daysInPreviousMonth - i + 1)
            days.add(
                CalendarDay(
                    date = date,
                    isCurrentMonth = false,
                    isSelected = date == selectedDate,
                    isToday = date == today,
                    isReserved = reservations.containsKey(date),
                    reservation = reservations[date]
                )
            )
        }
        
        for (dayOfMonth in 1..yearMonth.lengthOfMonth()) {
            val date = yearMonth.atDay(dayOfMonth)
            days.add(
                CalendarDay(
                    date = date,
                    isCurrentMonth = true,
                    isSelected = date == selectedDate,
                    isToday = date == today,
                    isReserved = reservations.containsKey(date),
                    reservation = reservations[date]
                )
            )
        }
        
        val remainingDays = 42 - days.size
        val nextMonth = yearMonth.plusMonths(1)
        for (dayOfMonth in 1..remainingDays) {
            val date = nextMonth.atDay(dayOfMonth)
            days.add(
                CalendarDay(
                    date = date,
                    isCurrentMonth = false,
                    isSelected = date == selectedDate,
                    isToday = date == today,
                    isReserved = reservations.containsKey(date),
                    reservation = reservations[date]
                )
            )
        }
        
        return days
    }
    
    fun generateMonthsForYear(
        year: Int,
        selectedDate: LocalDate? = null,
        reservations: Map<LocalDate, ReservationInfo> = emptyMap()
    ): List<CalendarMonth> {
        return (1..12).map { month ->
            val yearMonth = YearMonth.of(year, month)
            CalendarMonth(
                yearMonth = yearMonth,
                days = generateCalendarDays(yearMonth, selectedDate, reservations)
            )
        }
    }
    
    fun formatSelectedDate(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
        val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
        val dayOfMonth = date.dayOfMonth
        return "$dayOfWeek, $month $dayOfMonth"
    }
}