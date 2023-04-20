package com.jainhardik120.jiitcompanion.uitl

sealed class UiEvent {
    data class Navigate(val route: String) : UiEvent()
}