package com.jainhardik120.jiitcompanion.ui.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import com.jainhardik120.jiitcompanion.ui.components.icons.Insights
import com.jainhardik120.jiitcompanion.ui.components.icons.Monitor
import com.jainhardik120.jiitcompanion.ui.components.icons.School
import androidx.compose.ui.graphics.vector.ImageVector
import com.jainhardik120.jiitcompanion.ui.components.icons.Event


sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Filled.Home
    )

    object Attendance : BottomBarScreen(
        route = "attendance",
        title = "Attendance",
        icon = Icons.Filled.Monitor
    )

    object Performance : BottomBarScreen(
        route = "performance",
        title = "Grades",
        icon = Icons.Filled.Insights
    )

    object ExamSchedule : BottomBarScreen(
        route = "exam_schedule",
        title = "Exams",
        icon = Icons.Filled.Event
    )

    object Subjects : BottomBarScreen(
        route = "subjects",
        title = "Subjects",
        icon = Icons.Filled.School
    )
}
