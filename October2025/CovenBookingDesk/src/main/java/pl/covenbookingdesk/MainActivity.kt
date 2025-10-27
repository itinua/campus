package pl.covenbookingdesk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.covenbookingdesk.ui.theme.Project2Theme

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {

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
