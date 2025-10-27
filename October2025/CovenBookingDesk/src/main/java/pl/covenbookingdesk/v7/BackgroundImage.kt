package pl.covenbookingdesk.v7

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import pl.covenbookingdesk.R

@Composable
fun BackgrounImage(){
    Box(modifier = Modifier
        .background(Color(0xFF1A0D39))
        .fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.bg),
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            //contentScale = ContentScale.FillWidth,
            contentDescription = ""
        )
    }
}