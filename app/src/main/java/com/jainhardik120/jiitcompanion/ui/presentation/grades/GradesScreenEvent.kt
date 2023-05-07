package com.jainhardik120.jiitcompanion.ui.presentation.grades

import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration

sealed class GradesScreenEvent {
    object BottomSheetDismissed: GradesScreenEvent()
    data class ResultItemClicked(val stynumber: Int):GradesScreenEvent()
}