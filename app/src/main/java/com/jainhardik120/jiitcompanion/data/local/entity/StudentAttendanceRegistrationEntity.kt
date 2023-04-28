package com.jainhardik120.jiitcompanion.data.local.entity

import androidx.room.Entity

@Entity(tableName = "attendance_registration_table", primaryKeys = ["studentid", "registrationid"])
data class StudentAttendanceRegistrationEntity(
    val studentid : String,
    val registrationcode: String,
    val registrationid: String
)