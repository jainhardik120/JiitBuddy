package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity

data class AttendanceScreenState(
    val registrations:List<StudentAttendanceRegistrationEntity> = emptyList(),
    val selectedSemesterCode: String = "",
    val selectedSemesterId: String = "",
    val attendanceData:List<StudentAttendanceEntity> = emptyList()
)