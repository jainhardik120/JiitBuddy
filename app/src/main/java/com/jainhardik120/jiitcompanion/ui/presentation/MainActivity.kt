package com.jainhardik120.jiitcompanion.ui.presentation

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jainhardik120.jiitcompanion.ui.theme.JIITBuddyTheme
import com.jainhardik120.jiitcompanion.ui.presentation.root.RootNavigationGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
//            val systemUiController = rememberSystemUiController()
//            val isSystemInDarkTheme = isSystemInDarkTheme()
//            val navbarScrimColor = MaterialTheme.colorScheme.surface
//            LaunchedEffect(systemUiController, isSystemInDarkTheme, navbarScrimColor) {
//                systemUiController.setNavigationBarColor(
//                    color = if (true) {
//                        navbarScrimColor
//                    } else {
//                        Color.Transparent
//                    },
////                    darkIcons = !isSystemInDarkTheme,
////                    navigationBarContrastEnforced = false,
////                    transformColorForLightContent = { Color.Black },
//                )
//            }
            JIITBuddyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootNavigationGraph(navController = rememberNavController())
                }
            }
        }
    }
}