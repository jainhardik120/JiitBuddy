package com.jainhardik120.jiitcompanion.presentation.attendance

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: PortalRepository
) : ViewModel() {
    private val TAG = "AttendanceViewModel"

    init {
        Log.d(TAG, "AttendanceViewModel: Initialized")
    }
}