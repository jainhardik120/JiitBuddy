package com.jainhardik120.jiitcompanion.ui.presentation.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jainhardik120.jiitcompanion.ui.presentation.home.HomeScreen
import com.jainhardik120.jiitcompanion.ui.presentation.login.LoginScreen

object PortalNavArguments {
    var arguments = listOf(
        navArgument("userInfo") {
            type = NavType.StringType
            nullable = false
        }, navArgument("token") {
            type = NavType.StringType
            nullable = true
        }
    )
}

@Composable
fun RootNavigationGraph(navController: NavHostController, onReview: ()-> Unit) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(onNavigate = {
                navController.popBackStack()
                navController.navigate(it.route)
            })
        }
        composable(
            route = Screen.HomeScreen.route + "/{userInfo}/{token}",
            arguments = PortalNavArguments.arguments
        ) {

            HomeScreen(
                onNavigateUp = { event ->
                    navController.popBackStack()
                    navController.navigate(event.route)
                },
                userInfo = it.arguments?.getString("userInfo") ?: "",
                token = it.arguments?.getString("token") ?: "",
                onReview = { onReview() }
            )
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
}