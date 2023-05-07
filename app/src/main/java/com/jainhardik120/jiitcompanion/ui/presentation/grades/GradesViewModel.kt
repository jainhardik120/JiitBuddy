package com.jainhardik120.jiitcompanion.ui.presentation.grades

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration
import com.jainhardik120.jiitcompanion.domain.model.LoginInfo
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
    private lateinit var user: LoginInfo

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
            user = Moshi.Builder().build().adapter(LoginInfo::class.java).lenient()
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
                        result.message?.let { UiEvent.ShowSnackbar(it) }?.let { sendUiEvent(it) }
                    }
                }
            }
        }
    }


    private fun loadResultDetails(stynumber:Int){
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repository.getResultDetail(user.memberid, user.instituteValue, stynumber, token)){
                is Resource.Success->{
                    Log.d(TAG, "loadResultDetails: ${result.data.toString()}")
                    if(result.data!=null){
                        state = state.copy(detailData = result.data, isDetailDataReady = true)
                    }
                }
                is Resource.Error->{
                    result.message?.let { UiEvent.ShowSnackbar(it) }?.let { sendUiEvent(it) }
                }
            }
        }
    }


    fun onEvent(event: GradesScreenEvent){
        when(event){
            is GradesScreenEvent.ResultItemClicked -> {
                state = state.copy(isBottomSheetExpanded = true, isDetailDataReady = false)
                loadResultDetails(event.stynumber)
            }
            is GradesScreenEvent.BottomSheetDismissed -> {
                state = state.copy(isBottomSheetExpanded = false)
            }

        }
    }
}