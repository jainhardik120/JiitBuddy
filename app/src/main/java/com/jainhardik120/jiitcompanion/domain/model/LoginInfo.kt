package com.jainhardik120.jiitcompanion.domain.model

data class LoginInfo(
    val clientid: String,
    val enrollmentno: String,
    val memberid: String,
    val membertype: String,
    val instituteLabel: String,
    val instituteValue: String,
    val name: String,
    val admissionyear: String = "",
    val batch: String = "",
    val branch: String = "",
    val institutecode: String = "",
    val programcode: String = ""
)
