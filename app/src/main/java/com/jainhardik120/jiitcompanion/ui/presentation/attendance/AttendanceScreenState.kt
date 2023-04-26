package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.domain.model.AttendanceItem

data class AttendanceScreenState(
    val registrations:List<StudentAttendanceRegistrationEntity> = emptyList(),
    val selectedSemesterCode: String = "",
    val selectedSemesterId: String = "",
    val attendanceData:List<AttendanceItem> = emptyList(),
    val attendanceWarning :Int = 80,
    val isDetailDataReady: Boolean = false,
    val isBottomSheetExpanded: Boolean = false
)