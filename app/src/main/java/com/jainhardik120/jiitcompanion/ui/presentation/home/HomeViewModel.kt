package com.jainhardik120.jiitcompanion.ui.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.ui.presentation.root.Screen
import com.jainhardik120.jiitcompanion.util.UiEvent
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PortalRepository
):ViewModel(){

    companion object{
        private const val TAG = "HomeViewModel"
    }

    private lateinit var token: String
    private lateinit var user: UserEntity

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
            user = Moshi.Builder().build().adapter(UserEntity::class.java).lenient()
                .fromJson(savedStateHandle.get<String>("userInfo") ?: return@launch)!!
        }
    }


    fun onEvent(event: HomeScreenEvent){
        when(event){
            is HomeScreenEvent.onLogOutClicked->{
                state = state.copy(logOutDialogOpened = true)
            }
            is HomeScreenEvent.onLogOutConfirmed -> {
                viewModelScope.launch {
                    state = state.copy(logOutDialogOpened = false)
                    repository.logOut(user.memberid)
                    sendUiEvent(UiEvent.Navigate(Screen.LoginScreen.route))
                }
            }
            is HomeScreenEvent.onLogOutDismissed -> {
                state= state.copy(logOutDialogOpened = false)
            }
            is HomeScreenEvent.bottomNavItemClicked -> {
                sendUiEvent(UiEvent.Navigate("${event.screen.route}/${savedStateHandle.get<String>("userInfo")}/${savedStateHandle.get<String>("token")}"))
            }
            is HomeScreenEvent.onOfflineAlertClicked -> {
                state = state.copy(offlineDialogOpened = true)
            }
            HomeScreenEvent.offlineDialogConfirmed -> {
                viewModelScope.launch {
                    state = state.copy(offlineDialogOpened = false)
                    sendUiEvent(UiEvent.Navigate(Screen.LoginScreen.route))
                }
            }
            HomeScreenEvent.offlineDialogDismisse -> {
                state = state.copy(offlineDialogOpened = false)
            }
        }
    }
}