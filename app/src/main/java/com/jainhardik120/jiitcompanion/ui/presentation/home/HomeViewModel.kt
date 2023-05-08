package com.jainhardik120.jiitcompanion.ui.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration
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
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PortalRepository
):ViewModel(){

    private lateinit var token: String
    private lateinit var user: LoginInfo

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var state by mutableStateOf(HomeState())

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    init {
        viewModelScope.launch {
            token = savedStateHandle.get<String>("token") ?: return@launch
            user = Moshi.Builder().build().adapter(LoginInfo::class.java).lenient()
                .fromJson(savedStateHandle.get<String>("userInfo") ?: return@launch)!!
        }
    }

    private fun loadMarksRegistrations(){
        viewModelScope.launch(Dispatchers.IO){
            when(val result = repository.getMarksRegistration(user.instituteValue, user.memberid, token)){
                is Resource.Success ->{
                    if(result.data!=null){
                        state= state.copy(marksRegistrations = result.data, isMarksRegistrationsLoaded = true)
                    }
                }
                is Resource.Error ->{
                    sendUiEvent(UiEvent.ShowSnackbar(message = result.message?:"Unknown Error Occurred"))
                }
            }
        }
    }

    private fun downloadAndOpenMarksPdf(registration : MarksRegistration){
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repository.getMarksPdf(user.memberid, user.instituteValue, registration.registrationid, registration.registrationcode, token)){
                is Resource.Success->{
                    if(result.data!=null){
                        sendUiEvent(UiEvent.OpenPdf(result.data))
                    }
                }
                is Resource.Error->{
                    sendUiEvent(UiEvent.ShowSnackbar(message = result.message?:"Unknown Error Occurred"))
                }
            }
        }
    }


    fun onEvent(event: HomeScreenEvent){
        when(event){
            is HomeScreenEvent.ButtonViewMarksClicked ->{
                state = state.copy(isMarksDialogOpened = true)
                if(!state.isMarksRegistrationsLoaded){
                    loadMarksRegistrations()
                }
            }
            is HomeScreenEvent.MarksClicked -> {
                state = state.copy(isMarksDialogOpened = false)
                downloadAndOpenMarksPdf(registration  = event.registration)
            }
            is HomeScreenEvent.MarksDialogDismissed -> {
                state = state.copy(isMarksDialogOpened = false)
            }
            is HomeScreenEvent.OnLogOutClicked->{
                state = state.copy(logOutDialogOpened = true)
            }
            is HomeScreenEvent.OnLogOutConfirmed -> {
                viewModelScope.launch {
                    state = state.copy(logOutDialogOpened = false)
                    repository.logOut(user.memberid)
                    sendUiEvent(UiEvent.Navigate(Screen.LoginScreen.route))
                }
            }
            is HomeScreenEvent.OnLogOutDismissed -> {
                state= state.copy(logOutDialogOpened = false)
            }
            is HomeScreenEvent.BottomNavItemClicked -> {
                sendUiEvent(UiEvent.Navigate("${event.screen.route}/${savedStateHandle.get<String>("userInfo")}/${savedStateHandle.get<String>("token")}"))
            }
            is HomeScreenEvent.OnOfflineAlertClicked -> {
                state = state.copy(offlineDialogOpened = true)
            }
            HomeScreenEvent.OfflineDialogConfirmed -> {
                viewModelScope.launch {
                    state = state.copy(offlineDialogOpened = false)
                    sendUiEvent(UiEvent.Navigate(Screen.LoginScreen.route))
                }
            }
            HomeScreenEvent.OfflineDialogDismissed -> {
                state = state.copy(offlineDialogOpened = false)
            }
        }
    }
}