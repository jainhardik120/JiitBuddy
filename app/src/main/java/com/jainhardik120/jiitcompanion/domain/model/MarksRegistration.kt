package com.jainhardik120.jiitcompanion.domain.model

data class MarksRegistration(
    val registrationcode: String,
    val registrationdatefrom: Long,
    val registrationdateto: Long,
    val registrationdesc: String,
    val registrationid: String
)