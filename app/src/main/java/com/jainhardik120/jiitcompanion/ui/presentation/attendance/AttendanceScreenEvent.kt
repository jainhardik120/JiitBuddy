package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.domain.model.AttendanceItem


sealed class AttendanceScreenEvent{
    data class OnSemesterChanged(val semester : StudentAttendanceRegistrationEntity): AttendanceScreenEvent()
    data class OnAttendanceItemClicked(val attendanceItem : AttendanceItem): AttendanceScreenEvent()
}