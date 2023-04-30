package com.jainhardik120.jiitcompanion.ui.presentation.exams

import com.jainhardik120.jiitcompanion.data.local.entity.ExamEventsEntity
import com.jainhardik120.jiitcompanion.data.local.entity.ExamRegistrationsEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity

data class ExamScreenState(
    val registrations:List<ExamRegistrationsEntity> = emptyList(),
    val selectedSemesterCode: String = "",
    val selectedSemesterId: String = "",
    val events:List<ExamEventsEntity> = emptyList(),
    val selectedEventDesc: String = "",
    val selectedExamId: String = "",
)
