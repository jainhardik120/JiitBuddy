package com.jainhardik120.jiitcompanion.ui.presentation.exams

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
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
class ExamsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PortalRepository
) : ViewModel(){
    companion object{
        private const val TAG = "ExamsViewModel"
    }

    var state by mutableStateOf(ExamScreenState())

    private lateinit var token: String
    private lateinit var user: UserEntity

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun getRegistrations(){
        viewModelScope.launch(Dispatchers.IO) {
            token = savedStateHandle.get<String>("token") ?: return@launch
            user = Moshi.Builder().build().adapter(UserEntity::class.java).lenient()
                .fromJson(savedStateHandle.get<String>("userInfo") ?: return@launch)!!
            val result =
                repository.getExamRegistrations(user.clientid,user.instituteValue, user.memberid, token)
            when (result) {
                is Resource.Success -> {
                    if(result.data!=null){
                        state = state.copy(registrations = result.data)
                    }
                }
                is Resource.Error -> {
                    result.message?.let { UiEvent.ShowSnackbar(it) }?.let { sendUiEvent(it) }
                }
            }
        }
    }

    private fun loadExamEvents(){
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.getExamEvents(user.memberid, user.instituteValue, state.selectedSemesterId, token)) {
                is Resource.Success -> {
                    if(result.data!=null){
                        state = state.copy(events = result.data)
                    }
                }
                is Resource.Error -> {
                    result.message?.let { UiEvent.ShowSnackbar(it) }?.let { sendUiEvent(it) }
                }
            }
        }
    }
    
    private fun loadExamSchedule(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getExamSchedules(state.selectedExamId, state.selectedSemesterId, user.instituteValue, user.memberid, token)
            when (result) {
                is Resource.Success -> {
                    Log.d(TAG, "loadExamSchedule: ${result.data}")
                }
                is Resource.Error -> {
                    result.message?.let { UiEvent.ShowSnackbar(it) }?.let { sendUiEvent(it) }
                }
            }
        }
    }


    fun onEvent(event: ExamScreenEvent){
        when(event){
            is ExamScreenEvent.OnSemesterChanged->{
                state = state.copy(
                    selectedSemesterCode = event.semester.registrationcode,
                    selectedSemesterId = event.semester.registrationid,
                    selectedExamId = "",
                    selectedEventDesc = ""
                )
                loadExamEvents()
            }

            is ExamScreenEvent.OnExamEventChanged -> {
                state = state.copy(
                    selectedEventDesc = event.event.exameventdesc,
                    selectedExamId = event.event.exameventid,
                )
                loadExamSchedule()
            }
        }
    }
}