package com.jainhardik120.jiitcompanion.ui.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true) {
        viewModel.initialize()
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    onNavigate(it)
                }

                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(it.message)
                }

                else -> {}
            }
        }
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "JIIT Buddy")
        })
    },snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize(), verticalArrangement = Arrangement.Center) {
            LoginCard(state = state, onEvent = {
                viewModel.onEvent(it)
            })
        }
    }
    if(state.isLoading){
        AlertDialog(onDismissRequest = { /*TODO*/ }, properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Logging in...")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginCardPreview() {
    LoginCard(
        state = LoginState(enrollmentNo = "21103185", password = "E1CAEE"),
        onEvent = {

        })
}

@Composable
fun LoginCard(state: LoginState, onEvent: (LoginScreenEvent) -> Unit) {
    val showPassword = rememberSaveable { mutableStateOf(false) }
    Column(Modifier.padding(horizontal = 16.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.enrollmentNo,
            label = {
                Text(text = "Enrollment No", style = MaterialTheme.typography.bodyMedium)
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = {

                }
            ),singleLine = true,
            onValueChange = {
                onEvent(
                    LoginScreenEvent.OnEnrollmentNoChange(it)
                )
            })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.password,
            label = {
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            onValueChange = {
                onEvent(
                    LoginScreenEvent.OnPasswordChange(it)
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            trailingIcon = {
                if (showPassword.value) {
                    IconButton(onClick = { showPassword.value = false }) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "Hide Password"
                        )
                    }
                } else {
                    IconButton(onClick = { showPassword.value = true }) {
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = "Show Password"
                        )
                    }
                }
            }, visualTransformation = if (showPassword.value) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onEvent(
                        LoginScreenEvent.OnLoginClicked
                    )
                }
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onEvent(
                LoginScreenEvent.OnLoginClicked
            )
        }, Modifier.align(Alignment.End)) {
            Text(text = "Log in", modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}