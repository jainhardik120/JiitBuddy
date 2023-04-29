package com.jainhardik120.jiitcompanion.ui.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jainhardik120.jiitcompanion.ui.presentation.root.RootNavigationGraph
import com.jainhardik120.jiitcompanion.ui.theme.JIITBuddyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {

            JIITBuddyTheme {
                val systemUiController = rememberSystemUiController()
                val isSystemInDarkTheme = isSystemInDarkTheme()
                val navbarScrimColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                LaunchedEffect(systemUiController, isSystemInDarkTheme, navbarScrimColor) {
                    systemUiController.setNavigationBarColor(
                        color = navbarScrimColor
                    )
                }
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