package com.jainhardik120.jiitcompanion.ui.presentation.profile

import com.jainhardik120.jiitcompanion.data.remote.model.FeedItem
import com.jainhardik120.jiitcompanion.domain.model.LoginInfo

data class ProfileScreenState(
    val feedItems: List<FeedItem> = emptyList(),
    val user:LoginInfo? =null,
    val isNextAvailable:Boolean = false,
    val isPrevAvailable:Boolean = false,
    val currentItemIndex:Int = 0
)
