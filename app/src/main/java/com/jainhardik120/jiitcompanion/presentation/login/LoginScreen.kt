package com.jainhardik120.jiitcompanion.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.presentation.destinations.HomeScreenDestination
import com.jainhardik120.jiitcompanion.presentation.home.HomeScreen
import com.jainhardik120.jiitcompanion.presentation.home.ProfileCard
import com.jainhardik120.jiitcompanion.uitl.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(start = true)
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.state
    LaunchedEffect(key1 = true){
        viewModel.uiEvent.collect{
            when(it){
                is UiEvent.Navigate->{
                    navigator.navigate(it.destination)
                }
            }
        }
    }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center) {
        Card(Modifier) {
            Column() {
                OutlinedTextField(
                    value = state.enrollmentNo,
                    label = {
                            Text(text = "Enrollment No")
                    },
                    onValueChange = {
                    viewModel.onEvent(
                        LoginScreenEvent.OnEnrollmentNoChange(it)
                    )
                })
                OutlinedTextField(
                    value = state.password,
                    label = {
                        Text(text = "Password")
                    },
                    onValueChange = {
                        viewModel.onEvent(
                            LoginScreenEvent.OnPasswordChange(it)
                        )
                    })
                Button(onClick = {
                    viewModel.onEvent(
                        LoginScreenEvent.OnLoginClicked
                    )

                }) {
                    Text(text = "Login")
                }
            }
        }
    }


}
