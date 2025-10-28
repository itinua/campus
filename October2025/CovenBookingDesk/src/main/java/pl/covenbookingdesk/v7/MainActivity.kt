package pl.covenbookingdesk.v7

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.covenbookingdesk.R
import pl.covenbookingdesk.ui.theme.AppTheme
import pl.covenbookingdesk.ui.theme.colors_bg
import java.time.LocalDate
import java.time.format.DateTimeFormatter


data class Whitch(
    val name: String,
    @DrawableRes val imageId: Int,
    @DrawableRes val starId: Int,
    val isSelected: Boolean = false
)

val witchList = listOf(
    Whitch("Morgana", R.drawable.w1, R.drawable.star),
    Whitch("Selena", R.drawable.w2, R.drawable.property_1_moon),
    Whitch("Hecate", R.drawable.w3, R.drawable.star),
    Whitch("Elvira", R.drawable.w4, R.drawable.property_1_moon),
    Whitch("Nyx", R.drawable.w5, R.drawable.star),
    Whitch("Circe", R.drawable.w6, R.drawable.property_1_moon),
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            AppTheme {
                MainContent()
                //CalendarScreen()


//                val viewModel = viewModel<BookingViewModel>()
//
//                val witch = "Selena"
//
//                val bookings by viewModel.getBookingsByWitch(witch).collectAsState(emptyList())
//                LazyColumn {
//                    items(bookings){item->
//                        Text("Booking ${item.date} ${item.slot}")
//                    }
//                }
//                Button(onClick = {
//                    viewModel.insertBookings("2023-04-34", SlotTime.SLOT_0200, witch)
//                }) {
//                    Text("Add")
//                }


            }
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun MainContent() {
    val viewModel = viewModel<BookingViewModel>()
    var currentWitch by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    val bookingsByWitch by viewModel.getBookingsByWitch(currentWitch).collectAsState(emptyList())
    val bookingsByDate by viewModel.getBookingsByDate(currentDate).collectAsState(emptyList())

    Box {
        BackgrounImage()
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            Text(
                "Coven Booking Desk",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )


            var modelItems by remember { mutableStateOf(witchList) }


            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(modelItems.size) { id ->
                    val current = modelItems[id]
                    ComposableItem(current, { updatedWhitch ->
                        modelItems = modelItems.map { witch ->
                            if (witch.imageId == updatedWhitch.imageId) {
                                val isNowSelected = !updatedWhitch.isSelected
                                currentWitch = if (isNowSelected) updatedWhitch.name else ""
                                updatedWhitch.copy(isSelected = isNowSelected)
                            } else {
                                witch.copy(isSelected = false)
                            }
                        }
                    })
                }
            }

            Spacer(Modifier.height(40.dp))

            //Text("Size By date: ${bookingsByDate.size}", color = Color.White)

            if (bookingsByWitch.isEmpty()) {
                if (currentWitch.isNotEmpty()) {
                    Text(
                        "No reservation", color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                LazyColumn {
                    items(bookingsByWitch) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Booked:",
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .weight(1f),
                                color = Color.White,
                            )
                            Text(
                                text = "${it.date.format(DateTimeFormatter.ofPattern("MMM dd"))} ${it.slot.timeSlot}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                            IconButton(onClick = {
                                viewModel.deleteBookings(it)
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_close_24),
                                    tint = Color.White.copy(0.5f),
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                }
            }


            var isShowDateDialog by remember { mutableStateOf(false) }
            var isShowSlotDialog by remember { mutableStateOf(false) }

            if (isShowDateDialog) {


                val calendarData: List<CalendarMonth> = remember {

                    val currentYear = 2025
                    val previousYear = currentYear - 1
                    val nextYear = currentYear + 1

                    val allMonths = mutableListOf<CalendarMonth>()

                    allMonths.addAll(
                        CalendarUtils.generateMonthsForYear(
                            previousYear,
                            LocalDate.now()
                        )
                    )
                    allMonths.addAll(
                        CalendarUtils.generateMonthsForYear(
                            currentYear,
                            LocalDate.now()
                        )
                    )
                    allMonths.addAll(CalendarUtils.generateMonthsForYear(nextYear, LocalDate.now()))
                    allMonths
                }

                CustomCalendarDialog(
                    selectedDate = currentDate,
                    calendarMonths = calendarData,
                    onDateSelected = { date ->
                        currentDate = date
                    },
                    onDismiss = {
                        isShowDateDialog = false
                    },
                    onConfirm = {
                        isShowDateDialog = false
                        isShowSlotDialog = true
                    }
                )

            }

            if (isShowSlotDialog) {
                Dialog(onDismissRequest = {
                    isShowSlotDialog = false
                }) {
                    SlotDialog(
                        currentDate,
                        bookingsByDate,
                        onClose = { isShowSlotDialog = false },
                        onSlotSelected = { slot ->
                            if (currentWitch.isNotEmpty()) {
                                viewModel.insertBookings(currentDate, slot, currentWitch)
                                isShowSlotDialog = false
                            }
                        })
                }
            }


            if (currentWitch.isNotEmpty()) {
                ItemButton("Add arrival dates", onClick = {
                    isShowDateDialog = true
                })
            }

            Spacer(modifier = Modifier.weight(1f))
            if (!bookingsByWitch.isEmpty()) {
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF934A)),
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    Text(
                        "$currentWitch is booked for the Gathering",
                        color = colors_bg,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

        }
    }
}