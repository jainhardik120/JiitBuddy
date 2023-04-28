package com.jainhardik120.jiitcompanion.ui.presentation.grades

import com.jainhardik120.jiitcompanion.domain.model.MarksRegistration

sealed class GradesScreenEvent {
    object ButtonViewMarksClicked: GradesScreenEvent()
    object MarksDialogDismissed: GradesScreenEvent()
    data class MarksClicked(val registration: MarksRegistration):GradesScreenEvent()
}