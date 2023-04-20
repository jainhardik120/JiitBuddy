package com.jainhardik120.jiitcompanion.presentation.login

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.core.util.Resource
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.uitl.Routes
import com.jainhardik120.jiitcompanion.uitl.UiEvent
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

    fun onEvent(event: LoginScreenEvent){
        when(event){
            is LoginScreenEvent.OnEnrollmentNoChange->{
                state = state.copy(enrollmentNo = event.enrollmentno)
            }
            is LoginScreenEvent.OnPasswordChange->{
                state = state.copy(password = event.password)
            }
            is LoginScreenEvent.OnLoginClicked->{
                Log.d(TAG, "onEvent: Came Here")
                login(state.enrollmentNo, state.password)
            }
        }
    }

    fun login(enrollment:String, password: String){
        viewModelScope.launch {
            repository.loginUser(enrollment, password).collect{
                result->
                when(result){
                    is Resource.Success ->{
                        Log.d(TAG, "login: Success Resource")
                        sendUiEvent(UiEvent.Navigate(Routes.HOME_SCREEN))
                    }
                    is Resource.Error -> {

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