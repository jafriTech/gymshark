package com.jafritech.gymshark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jafritech.gymshark.presentation.navigation.AppNavHost
import com.jafritech.gymshark.presentation.ui.theme.GymSharkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymSharkTheme {
                AppNavHost()
            }
        }
    }
}
