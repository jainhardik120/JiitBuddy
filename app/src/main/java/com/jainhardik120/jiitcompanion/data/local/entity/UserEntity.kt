package com.jainhardik120.jiitcompanion.data.local.entity

import androidx.room.Entity

@Entity(tableName = "user_table", primaryKeys = ["username", "password"])
data class UserEntity(
    val password: String,
    val username: String,
    val clientid: String,
    val enrollmentno: String,
    val memberid: String,
    val membertype: String,
    val instituteLabel: String,
    val instituteValue: String,
    val name: String,
    val userDOB: String,
    val userid: String,
    val admissionyear: String = "",
    val batch: String = "",
    val branch: String = "",
    val gender: String = "",
    val institutecode: String = "",
    val programcode: String = "",
    val semester: Int = 0,
    val studentcellno: String = "",
    val studentpersonalemailid: String = "",
    val lastAttendanceRegistrationId : String? = null
)
