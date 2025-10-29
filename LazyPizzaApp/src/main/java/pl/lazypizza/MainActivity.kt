package pl.lazypizza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.Firebase
import com.google.firebase.initialize
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level
import pl.lazypizza.di.initKoin
import pl.lazypizza.presentation.navigation.AppNavigation
import pl.lazypizza.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private var keepSplashOnScreen = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Keep splash screen visible for the animation duration
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }
        
        // Hide splash screen after animation completes
        window.decorView.postDelayed({
            keepSplashOnScreen = false
        }, 2500L)
        
        // Initialize Firebase
        Firebase.initialize(this)
        
        // Initialize Koin if not already started
        if (GlobalContext.getOrNull() == null) {
            initKoin {
                androidLogger(Level.ERROR)
                androidContext(applicationContext)
            }
        }
        
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavigation()
            }
        }
    }
}