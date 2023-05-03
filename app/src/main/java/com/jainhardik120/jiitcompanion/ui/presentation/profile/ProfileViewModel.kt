package com.jainhardik120.jiitcompanion.ui.presentation.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.domain.repository.FeedRepository
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
class ProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository
) : ViewModel() {

    var state by mutableStateOf(ProfileScreenState())

    companion object {
        private const val TAG = "ProfileScreenViewModel"
    }


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
    
    fun initialize() {
        val user = savedStateHandle.get<String>("userInfo")?.let {
            Moshi.Builder().build().adapter(UserEntity::class.java).lenient()
                .fromJson(it)
        }
        if (user != null) {
            state = state.copy(user = user)
        }
        loadFeed()
    }


    private fun loadFeed() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val result = feedRepository.getAllRows()
                    if (result.data != null) {
                        state = state.copy(feedItems = result.data)
                    }
                    updateCurrentIndex(0)
                    Log.d(TAG, "loadFeed: ${result.data.toString()}")
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: ${e.message}")
                }
            }
        }
    }

    private fun updateCurrentIndex(newIndex: Int) {
        if (newIndex < state.feedItems.size) {
            state = state.copy(
                currentItemIndex = newIndex,
                isNextAvailable = (newIndex != state.feedItems.size - 1),
                isPrevAvailable = (newIndex != 0)
            )
        } else {
            return
        }
    }

    fun onEvent(event: ProfileScreenEvent) {
        when (event) {
            is ProfileScreenEvent.NextButtonClicked -> {
                updateCurrentIndex(state.currentItemIndex+1)
            }
            is ProfileScreenEvent.OpenInBrowserClicked -> {
                Log.d(TAG, "onEvent: ${state.feedItems[state.currentItemIndex].webUrl}")
                sendUiEvent(UiEvent.OpenUrl(state.feedItems[state.currentItemIndex].webUrl))
            }
            is ProfileScreenEvent.PrevButtonClicked -> {
                updateCurrentIndex(state.currentItemIndex-1)
            }
        }
    }
}