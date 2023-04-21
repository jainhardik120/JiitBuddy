package com.jainhardik120.jiitcompanion.util.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.ui.presentation.home.HomeScreen
import com.jainhardik120.jiitcompanion.ui.presentation.login.LoginScreen
import com.squareup.moshi.Moshi

@Composable
fun RootNavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, route = Graph.ROOT, startDestination = Screen.LoginScreen.route){
        composable(route = Screen.LoginScreen.route){
            LoginScreen(onNavigate = {
                navController.popBackStack()
                navController.navigate(it.route)
            })
        }
        composable(
            route = Screen.HomeScreen.route + "/{userInfo}/{token}",
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
                HomeScreen(userInfo = userObject, token = it.arguments?.getString("token"))
            }
        }
    }
}
object Graph{
    const val ROOT = "root_graph"
}