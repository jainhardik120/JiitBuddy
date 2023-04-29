package com.jainhardik120.jiitcompanion.ui.presentation.profile

sealed class ProfileScreenEvent {
    object NextButtonClicked:ProfileScreenEvent()
    object PrevButtonClicked:ProfileScreenEvent()
    object OpenInBrowserClicked:ProfileScreenEvent()
}