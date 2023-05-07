package com.jainhardik120.jiitcompanion.ui.presentation.home

import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration
import com.jainhardik120.jiitcompanion.ui.presentation.grades.GradesScreenEvent

sealed class HomeScreenEvent{
    object onLogOutClicked:HomeScreenEvent()
    object onLogOutConfirmed:HomeScreenEvent()
    object offlineDialogConfirmed:HomeScreenEvent()
    object offlineDialogDismisse:HomeScreenEvent()
    object onLogOutDismissed:HomeScreenEvent()
    object onOfflineAlertClicked:HomeScreenEvent()
    data class bottomNavItemClicked(val screen:BottomBarScreen):HomeScreenEvent()
    object ButtonViewMarksClicked: HomeScreenEvent()
    object MarksDialogDismissed: HomeScreenEvent()
    data class MarksClicked(val registration: MarksRegistration): HomeScreenEvent()
}
