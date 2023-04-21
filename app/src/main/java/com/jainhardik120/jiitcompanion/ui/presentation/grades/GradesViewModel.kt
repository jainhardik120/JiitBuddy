package com.jainhardik120.jiitcompanion.ui.presentation.grades

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.util.Resource
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GradesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PortalRepository
) : ViewModel() {
    private val TAG = "GradesViewModel"
    var state by mutableStateOf(GradesState())
    init {
        viewModelScope.launch {
            val token = savedStateHandle.get<String>("token") ?: return@launch
            val user = Moshi.Builder().build().adapter(UserEntity::class.java).lenient()
                .fromJson(savedStateHandle.get<String>("userInfo") ?: return@launch)
            if (user != null) {
                repository.getStudentResultData(user.instituteValue, user.memberid, 4, token)
                    .collect { result ->
                        when(result){
                            is Resource.Success->{
                                state = result.data?.let { state.copy(results = it) }!!
                            }
                            is Resource.Loading->{

                            }
                            is Resource.Error->{

                            }
                        }
                    }
            }
        }
    }
}