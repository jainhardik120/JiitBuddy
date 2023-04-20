package com.jainhardik120.jiitcompanion.presentation.login

import androidx.lifecycle.MutableLiveData

data class LoginState(
    val enrollmentNo: String = "",
    val password:String = "",
    val loggedIn:MutableLiveData<Boolean> = MutableLiveData(false)
)