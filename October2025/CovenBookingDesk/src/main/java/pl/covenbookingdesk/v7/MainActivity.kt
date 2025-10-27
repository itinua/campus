package pl.covenbookingdesk.v7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.covenbookingdesk.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                CalendarScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    val context = LocalContext.current
//    val viewModel: CalendarViewModel = viewModel(
//        factory = CalendarViewModelFactory(context.applicationContext as android.app.Application)
//    )
    val viewModel = viewModel<CalendarViewModel>()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val purpleBackground = Color(0xFF5A4A8F)
    var selectedReservation by remember { mutableStateOf<Pair<LocalDate, ReservationInfo>?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Coven Booking Desk",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = purpleBackground,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(
                        onClick = { viewModel.clearAllReservations() }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Clear All",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openBookingDialog() },
                containerColor = purpleBackground
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Booking",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Selected Date",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = CalendarUtils.formatSelectedDate(uiState.selectedDate),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val selectedDateReservation = uiState.reservations[uiState.selectedDate]
                    if (selectedDateReservation != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFF6B6B).copy(alpha = 0.1f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "Reserved by: ${selectedDateReservation.guestName}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFFF6B6B)
                                )
                                selectedDateReservation.notes?.let { notes ->
                                    Text(
                                        text = "Notes: $notes",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Available for booking",
                            fontSize = 14.sp,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Button(
                        onClick = { viewModel.openDialog() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = purpleBackground
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Choose Date",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = purpleBackground.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Upcoming Reservations",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = purpleBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val upcomingReservations = uiState.reservations
                        .filter { it.key >= java.time.LocalDate.now() }
                        .toList()
                        .sortedBy { it.first }
                        .take(5)

                    if (upcomingReservations.isEmpty()) {
                        Text(
                            text = "No upcoming reservations",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        upcomingReservations.forEach { (date, reservation) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                onClick = {
                                    selectedReservation = date to reservation
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = purpleBackground
                                        )
                                        Text(
                                            text = reservation.guestName,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                Color(0xFFFF6B6B),
                                                shape = androidx.compose.foundation.shape.CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Legend",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = purpleBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    Color(0xFFFF6B6B),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        Text(
                            text = "Reserved Date",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    Color.White,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .padding(1.dp)
                                .background(
                                    purpleBackground,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        Text(
                            text = "Selected Date",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        if (uiState.isDialogOpen) {
            CustomCalendarDialog(
                selectedDate = uiState.selectedDate,
                calendarMonths = uiState.calendarMonths,
                onDateSelected = { date ->
                    viewModel.selectDate(date)
                },
                onDismiss = {
                    viewModel.cancelSelection()
                },
                onConfirm = {
                    viewModel.confirmSelection()
                }
            )
        }

        if (uiState.isBookingDialogOpen) {
            BookingDialog(
                selectedDate = uiState.selectedDate,
                onConfirm = { guestName, notes ->
                    viewModel.makeReservation(guestName, notes)
                },
                onDismiss = {
                    viewModel.closeBookingDialog()
                }
            )
        }

        selectedReservation?.let { (date, reservation) ->
            ReservationDetailsDialog(
                reservation = reservation,
                date = date,
                onCancel = {
                    viewModel.cancelReservation(date)
                    selectedReservation = null
                },
                onDismiss = {
                    selectedReservation = null
                }
            )
        }
    }
}