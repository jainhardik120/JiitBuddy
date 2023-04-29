package com.jainhardik120.jiitcompanion.ui.presentation.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.data.remote.FeedApi
import com.jainhardik120.jiitcompanion.domain.repository.FeedRepository
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.ui.presentation.root.Screen
import com.jainhardik120.jiitcompanion.util.UiEvent
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PortalRepository,
    private val feedRepository: FeedRepository
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
            loadFeed()
        }
    }

    private fun loadFeed(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val result = feedRepository.getAllRows()
                    Log.d(TAG, "feedApi : ${result.data}")
                }catch (e :Exception){
                    Log.d(TAG, "Exception: ${e.message}")
                }
            }
        }
    }

    fun onEvent(event: HomeScreenEvent){
        when(event){
            is HomeScreenEvent.onLogOutClicked->{
                state = state.copy(logOutDialogOpened = true)
            }
            HomeScreenEvent.onLogOutConfirmed -> {
                viewModelScope.launch {
                    repository.logOut(user.memberid)
                    sendUiEvent(UiEvent.Navigate(Screen.LoginScreen.route))
                }
            }

            HomeScreenEvent.onLogOutDismissed -> {
                state= state.copy(logOutDialogOpened = false)
            }
        }
    }
}