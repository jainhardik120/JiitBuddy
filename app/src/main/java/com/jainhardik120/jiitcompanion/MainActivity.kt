package com.jainhardik120.jiitcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.presentation.home.HomeScreen
import com.jainhardik120.jiitcompanion.presentation.login.LoginScreen
import com.jainhardik120.jiitcompanion.ui.theme.JIITBuddyTheme
import com.jainhardik120.jiitcompanion.uitl.Screen
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JIITBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.LoginScreen.route
                    ) {
                        composable(route = Screen.LoginScreen.route){
                            LoginScreen(onNavigate = {
                                navController.navigate(it.route)
                            })
                        }
                        composable(route = Screen.HomeScreen.route + "/{userInfo}/{token}",
                        arguments = listOf(
                            navArgument("userInfo"){
                                type = NavType.StringType
                                nullable = false
                            }, navArgument("token"){
                                type = NavType.StringType
                                nullable = true
                            }
                        )
                        ){
                            val userJson = it.arguments?.getString("userInfo")
                            val userObject = userJson?.let { it1 ->
                                Moshi.Builder().build().adapter(UserEntity::class.java).lenient().fromJson(
                                    it1
                                )
                            }
                            if (userObject != null) {
                                HomeScreen(userInfo = userObject)
                            }
                        }
                    }
                }
            }
        }
    }
}