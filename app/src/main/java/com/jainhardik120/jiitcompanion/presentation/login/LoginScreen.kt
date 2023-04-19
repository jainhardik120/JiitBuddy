package com.jainhardik120.jiitcompanion.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.presentation.home.ProfileCard
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(start = true)
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel()
) {
//    Button(onClick = {
////        viewModel.login("21103185", "E1CAEE")
//        navigator.navigate(HomeScreenDestination())
//    }) {
//        Text(text = "Login")
//    }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center) {
        ProfileCard(userEntity = UserEntity(
            "E1CAEE",
            "JAYPEE",
            "21103185",
            "JIAC2202375",
            "S",
            "JAYPEE INSTITUTE OF INFORMATION TECHNOLOGY",
            "11IN1902J000001",
            "HARDIK JAIN",
            "2003-10-17",
            "USID2109A0000458",
            "2122",
            "B7",
            "CSE",
            "M",
            "JIIT",
            "B.T",
            4,
            "7983121194",
            "JAINHARDIK120@GMAIL.COM"
        )
        )
    }
}
