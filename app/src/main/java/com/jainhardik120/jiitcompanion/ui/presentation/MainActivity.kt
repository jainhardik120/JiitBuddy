package com.jainhardik120.jiitcompanion.ui.presentation

import android.os.Bundle
import android.util.Log
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
//import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.jainhardik120.jiitcompanion.R
import com.jainhardik120.jiitcompanion.ui.presentation.root.RootNavigationGraph
import com.jainhardik120.jiitcompanion.ui.theme.JIITBuddyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config)
        remoteConfig.fetchAndActivate()
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
                    RootNavigationGraph(navController = rememberNavController(), onReview = {showFeedbackDialog()})
                }
            }
        }
    }

    private fun showFeedbackDialog(){
        Log.d("MainActivity", "showFeedbackDialog: Called")
        val reviewManager = ReviewManagerFactory.create(applicationContext)
        reviewManager.requestReviewFlow().addOnCompleteListener{
            if(it.isSuccessful){
                reviewManager.launchReviewFlow(this, it.result)
            }
        }
    }
}