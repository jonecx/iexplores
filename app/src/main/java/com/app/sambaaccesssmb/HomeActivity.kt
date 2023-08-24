package com.app.sambaaccesssmb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.app.sambaaccesssmb.ui.SmbApp
import dagger.hilt.android.AndroidEntryPoint
import org.bytedeco.ffmpeg.ffmpeg
import org.bytedeco.javacpp.Loader

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SmbApp(calculateWindowSizeClass(activity = this))
        }

        Loader.load(org.bytedeco.ffmpeg.ffmpeg::class.java)
    }
}
