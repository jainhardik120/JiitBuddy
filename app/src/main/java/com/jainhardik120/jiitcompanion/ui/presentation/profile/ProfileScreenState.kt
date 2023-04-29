package com.jainhardik120.jiitcompanion.ui.presentation.profile

import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.data.remote.model.FeedItem

data class ProfileScreenState(
    val feedItems: List<FeedItem> = emptyList(),
    val user:UserEntity? =null,
    val isNextAvailable:Boolean = false,
    val isPrevAvailable:Boolean = false,
    val currentItemIndex:Int = 0
)
