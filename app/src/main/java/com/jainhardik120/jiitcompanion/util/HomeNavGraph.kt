package com.jainhardik120.jiitcompanion.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.ui.presentation.home.ProfileCard

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    userEntity: UserEntity,
    token:String?,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ){
        composable(route = BottomBarScreen.Home.route){
            ProfileCard(userEntity = userEntity)
        }
        composable(route = BottomBarScreen.Attendance.route){
            Text(text = "Attendance")
            ProfileCard(userEntity = userEntity)
        }
        composable(route = BottomBarScreen.ExamSchedule.route){
            ProfileCard(userEntity = userEntity)
        }
        composable(route = BottomBarScreen.Performance.route){
            ProfileCard(userEntity = userEntity)
        }
        composable(route = BottomBarScreen.Subjects.route){
            ProfileCard(userEntity = userEntity)
        }
    }
}
