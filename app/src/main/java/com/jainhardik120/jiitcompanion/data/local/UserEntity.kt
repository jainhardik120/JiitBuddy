package com.jainhardik120.jiitcompanion.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    val password: String,
    val clientid: String,
    val enrollmentno: String,
    @PrimaryKey val memberid: String,
    val membertype: String,
    val instituteLabel: String,
    val instituteValue: String,
    val name: String,
    val userDOB: String,
    val userid: String,
    val admissionyear: String,
    val batch: String,
    val branch: String,
    val gender: String,
    val institutecode: String,
    val programcode: String,
    val semester: Int,
    val studentcellno: String,
    val studentpersonalemailid: String,
    val lastAttendanceRegistrationId : String? = null
)

