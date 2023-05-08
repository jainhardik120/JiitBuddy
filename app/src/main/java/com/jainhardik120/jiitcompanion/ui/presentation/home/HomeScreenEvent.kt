package com.jainhardik120.jiitcompanion.ui.presentation.home

import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration

sealed class HomeScreenEvent{
    object OnLogOutClicked:HomeScreenEvent()
    object OnLogOutConfirmed:HomeScreenEvent()
    object OfflineDialogConfirmed:HomeScreenEvent()
    object OfflineDialogDismissed:HomeScreenEvent()
    object OnLogOutDismissed:HomeScreenEvent()
    object OnOfflineAlertClicked:HomeScreenEvent()
    data class BottomNavItemClicked(val screen:BottomBarScreen):HomeScreenEvent()
    object ButtonViewMarksClicked: HomeScreenEvent()
    object MarksDialogDismissed: HomeScreenEvent()
    data class MarksClicked(val registration: MarksRegistration): HomeScreenEvent()
}
