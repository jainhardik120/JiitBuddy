package com.jainhardik120.jiitcompanion.ui.presentation.home

import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration

data class HomeState(
    val logOutDialogOpened:Boolean = false,
    val offlineDialogOpened:Boolean = false,
    val isMarksDialogOpened: Boolean = false,
    val isMarksRegistrationsLoaded: Boolean = false,
    val marksRegistrations:List<MarksRegistration> = emptyList(),
)