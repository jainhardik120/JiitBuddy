package com.jainhardik120.jiitcompanion.util

sealed class UiEvent {
    data class Navigate(val route: String) : UiEvent()
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ): UiEvent()
    data class OpenPdf(
        val path:String
    ):UiEvent()
    data class OpenUrl(val url:String)
        :UiEvent()
}