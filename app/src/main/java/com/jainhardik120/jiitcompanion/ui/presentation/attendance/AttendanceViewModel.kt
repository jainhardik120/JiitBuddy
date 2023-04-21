package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.ui.presentation.grades.GradesState
import com.jainhardik120.jiitcompanion.util.Resource
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PortalRepository
) : ViewModel() {
    private val TAG = "AttendanceViewModel"
    var state by mutableStateOf(AttendanceScreenState())
    private lateinit var token: String
    private lateinit var user: UserEntity

    init {
        viewModelScope.launch {
            token = savedStateHandle.get<String>("token") ?: return@launch
            user = Moshi.Builder().build().adapter(UserEntity::class.java).lenient()
                .fromJson(savedStateHandle.get<String>("userInfo") ?: return@launch)!!
            repository.getAttendanceRegistrationDetails(
                user.clientid,
                user.instituteValue,
                user.memberid,
                user.membertype,
                token
            )
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            state = result.data?.let { state.copy(registrations = it) }!!
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Error -> {

                        }
                    }
                }
        }
    }

    fun onEvent(event: AttendanceScreenEvent) {
        when (event) {
            is AttendanceScreenEvent.OnSemesterChanged -> {
                state = state.copy(
                    selectedSemesterCode = event.semester.registrationcode,
                    selectedSemesterId = event.semester.registrationid
                )
                loadAttendanceDetails()
            }
        }
    }

    private fun loadAttendanceDetails() {
        viewModelScope.launch {
            repository.getAttendanceDetails(
                user.clientid,
                user.instituteValue,
                user.memberid,
                1,
                state.selectedSemesterId,
                token
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        state = result.data?.let { state.copy(attendanceData = it) }!!
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {

                    }
                }
            }
        }
    }
}