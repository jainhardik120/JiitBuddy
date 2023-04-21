package com.jainhardik120.jiitcompanion.ui.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.ui.presentation.attendance.AttendanceScreen
import com.jainhardik120.jiitcompanion.ui.presentation.exams.ExamsScreen
import com.jainhardik120.jiitcompanion.ui.presentation.grades.GradesScreen
import com.jainhardik120.jiitcompanion.ui.presentation.subjects.SubjectsScreen

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
            AttendanceScreen(userInfo = userEntity, token = token)
        }
        composable(route = BottomBarScreen.ExamSchedule.route){
            ExamsScreen(userInfo = userEntity, token = token)
        }
        composable(route = BottomBarScreen.Performance.route){
            GradesScreen(userInfo = userEntity, token = token)
        }
        composable(route = BottomBarScreen.Subjects.route){
            SubjectsScreen(userInfo = userEntity, token = token)
        }
    }
}
@Composable
fun ProfileCard(userEntity: UserEntity){
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
            Text(text = userEntity.name)
            Text(text = userEntity.enrollmentno)
            Text(text = userEntity.instituteLabel)
            Text(text = "${userEntity.programcode} ${userEntity.branch} ${userEntity.admissionyear} ${userEntity.batch}")
            Text(text = "${userEntity.userDOB} ${userEntity.gender}")
            Text(text = userEntity.studentcellno)
            Text(text = userEntity.studentpersonalemailid)
        }
    }
}