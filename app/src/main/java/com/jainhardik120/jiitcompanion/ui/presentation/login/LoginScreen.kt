package com.jainhardik120.jiitcompanion.ui.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.ui.components.icons.BugReport
import com.jainhardik120.jiitcompanion.ui.components.icons.Visibility
import com.jainhardik120.jiitcompanion.ui.components.icons.VisibilityOff
import com.jainhardik120.jiitcompanion.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uriHandler = LocalUriHandler.current
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
        }, actions = {
            IconButton(onClick = {
                uriHandler.openUri("https://docs.google.com/forms/d/e/1FAIpQLScpK0CC5PlwUe2pwY0AUAKBs5KS78_nK8xvJ-S8pv6u2V7yxg/viewform?usp=sf_link")
            }) {
                Icon(Icons.Filled.BugReport, contentDescription = "Report an issue")
            }
        })
    },snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize(), verticalArrangement = Arrangement.Center) {
            LoginCard(state = state, onEvent = {event->
                viewModel.onEvent(event)
            })
        }
    }
    if(state.isLoading){
        AlertDialog(onDismissRequest = {

        }, properties = DialogProperties(
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