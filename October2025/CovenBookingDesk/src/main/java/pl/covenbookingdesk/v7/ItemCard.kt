package pl.covenbookingdesk.v7

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.covenbookingdesk.R
import pl.covenbookingdesk.ui.theme.AppTheme
import pl.covenbookingdesk.ui.theme.colors_frame_active
import pl.covenbookingdesk.ui.theme.colors_outline_active


@Composable
fun ComposableItem(witch: Whitch, onMyClick: (Whitch) -> Unit) {
    Box() {
        Column(

            Modifier
                .clickable(onClick = {
                    onMyClick(witch)
                })
                .then(if(witch.isSelected)
                    Modifier.background(color = colors_frame_active)
                    else
                    Modifier.background(color = colors_outline_active)
                )

                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.padding(2.dp)) {
                Image(
                    painter = painterResource(witch.imageId),
                    modifier = Modifier.size(120.dp),
                    contentDescription = ""
                )
                Image(
                    painter = painterResource(R.drawable.frame),
                    modifier = Modifier.size(120.dp),
                    contentDescription = ""
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(

                    painter = painterResource(witch.starId),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(10.dp),
                    contentDescription = ""
                )


                Text(
                    witch.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall
                )
                Image(
                    painter = painterResource(witch.starId),
                    modifier = Modifier
                        .rotate(180f)
                        .padding(8.dp)
                        .size(10.dp),
                    contentDescription = ""
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComposableItemPreview() {
    AppTheme {
        ComposableItem(witchList[1],{})
    }
}