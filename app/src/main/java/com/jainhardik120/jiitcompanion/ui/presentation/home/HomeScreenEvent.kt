package com.jainhardik120.jiitcompanion.ui.presentation.home

sealed class HomeScreenEvent{
    object onLogOutClicked:HomeScreenEvent()
    object onLogOutConfirmed:HomeScreenEvent()
    object onLogOutDismissed:HomeScreenEvent()
    object onOfflineAlertClicked:HomeScreenEvent()
    data class bottomNavItemClicked(val screen:BottomBarScreen):HomeScreenEvent()
}
