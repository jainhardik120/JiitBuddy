package com.jainhardik120.jiitcompanion.ui.presentation.subjects

import androidx.lifecycle.ViewModel
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    private val repository: PortalRepository
) : ViewModel()  {
}