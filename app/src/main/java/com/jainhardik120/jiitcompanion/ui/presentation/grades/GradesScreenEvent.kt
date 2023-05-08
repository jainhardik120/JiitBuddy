package com.jainhardik120.jiitcompanion.ui.presentation.grades

sealed class GradesScreenEvent {
    object BottomSheetDismissed: GradesScreenEvent()
    data class ResultItemClicked(val stynumber: Int):GradesScreenEvent()
}