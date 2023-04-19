package com.jainhardik120.jiitcompanion.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.core.util.Resource
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: PortalRepository
) : ViewModel(){
    fun login(enrollment:String, password: String){
        viewModelScope.launch {
            repository.loginUser(enrollment, password).collect{
                result->
                when(result){
                    is Resource.Success ->{

                    }
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {

                    }
                }
            }
        }
    }
}