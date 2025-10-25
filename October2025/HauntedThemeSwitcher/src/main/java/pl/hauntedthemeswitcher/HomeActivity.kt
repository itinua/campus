package pl.hauntedthemeswitcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import pl.hauntedthemeswitcher.ui.theme.CampusTheme


val colorDay = Color("#FEEEE2".toColorInt())
val colorNight = Color("#4D2EAA".toColorInt())

class HomeActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            var isDay by remember { mutableStateOf(true) }

            val animatedColor by animateColorAsState(
                if (isDay) colorDay else colorNight, label = "color"
            )
            val offset by animateIntOffsetAsState(
                targetValue = if (isDay) {
                    IntOffset.Zero
                } else {
                    IntOffset.Zero

                }, label = "offset"
            )

            CampusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding()
                            .background(animatedColor)
                    ) {
                        var state = rememberScrollState()
                        LaunchedEffect(Unit) {
                            state.scrollTo(2000)
                        }


                        Image(
                            painter = painterResource(R.drawable.graveyard),
                            modifier = Modifier
                                .fillMaxSize()
                                .horizontalScroll(state,true)
                                .offset { offset },

                            contentScale = ContentScale.FillHeight,
                            contentDescription = ""
                        )
                        LaunchedEffect(isDay) {
                            while (true) {
                                state.animateScrollTo(if(isDay) 2000 else 0, animationSpec = tween (durationMillis = 1000))

                            }
                        }



                        val configuration = LocalConfiguration.current



                        val sunOffset by animateFloatAsState(if (isDay) 200f else -100f, animationSpec = tween(1000))
                        val cloud1 by animateFloatAsState(if (isDay) 100f else 300f, animationSpec = tween(1000))
                        val cloud2 by animateFloatAsState(if (isDay) 20f else 400f, animationSpec = tween(1000))
                        val cloud3 by animateFloatAsState(if (isDay) 300f else -300f, animationSpec = tween(1000))
                        val ghost by animateFloatAsState(if (isDay) 0.0f else 1.0f, animationSpec = tween(1000))
                        val moon by animateFloatAsState(if (isDay) 0.0f else 1.0f, animationSpec = tween(2000))

                        val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
                        val animatedGhost by infiniteTransition.animateFloat(
                            initialValue = 50f,
                            targetValue = 250f,
                            animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
                        )
                        val animatedCloud1 by infiniteTransition.animateFloat(
                            initialValue = 100f,
                            targetValue = 150f,
                            animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
                        )

                        Image(painterResource(R.drawable.cloud_01), contentDescription = "",
                            modifier = Modifier.offset(x=animatedCloud1.dp,y=10.dp).alpha(1-ghost))

                        Image(painterResource(R.drawable.cloud_02), contentDescription = "",
                            modifier = Modifier.offset(x=cloud2.dp,y=200.dp))

                        Image(painterResource(R.drawable.cloud_03), contentDescription = "",
                            modifier = Modifier.offset(x=cloud3.dp,y=150.dp))

                        Image(painterResource(R.drawable.sun), contentDescription = "",
                            modifier = Modifier.offset(x=sunOffset.dp,y=150.dp))

                        Image(painterResource(R.drawable.subtract), contentDescription = "",
                            modifier = Modifier.offset(200.dp, 200.dp).alpha(moon))


                        Image(painterResource(R.drawable.ghost), contentDescription = "",
                            modifier = Modifier.offset(animatedGhost.dp,(600-animatedGhost).dp).alpha(ghost))





                        val pWidth = 160
                        val pHeight = 50

                        val pumpkinOffset by animateFloatAsState(if (isDay) 10f else (pWidth - pHeight).toFloat())

                        Box(
                            modifier = Modifier
                                .padding(bottom = 30.dp)
                                .clickable(onClick = { isDay = !isDay })
                                .width(pWidth.dp)
                                .height(pHeight.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .align(Alignment.BottomCenter),
                            contentAlignment = Alignment.CenterStart

                        ) {
                            Image(
                                painter = painterResource(R.drawable.pumpkin),
                                modifier = Modifier
                                    .size((pHeight - 8).dp)
                                    .offset(x = pumpkinOffset.dp, y = (-4).dp),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }
}



