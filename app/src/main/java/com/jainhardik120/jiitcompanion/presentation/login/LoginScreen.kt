package com.jainhardik120.jiitcompanion.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.uitl.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.state
    LaunchedEffect(key1 = true){
        viewModel.uiEvent.collect{
            when(it){
                is UiEvent.Navigate->{
                    onNavigate(it)
                }
            }
        }
    }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center) {
        Card(Modifier.fillMaxWidth().padding(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
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
                OutlinedTextField(
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


}
