package com.jainhardik120.jiitcompanion.ui.presentation.login

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.util.Resource
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.util.Screen
import com.jainhardik120.jiitcompanion.util.UiEvent
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: PortalRepository
) : ViewModel(){

    var state by mutableStateOf(LoginState())

    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val TAG = "LoginViewModel"

    init {
        Log.d(TAG, "LoginViewModel: Initialized")
        val lastUser = repository.lastUser()
        if(!lastUser.data?.first.equals("null")){
            lastUser.data?.first?.let { lastUser.data.second.let { it1 -> login(it, it1) } }
        }
    }
    fun onEvent(event: LoginScreenEvent){
        when(event){
            is LoginScreenEvent.OnEnrollmentNoChange ->{
                state = state.copy(enrollmentNo = event.enrollmentno)
            }
            is LoginScreenEvent.OnPasswordChange ->{
                state = state.copy(password = event.password)
            }
            is LoginScreenEvent.OnLoginClicked ->{
                Log.d(TAG, "onEvent: Came Here")
                if(state.enrollmentNo.isNotEmpty() && state.password.isNotEmpty()){
                    login(state.enrollmentNo, state.password)
                } else {
                    sendUiEvent(UiEvent.ShowSnackbar("Enter Enrollment No and Password"))
                }

            }
        }
    }

    fun login(enrollment:String, password: String){
        viewModelScope.launch {
            repository.loginUser(enrollment, password).collect{
                result->
                when(result){
                    is Resource.Success ->{
                        val json = Moshi.Builder().build().adapter(UserEntity::class.java).lenient().toJson(
                            result.data?.first
                        )
                        result.data?.let { Screen.HomeScreen.withArgs(json, it.second) }
                            ?.let { UiEvent.Navigate(it) }?.let { sendUiEvent(it) }
                    }
                    is Resource.Error -> {
                        result.message?.let { UiEvent.ShowSnackbar(it) }?.let { sendUiEvent(it) }
                    }
                    is Resource.Loading -> {

                    }
                }
            }
        }
    }
    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}