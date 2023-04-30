package com.jainhardik120.jiitcompanion.ui.presentation.exams

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.util.Resource
import com.jainhardik120.jiitcompanion.util.UiEvent
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private lateinit var token: String
    private lateinit var user: UserEntity

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    init {
        viewModelScope.launch {
            token = savedStateHandle.get<String>("token") ?: return@launch
            user = Moshi.Builder().build().adapter(UserEntity::class.java).lenient()
                .fromJson(savedStateHandle.get<String>("userInfo") ?: return@launch)!!
            val result =
                repository.getExamRegistrations(user.clientid,user.instituteValue, user.memberid, token)
            when (result) {
                is Resource.Success -> {
                    Log.d(TAG, "registrationsResult: ${result.data.toString()}")
                    val examEvents = result.data?.get(2)?.let { repository.getExamEvents(user.memberid,user.instituteValue, it.registrationid, token) }
                    when(examEvents){
                        is Resource.Success->{
                            Log.d(TAG, "examEvents: ${examEvents.data.toString()}")
                        }else->{

                        }
                    }
                }
                is Resource.Error -> {

                }
            }
        }
    }
}