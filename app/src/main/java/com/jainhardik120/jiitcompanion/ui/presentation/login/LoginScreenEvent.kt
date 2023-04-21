package com.jainhardik120.jiitcompanion.ui.presentation.login

sealed class LoginScreenEvent{
    data class OnEnrollmentNoChange(val enrollmentno : String): LoginScreenEvent()
    data class OnPasswordChange(val password : String): LoginScreenEvent()
    object OnLoginClicked: LoginScreenEvent()
}
