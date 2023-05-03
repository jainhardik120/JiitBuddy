package com.jainhardik120.jiitcompanion.ui.presentation.profile

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.jainhardik120.jiitcompanion.data.remote.model.FeedItem
import com.jainhardik120.jiitcompanion.util.UiEvent
import com.jainhardik120.jiitcompanion.util.drawVerticalScrollbar
import com.jainhardik120.jiitcompanion.util.flingBehaviorIgnoringMotionScale
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Github
import compose.icons.simpleicons.Instagram
import compose.icons.simpleicons.Linkedin
import compose.icons.simpleicons.Reddit
import compose.icons.simpleicons.Twitter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(key1 = true) {
        viewModel.initialize()
        viewModel.uiEvent.collect {
            Log.d("TAG", "ProfileScreen: $it")
            when (it) {
                is UiEvent.OpenUrl -> {
                    uriHandler.openUri(it.url)
                }

                else -> {

                }
            }
        }
    }

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    ScrollbarLazyColumn(content = {
        item {
            if (state.user != null) {
                val userEntity = state.user
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                        Text(
                            text = userEntity.enrollmentno,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = userEntity.instituteLabel,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${userEntity.programcode} ${userEntity.branch} ${userEntity.admissionyear} ${userEntity.batch}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
//        item{
//            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//                BannerAdView(true)
//                NativeAdView(true)
//            }
//        }
        item {
            if (state.feedItems.isNotEmpty()) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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
        item {
            Signature()
        }


    })
}

@Composable
fun ScrollbarLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = flingBehaviorIgnoringMotionScale(),
    userScrollEnabled: Boolean = true,
    content: LazyListScope.() -> Unit,
) {
    val direction = LocalLayoutDirection.current
    val density = LocalDensity.current
    val positionOffset = remember(contentPadding) {
        with(density) { contentPadding.calculateEndPadding(direction).toPx() }
    }
    LazyColumn(
        modifier = modifier
            .drawVerticalScrollbar(
                state = state,
                reverseScrolling = reverseLayout,
                positionOffsetPx = positionOffset,
            ),
        state = state,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        content = content,
    )
}


@Composable
fun Signature() {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
            Text(text = "Crafted with " + String(Character.toChars(0x2764)) + " by Hardik Jain", Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                LinkIcon(
                    label = "Twitter",
                    icon = SimpleIcons.Twitter,
                    url = "https://twitter.com/jainhardik17",
                )
                LinkIcon(
                    label = "Reddit",
                    icon = SimpleIcons.Reddit,
                    url = "https://www.reddit.com/user/HardikJain17",
                )
                LinkIcon(
                    label = "Instagram",
                    icon = SimpleIcons.Instagram,
                    url = "https://instagram.com/_.hardikj",
                )
                LinkIcon(
                    label = "LinkedIn",
                    icon = SimpleIcons.Linkedin,
                    url = "https://www.linkedin.com/in/jainhardik120/",
                )
                LinkIcon(
                    label = "GitHub",
                    icon = SimpleIcons.Github,
                    url = "https://github.com/jainhardik120",
                )
            }
        }
    }

}

@Composable
fun LinkIcon(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    url: String,
) {
    val uriHandler = LocalUriHandler.current
    IconButton(
        modifier = modifier.padding(4.dp),
        onClick = { uriHandler.openUri(url) },
    ) {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = label,
        )
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

