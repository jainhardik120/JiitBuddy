package com.jainhardik120.jiitcompanion.ui.presentation.grades


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.ResultEntity

@Composable
fun GradesScreen(
    viewModel: GradesViewModel = hiltViewModel()
) {
    val state = viewModel.state
    LazyColumn {
        items(state.results.size) { it ->
            ResultItem(resultEntity = state.results[it])
        }
    }
}


@Composable
fun ResultItem(resultEntity: ResultEntity) {
    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = resultEntity.stynumber.toString(), fontSize = 24.sp)
            Column() {
                Text(text = "SGPA : ${resultEntity.sgpa}")
                Text(text = "CGPA : ${resultEntity.cgpa}")
            }
        }
    }
}

@Preview
@Composable
fun ResultItemPreview() {
    ResultItem(
        resultEntity = ResultEntity(
            9.3, 181.0, 65, 181.0, 19.5,
            19.5, 8.8, 1, 19.5, 19.5, 19.5, 65,
            181.0, 181.0, 19.5
        )
    )
}