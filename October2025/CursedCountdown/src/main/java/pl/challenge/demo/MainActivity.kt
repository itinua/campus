package pl.challenge.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import pl.challenge.demo.theme.Project2Theme
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import java.time.Month


val RoadRageFontFamily = FontFamily(
    Font(
        resId = R.font.roadrage_regular, weight = FontWeight.Normal
    )
)

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            //.padding(innerPadding)
                            .background(Color("#2c1b53".toColorInt()))
                    ) {
                        Image(
                            painter = painterResource(R.drawable.graveyard2),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds,
                            contentDescription = ""
                        )

                        val infiniteTransition =
                            rememberInfiniteTransition(label = "infinite transition")
                        val anamateScale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.1f,
                            animationSpec = infiniteRepeatable(tween(5000), RepeatMode.Reverse),
                        )

                        Image(
                            painter = painterResource(R.drawable.group),
                            modifier = Modifier
                                .size(300.dp)
                                .scale(anamateScale),
                            contentDescription = ""
                        )
                        Box(modifier = Modifier.align(Alignment.Center)) {
                            //Text("Hello")
                            Timer()
                        }
                    }

                }

            }
        }
    }
}

@Composable
fun Timer() {
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(20.dp)
            )
            .alpha(1f)
            .background(Color("#ff934a".toColorInt()))
            .width(300.dp)
            .height(110.dp),
        contentAlignment = Alignment.Center
    ) {

        var time by remember { mutableStateOf(displayTimeToHalloween()) }
        LaunchedEffect(Unit) {
            while (true) {
                time = displayTimeToHalloween()
                delay(1000)
            }
        }

        Row(modifier = Modifier.fillMaxHeight()) {
            AnimatedText("${time.days}D", 60.dp)
            Dots(" ")
            AnimatedText("${time.hours}", 45.dp)
            Dots(":")
            AnimatedText("${time.minutes}", 45.dp)
            Dots(":")
            AnimatedText("${time.seconds}", 45.dp)

            //AnimatedText(current.toString())
        }
    }

}

@Composable
fun Dots(text: String) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .offset(y = (-3).dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            textAlign = TextAlign.Start, text = text, style = TextStyle(
                fontSize = 60.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = RoadRageFontFamily,
                fontStyle = FontStyle.Normal,

                color = Color(0xff1a0d39),
                letterSpacing = 1.2.sp
            )
        )
    }
}

data class Time(
    val days: Int, val hours: Int, val minutes: Int, val seconds: Int
)


fun displayTimeToHalloween(): Time {
    val now = LocalDateTime.now()
    val halloween = LocalDateTime.of(now.year, Month.OCTOBER, 31, 0, 0, 0)

    // If current date is past Halloween, use next year's Halloween
    val targetHalloween = if (now.isAfter(halloween)) {
        halloween.plusYears(1)
    } else {
        halloween
    }

    val duration = Duration.between(now, targetHalloween)

    val days = duration.toDays()
    val hours = duration.toHours() % 24
    val minutes = duration.toMinutes() % 60
    val seconds = duration.seconds % 60
    return Time(days.toInt(), hours.toInt(), minutes.toInt(), seconds.toInt())
}


@Composable
fun AnimatedText(text1: String, width: Dp) {
    var text = text1
    if (text.length == 1) {
        text = text.prependIndent("0")
    }
    val duration:Int = 500

    AnimatedContent(
        modifier = Modifier
            .fillMaxHeight()
            .width(width),
        targetState = text,



        transitionSpec = {
            slideInVertically(
                animationSpec = tween(
                    durationMillis = duration, easing = LinearOutSlowInEasing
                )
            ) { it / 2 } + fadeIn(
                animationSpec = tween(
                    durationMillis = duration, easing = LinearOutSlowInEasing
                )
            ) togetherWith slideOutVertically(
                animationSpec = tween(durationMillis = duration, easing = LinearOutSlowInEasing)
            ) { -it / 2 } + fadeOut(
                animationSpec = tween(
                    durationMillis = duration, easing = LinearOutSlowInEasing
                )
            )
        },
        contentAlignment = Alignment.CenterEnd,
    ) { char ->
        Box(modifier = Modifier.fillMaxHeight()) {
            Text(
                modifier = Modifier.align(Alignment.Center), text = text, style = TextStyle(
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = RoadRageFontFamily,
                    fontStyle = FontStyle.Normal,

                    color = Color(0xff1a0d39),
                    letterSpacing = 1.2.sp
                )
            )
        }
    }


}