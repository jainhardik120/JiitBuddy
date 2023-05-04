package com.jainhardik120.jiitcompanion.ui.presentation.subjects

import com.jainhardik120.jiitcompanion.data.remote.model.SubjectSemesterRegistrations
import com.jainhardik120.jiitcompanion.domain.model.SubjectItem

data class SubjectsState(
    val isOffline: Boolean = false,
    val registrations:List<SubjectSemesterRegistrations> = emptyList(),
    val selectedSemesterCode: String = "",
    val selectedSemesterId: String = "",
    val subjects:List<SubjectItem> = emptyList()
)