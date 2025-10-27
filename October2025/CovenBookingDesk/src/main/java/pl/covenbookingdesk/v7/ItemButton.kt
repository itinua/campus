package pl.covenbookingdesk.v7

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.covenbookingdesk.ui.theme.colors_frame_inactive
import pl.covenbookingdesk.ui.theme.colors_outline_active

@Composable
fun ItemButton(text:String, onClick:()-> Unit) {
    Box(Modifier.padding(12.dp).height(60.dp)) {
        Button(
            onClick = onClick,
            Modifier.fillMaxSize().padding(4.dp),
            shape = CutCornerShape(9.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors_frame_inactive),
        ) {
            Text(
                text, color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Box(
            modifier = Modifier
                .padding(6.dp)
                .border(0.5.dp, colors_outline_active)
                .fillMaxSize()
        ) {
        }


    }
}

@Preview(showBackground = true)
@Composable
fun ItemButtonPreview() {
    ItemButton("Choose item....",{})
}