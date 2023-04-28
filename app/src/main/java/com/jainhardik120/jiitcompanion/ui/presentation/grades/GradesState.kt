package com.jainhardik120.jiitcompanion.ui.presentation.grades

import com.jainhardik120.jiitcompanion.data.local.entity.ResultEntity
import com.jainhardik120.jiitcompanion.domain.model.MarksRegistration

data class GradesState(
    val results:List<ResultEntity> = emptyList(),
    val isMarksDialogOpened: Boolean = false,
    val isMarksRegistrationsLoaded: Boolean = false,
    val marksRegistrations:List<MarksRegistration> = emptyList()
)