package com.gunishjain.locationtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gunishjain.locationtracker.ui.register.RegisterScreen
import com.gunishjain.locationtracker.ui.theme.LocationTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationTrackerTheme {

                RegisterScreen()

            }

        }
    }
}


