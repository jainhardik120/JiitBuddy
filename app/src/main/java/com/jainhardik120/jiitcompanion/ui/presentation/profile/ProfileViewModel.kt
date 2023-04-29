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
import com.jainhardik120.jiitcompanion.ui.presentation.home.HomeViewModel
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository
) : ViewModel(){

    var state by mutableStateOf(ProfileScreenState())

    fun initialize(){
        val user = savedStateHandle.get<String>("userInfo")?.let {
            Moshi.Builder().build().adapter(UserEntity::class.java).lenient()
                .fromJson(it)
        }
        if(user!=null){
            state = state.copy(user = user)
        }
        Log.d("TAG", "initialize: ${state.user}")
        loadFeed()
    }

    private fun loadFeed(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val result = feedRepository.getAllRows()
                    if(result.data!=null){
                        state = state.copy(feedItems = result.data)
                    }
                }catch (e :Exception){
                    Log.d("TAG", "Exception: ${e.message}")
                }
            }
        }
    }
}