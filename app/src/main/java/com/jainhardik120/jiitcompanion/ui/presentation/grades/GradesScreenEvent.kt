package com.jainhardik120.jiitcompanion.ui.presentation.grades

import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration

sealed class GradesScreenEvent {
    object ButtonViewMarksClicked: GradesScreenEvent()
    object MarksDialogDismissed: GradesScreenEvent()
    object BottomSheetDismissed: GradesScreenEvent()
    data class MarksClicked(val registration: MarksRegistration):GradesScreenEvent()
    data class ResultItemClicked(val stynumber: Int):GradesScreenEvent()
}