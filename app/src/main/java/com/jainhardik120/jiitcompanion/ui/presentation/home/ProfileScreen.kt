package com.jainhardik120.jiitcompanion.ui.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity

@Composable
fun ProfileScreen(userEntity: UserEntity){
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
        }
    }
}