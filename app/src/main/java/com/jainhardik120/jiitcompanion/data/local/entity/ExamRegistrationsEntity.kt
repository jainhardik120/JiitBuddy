package com.jainhardik120.jiitcompanion.data.local.entity

import androidx.room.Entity

@Entity(tableName = "exam_registrations_table", primaryKeys = ["studentId", "registrationid"])
data class ExamRegistrationsEntity(
    val studentId:String,
    val registrationcode: String,
    val registrationdesc: String,
    val registrationid: String
)