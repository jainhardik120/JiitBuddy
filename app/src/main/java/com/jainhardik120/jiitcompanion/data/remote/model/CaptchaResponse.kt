package com.jainhardik120.jiitcompanion.data.remote.model

data class CaptchaResponse(
    val hidden : String = "",
    val image : String = ""
)

data class CaptchaValidationResponse(
    val random : String = "",
    val otppwd : String = "",
    val rejectedData : String = "",
    val username : String = ""
)
