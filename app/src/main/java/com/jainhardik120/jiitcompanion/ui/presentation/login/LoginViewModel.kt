package com.jainhardik120.jiitcompanion.ui.presentation.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.domain.model.LoginInfo
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.ui.presentation.root.Screen
import com.jainhardik120.jiitcompanion.util.Resource
import com.jainhardik120.jiitcompanion.util.UiEvent
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: PortalRepository
) : ViewModel() {

    var state by mutableStateOf(LoginState())

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    companion object {
        private const val TAG = "LoginViewModel"
    }

    fun initialize() {
        Log.d(TAG, "LoginViewModel: Initialized")
        val lastUser = repository.lastUser()
        if (!lastUser.data?.first.equals("null")) {
            lastUser.data?.first?.let { lastUser.data.second.let { it1 -> login(it, it1) } }
        }
    }

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.OnEnrollmentNoChange -> {
                state = state.copy(enrollmentNo = event.enrollmentno)
            }

            is LoginScreenEvent.OnPasswordChange -> {
                state = state.copy(password = event.password)
            }

            is LoginScreenEvent.OnLoginClicked -> {
                Log.d(TAG, "onEvent: Came Here")
                if (state.enrollmentNo.isNotEmpty() && state.password.isNotEmpty()) {
                    login(state.enrollmentNo, state.password)
                } else {
                    sendUiEvent(UiEvent.ShowSnackbar("Enter Enrollment No and Password"))
                }

            }
        }
    }

    fun login(enrollment: String, password: String) {
        state = state.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.loginUser(enrollment, password)
            state = state.copy(isLoading = false)
            when (result) {
                is Resource.Success -> {
                    if (result.data != null) {
                        val json =
                            Moshi.Builder().build().adapter(LoginInfo::class.java).lenient().toJson(
                                toLoginInfo(result.data.first)
                            )
                        sendUiEvent(
                            UiEvent.Navigate(
                                Screen.HomeScreen.withArgs(
                                    json,
                                    result.data.second
                                )
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    result.message?.let { UiEvent.ShowSnackbar(it) }?.let { sendUiEvent(it) }
                }
            }
        }
    }

    private fun toLoginInfo(user: UserEntity): LoginInfo {
        return LoginInfo(
            clientid = user.clientid,
            enrollmentno = user.enrollmentno,
            memberid = user.memberid,
            membertype = user.membertype,
            instituteLabel = user.instituteLabel,
            instituteValue = user.instituteValue,
            name = user.name,
            admissionyear = user.admissionyear,
            batch = user.batch,
            branch = user.branch,
            institutecode = user.institutecode,
            programcode = user.programcode,
        )
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}