package com.jainhardik120.jiitcompanion.ui.presentation.exams

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExamsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PortalRepository
) : ViewModel(){

    private val TAG = "ExamsViewModel"
    init {
        viewModelScope.launch {
            val token = savedStateHandle.get<String>("token")?:return@launch

        }
    }
}