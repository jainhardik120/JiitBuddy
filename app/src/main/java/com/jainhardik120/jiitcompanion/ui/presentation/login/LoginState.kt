package com.jainhardik120.jiitcompanion.ui.presentation.login

data class LoginState(
    val enrollmentNo: String = "",
    val password:String = "",
    val isLoading:Boolean = false,
    val isCaptchaDialogOpened : Boolean = false,
    val captchaImageBase64 : String = "",
    val captchaText : String = "",
    val hiddenVal  :String = ""
)