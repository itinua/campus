package pl.covenbookingdesk.v7

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun BookingDialog(
    selectedDate: LocalDate,
    onConfirm: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var guestName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val purpleBackground = Color(0xFF5A4A8F)
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Make Reservation",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = purpleBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Date: ${selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))}",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = guestName,
                    onValueChange = { guestName = it },
                    label = { Text("Guest Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purpleBackground,
                        focusedLabelColor = purpleBackground,
                        cursorColor = purpleBackground
                    ),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purpleBackground,
                        focusedLabelColor = purpleBackground,
                        cursorColor = purpleBackground
                    ),
                    maxLines = 3
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.Gray
                        )
                    }
                    
                    Button(
                        onClick = {
                            if (guestName.isNotBlank()) {
                                onConfirm(guestName, notes.ifBlank { null })
                            }
                        },
                        enabled = guestName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = purpleBackground
                        )
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationDetailsDialog(
    reservation: ReservationInfo,
    date: LocalDate,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    val purpleBackground = Color(0xFF5A4A8F)
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Reservation Details",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = purpleBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = purpleBackground.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Date",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Text(
                            text = "Guest Name",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = reservation.guestName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = if (reservation.notes != null) 12.dp else 0.dp)
                        )
                        
                        reservation.notes?.let { notes ->
                            Text(
                                text = "Notes",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = notes,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = "Close",
                            color = Color.Gray
                        )
                    }
                    
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B6B)
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Cancel Reservation")
                    }
                }
            }
        }
    }
}