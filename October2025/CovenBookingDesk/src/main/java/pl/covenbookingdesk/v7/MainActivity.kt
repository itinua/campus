package pl.covenbookingdesk.v7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.covenbookingdesk.ui.theme.Project2Theme
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    
    private val calendarViewModel: CalendarViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            Project2Theme {
                CalendarScreen(viewModel = calendarViewModel)
            }
        }
    }
}

@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val purpleBackground = Color(0xFF5A4A8F)
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Coven Booking Desk",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = purpleBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                        text = "Selected Arrival Date",
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
                    
                    Text(
                        text = uiState.selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Button(
                        onClick = { viewModel.openDialog() },
                        modifier = Modifier.fillMaxWidth(),
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
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = purpleBackground.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Features",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = purpleBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    FeatureItem("✓ Scrollable month view")
                    FeatureItem("✓ Clean calendar display - only current month dates shown")
                    FeatureItem("✓ Past and future year navigation")
                    FeatureItem("✓ Purple theme design")
                    FeatureItem("✓ Date selection with visual feedback")
                    FeatureItem("✓ Auto-scroll to selected month")
                }
            }
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
    }
}

@Composable
private fun FeatureItem(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Color(0xFF5A4A8F),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}