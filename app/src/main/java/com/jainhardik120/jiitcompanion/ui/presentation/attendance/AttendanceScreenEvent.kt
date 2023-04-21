package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity


sealed class AttendanceScreenEvent{
    data class OnSemesterChanged(val semester : StudentAttendanceRegistrationEntity): AttendanceScreenEvent()
}