package com.jainhardik120.jiitcompanion.presentation.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.presentation.login.LoginViewModel

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    userInfo : UserEntity,
    viewModel: HomeViewModel = hiltViewModel(),
    token: String? = null
) {
    Log.d(TAG, "HomeScreen: $userInfo")
    Column(Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center) {
        ProfileCard(userEntity = userInfo)
    }
}

@Preview
@Composable
fun PreviewCard(){
    Column(Modifier.fillMaxSize(),
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

@Composable
fun ProfileCard(userEntity: UserEntity){
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
            Text(text = userEntity.name)
            Text(text = userEntity.enrollmentno)
            Text(text = userEntity.instituteLabel)
            Text(text = "${userEntity.programcode} ${userEntity.branch} ${userEntity.admissionyear} ${userEntity.batch}")
            Text(text = "${userEntity.userDOB} ${userEntity.gender}")
            Text(text = userEntity.studentcellno)
            Text(text = userEntity.studentpersonalemailid)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "View Attendance")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "View Registered Subject")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "View Result")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Grade Card")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Subject Marks")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Examination Schedule")
            }
        }
    }
}
