package com.jainhardik120.jiitcompanion.domain.repository

import com.jainhardik120.jiitcompanion.data.remote.model.BlockedUserItem
import com.jainhardik120.jiitcompanion.data.remote.model.FeedItem
import com.jainhardik120.jiitcompanion.util.Resource

interface FeedRepository {
    suspend fun getAllRows(): Resource<List<FeedItem>>

    suspend fun getBlockedUsers(): Resource<List<BlockedUserItem>>
}