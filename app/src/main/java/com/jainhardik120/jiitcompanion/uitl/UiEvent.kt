package com.jainhardik120.jiitcompanion.uitl

import com.jainhardik120.jiitcompanion.presentation.destinations.DirectionDestination


sealed class UiEvent {
    data class Navigate(val destination: DirectionDestination) : UiEvent()
}