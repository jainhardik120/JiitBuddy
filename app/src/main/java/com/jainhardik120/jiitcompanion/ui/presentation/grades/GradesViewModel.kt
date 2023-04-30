package com.jainhardik120.jiitcompanion.ui.presentation.grades

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.domain.model.MarksRegistration
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
class GradesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PortalRepository
) : ViewModel() {
    companion object{
        private const val TAG = "GradesViewModel"
    }

    var state by mutableStateOf(GradesState())
    private lateinit var token: String
    private lateinit var user: UserEntity

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun initialize() {
        viewModelScope.launch {
            token = savedStateHandle.get<String>("token") ?: return@launch
            user = Moshi.Builder().build().adapter(UserEntity::class.java).lenient()
                .fromJson(savedStateHandle.get<String>("userInfo") ?: return@launch)!!
            if(token=="offline"){
                state=state.copy(isOffline = true)
            }else{
                val result =
                    repository.getStudentResultData(user.instituteValue, user.memberid,  token)
                when (result) {
                    is Resource.Success -> {
                        state = result.data?.let { state.copy(results = it) }!!
                    }
                    is Resource.Error -> {

                    }
                }
            }
        }
    }

    private fun loadMarksRegistrations(){
        viewModelScope.launch(Dispatchers.IO){
            val result = repository.getMarksRegistration(user.instituteValue, user.memberid, token)
            when(result){
                is Resource.Success ->{
                    if(result.data!=null){
                        state= state.copy(marksRegistrations = result.data, isMarksRegistrationsLoaded = true)
                    }
                }
                is Resource.Error ->{

                }
            }
        }
    }
    
    private fun downloadAndOpenMarksPdf(registration : MarksRegistration){
        Log.d(TAG, "downloadAndOpenMarksPdf: ${registration.registrationcode}")
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getMarksPdf(user.memberid, user.instituteValue, registration.registrationid, registration.registrationcode, token)
            when(result){
                is Resource.Success->{
                    Log.d(TAG, "downloadAndOpenMarksPdf: ${result.data}")
                    if(result.data!=null){
                        sendUiEvent(UiEvent.OpenPdf(result.data))
                    }

                } 
                is Resource.Error->{
                    Log.d(TAG, "downloadAndOpenMarksPdf: ${result.message}")
                }
            }
        }

    }

    fun onEvent(event: GradesScreenEvent){
        when(event){
            is GradesScreenEvent.ButtonViewMarksClicked ->{
                state = state.copy(isMarksDialogOpened = true)
                if(!state.isMarksRegistrationsLoaded){
                    loadMarksRegistrations()
                }
            }
            is GradesScreenEvent.MarksClicked -> {
                state = state.copy(isMarksDialogOpened = false)
                downloadAndOpenMarksPdf(registration  = event.registration)
            }
            is GradesScreenEvent.MarksDialogDismissed -> {
                state = state.copy(isMarksDialogOpened = false)
            }
        }
    }
}