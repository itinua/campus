package com.example.demo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.demo.ui.theme.Project2Theme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        installSplashScreen().setOnExitAnimationListener { screen ->
            val scaleX = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_X, 1f, 0f)
            val scaleY = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_Y, 1f, 0f)
            val rotation = ObjectAnimator.ofFloat(screen.iconView, View.ROTATION, 0f, -360f)

            AnimatorSet().apply {
                //playTogether(scaleX, scaleY)

                playTogether(scaleX, scaleY,rotation) //bug with rotation in Android
                interpolator = AccelerateInterpolator()
                duration = 800L

                start()
                doOnEnd {
                    screen.remove()
                }
            }

        }
        installSplashScreen().setKeepOnScreenCondition {  !viewModel.isReady }

        enableEdgeToEdge()
        setContent {
            Project2Theme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()

                ) { innerPadding ->
                    Box(modifier = Modifier.background(Color(0xff1A0D39)).fillMaxSize()) {
                        Greeting(
                            name = "",
                            modifier = Modifier.padding(innerPadding).align (Alignment.Center)
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name",
        color = Color.White,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Project2Theme {
        Greeting("Android")
    }
}