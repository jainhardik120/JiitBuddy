package com.jainhardik120.jiitcompanion.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PortalRepository
) : ViewModel() {
    private val TAG = "HomeViewModel"
    init {
        Log.d(TAG, "HomeViewModel: Initialized")
    }
}