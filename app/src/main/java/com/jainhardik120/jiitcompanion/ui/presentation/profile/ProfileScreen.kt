package com.jainhardik120.jiitcompanion.ui.presentation.profile

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.jainhardik120.jiitcompanion.data.remote.model.FeedItem
import com.jainhardik120.jiitcompanion.util.UiEvent
import kotlinx.coroutines.flow.collect
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {

        })
    LaunchedEffect(key1 = true) {
        viewModel.initialize()
        viewModel.uiEvent.collect {
            Log.d("TAG", "ProfileScreen: $it")
            when (it) {
                is UiEvent.OpenUrl -> {
                    Log.d("TAG", "ProfileScreen: $it")
                    try {
                        val pdfOpenIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
                        launcher.launch(pdfOpenIntent)
                    } catch (e: Exception) {
                        Log.d("myApp", "onCreateView: $e")
                    }
                }
                else -> {

                }
            }
        }
    }

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    if (state.user != null) {
        val userEntity = state.user
        Card(
            modifier = Modifier.padding(16.dp),
            onClick = {},
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    3.dp
                )
            )
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = userEntity.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = userEntity.enrollmentno, style = MaterialTheme.typography.bodyLarge)
                Text(text = userEntity.instituteLabel, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${userEntity.programcode} ${userEntity.branch} ${userEntity.admissionyear} ${userEntity.batch}",
                    style = MaterialTheme.typography.bodyLarge
                )
//                Text(text = "${userEntity.userDOB} ${userEntity.gender}")
//                Text(text = userEntity.studentcellno)
//                Text(text = userEntity.studentpersonalemailid)
            }
        }
    }

    if (state.feedItems.isNotEmpty()) {
        Box(modifier = Modifier.padding(16.dp)) {
            FeedCard(
                feedItem = state.feedItems[state.currentItemIndex],
                screenWidth - 32.dp,
                onPrevClicked = { viewModel.onEvent(ProfileScreenEvent.PrevButtonClicked) },
                onNextClicked = { viewModel.onEvent(ProfileScreenEvent.NextButtonClicked) },
                isPrevAvailable = state.isPrevAvailable,
                isNextAvailable = state.isNextAvailable,
                onItemClicked = {
                    viewModel.onEvent(ProfileScreenEvent.OpenInBrowserClicked)
                }
            )
        }
    }
}

@Composable
fun FeedCard(
    feedItem: FeedItem,
    imageHeight: Dp,
    onPrevClicked: () -> Unit,
    onNextClicked: () -> Unit,
    isPrevAvailable: Boolean,
    isNextAvailable: Boolean,
    onItemClicked: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                1.dp
            )
        )
    ) {
        Column {
            Row {
                Box(Modifier.height((imageHeight * 9 / 16))) {
                    AsyncImage(
                        model = feedItem.imageUrl.ifEmpty {
                            ""
                        },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Row(Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    enabled = isPrevAvailable,
                                    onClick = onPrevClicked
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPrevAvailable) {
                                Icon(Icons.Filled.NavigateBefore, contentDescription = "Prev Icon")
                            }
                        }
                        Spacer(modifier = Modifier.weight(3f))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    enabled = isNextAvailable,
                                    onClick = onNextClicked
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isNextAvailable) {
                                Icon(Icons.Filled.NavigateNext, contentDescription = "Next Icon")
                            }
                        }
                    }
                }

            }
            Box(
                modifier = Modifier.padding(16.dp),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        Text(
                            feedItem.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.fillMaxWidth((.8f)),
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            onItemClicked()
                        }) {
                            Icon(Icons.Filled.OpenInNew, contentDescription = "Open Link")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = feedItem.date, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = feedItem.description,
                        style = MaterialTheme.typography.bodyLarge,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

