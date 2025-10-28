package pl.covenbookingdesk.v7

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.covenbookingdesk.R
import pl.covenbookingdesk.ui.theme.AppTheme
import pl.covenbookingdesk.ui.theme.colors_slot
import pl.covenbookingdesk.ui.theme.colors_text_secondary
import pl.covenbookingdesk.v7.database.BookingEntity
import pl.covenbookingdesk.v7.database.SlotTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun SlotDialog(
    date: LocalDate,
    bookingsByDate: List<BookingEntity>,
    onClose: () -> Unit, onSlotSelected: (SlotTime) -> Unit
) {
    Column(
        Modifier
            .background(colors_slot)
            .padding(8.dp)
            .fillMaxSize()
    ) {

        IconButton(onClick = onClose, Modifier.align(Alignment.End)) {
            Image(
                painter = painterResource(
                    R.drawable.outline_close_24
                ),
                colorFilter = ColorFilter.tint(colors_text_secondary),
                contentDescription = ""

            )
        }
        val styledText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.White)) {
                append("Times Slots for ")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                append(date.format(DateTimeFormatter.ofPattern("YYYY-MM-DD")))
            }
        }

        Text(text = styledText, fontSize = 18.sp)

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(SlotTime.entries) { slot ->
                Column {
                    Button(
                        onClick = { onSlotSelected(slot) },
                        shape = RectangleShape,
                        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier.fillMaxWidth(),

                        ) {
                        val cur: BookingEntity? = bookingsByDate.find { it.slot == slot }
                        if (cur != null) {
                            Text(
                                "${slot.timeSlot} Reserved by ${cur.witch}",
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Gray,
                                textAlign = TextAlign.Start
                            )

                        } else {
                            Text(
                                "${slot.timeSlot} Available",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }


                    }
                    //Spacer(modifier = Modifier.height(4.dp))
                }

            }

        }


    }
}

@Preview(showBackground = true)
@Composable
fun SlotDialogPreview() {
    AppTheme {
        SlotDialog( LocalDate.now(), listOf(),{}, {})
    }
}

