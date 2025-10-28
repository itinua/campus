package pl.covenbookingdesk.v7

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CustomCalendarDialog(
    selectedDate: LocalDate,
    calendarMonths: List<CalendarMonth>,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val purpleBackground = Color(0xFF5A4A8F)
    val lightPurple = Color(0xFF7A6AAF)
    val textWhite = Color.White
    val textGray = Color(0xFFB0A0D0)
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.65f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = purpleBackground)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CalendarHeader(
                    selectedDate = selectedDate,
                    textColor = textWhite,
                    onClose = onDismiss
                )
                
                Spacer(
                    modifier = Modifier
                        .background(lightPurple)
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 16.dp)
                )
                
                DaysOfWeekHeader(textColor = textGray)
                
                CalendarMonthsList(
                    modifier = Modifier.weight(1f),
                    calendarMonths = calendarMonths,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected,
                    textWhite = textWhite,
                    textGray = textGray
                )

                Spacer(
                    modifier = Modifier
                        .background(lightPurple)
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 16.dp)
                )
                
                CalendarFooter(
                    onCancel = onDismiss,
                    onConfirm = onConfirm,
                    textColor = textWhite
                )
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    selectedDate: LocalDate,
    textColor: Color,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Choose Arrival Date",
                fontSize = 18.sp,
                color = textColor.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal
            )
            
            Text(
                text = "×",
                fontSize = 28.sp,
                color = textColor.copy(alpha = 0.8f),
                modifier = Modifier.clickable { onClose() }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = CalendarUtils.formatSelectedDate(selectedDate),
            fontSize = 42.sp,
            color = textColor,
            fontWeight = FontWeight.Light,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun DaysOfWeekHeader(textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
            Text(
                text = day,
                fontSize = 14.sp,
                color = textColor,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarMonthsList(
    modifier: Modifier = Modifier,
    calendarMonths: List<CalendarMonth>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    textWhite: Color,
    textGray: Color
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(calendarMonths) {
        val currentMonthIndex = calendarMonths.indexOfFirst { 
            it.yearMonth == YearMonth.from(selectedDate) 
        }
        if (currentMonthIndex >= 0) {
            //listState.animateScrollToItem(currentMonthIndex)
            listState.scrollToItem(currentMonthIndex)
        }
    }
    
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(calendarMonths) { month ->
            MonthCalendarView(
                month = month,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                textWhite = textWhite,
                textGray = textGray
            )
        }
    }
}

@Composable
private fun MonthCalendarView(
    month: CalendarMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    textWhite: Color,
    textGray: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "${month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.yearMonth.year}",
            fontSize = 16.sp,
            color = textGray,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        val rows = month.days.chunked(7)
        val filteredRows = rows.filter { week ->
            week.any { it.isCurrentMonth }
        }
        
        filteredRows.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { day ->
                    if (day.isCurrentMonth) {
                        DayCell(
                            day = day,
                            isSelected = day.date == selectedDate,
                            onDateSelected = { onDateSelected(day.date) },
                            textWhite = textWhite,
                            textGray = textGray
                        )
                    } else {
                        Spacer(
                            modifier = Modifier
                                .size(40.dp)
                                .aspectRatio(1f)
                                .padding(2.dp)
                        )
                    }
                }
                
                if (week.size < 7) {
                    repeat(7 - week.size) {
                        Spacer(
                            modifier = Modifier
                                .size(40.dp)
                                .aspectRatio(1f)
                                .padding(2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onDateSelected: () -> Unit,
    textWhite: Color,
    textGray: Color
) {
    val backgroundColor = when {
        isSelected && day.isCurrentMonth -> textWhite
        day.isReserved && day.isCurrentMonth -> Color(0xFFFF6B6B)
        else -> Color.Transparent
    }
    
    val textColor = when {
        isSelected -> Color(0xFF5A4A8F)
        day.isReserved -> Color.White
        else -> textWhite
    }
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(enabled = day.isCurrentMonth && !day.isReserved) {
                if (day.isCurrentMonth && !day.isReserved) onDateSelected()
            },
        contentAlignment = Alignment.Center
    ) {
        if (day.isCurrentMonth) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    fontSize = if (day.isReserved) 14.sp else 16.sp,
                    color = textColor,
                    fontWeight = if (isSelected || day.isReserved) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                if (day.isReserved) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarFooter(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Cancel",
                fontSize = 16.sp,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
        
        TextButton(
            onClick = onConfirm,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "OK",
                fontSize = 16.sp,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}