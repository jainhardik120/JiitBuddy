package com.jainhardik120.jiitcompanion.ui.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val snackbarHostState = remember {SnackbarHostState()}
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    onNavigate(it)
                }
                is UiEvent.ShowSnackbar ->{
                    snackbarHostState.showSnackbar(it.message)
                }
            }
        }
    }
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState)}) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.enrollmentNo,
                label = {
                    Text(text = "Enrollment No")
                },
                onValueChange = {
                    viewModel.onEvent(
                        LoginScreenEvent.OnEnrollmentNoChange(it)
                    )
                })
            TextField(
                modifier = Modifier.fillMaxWidth(),
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
