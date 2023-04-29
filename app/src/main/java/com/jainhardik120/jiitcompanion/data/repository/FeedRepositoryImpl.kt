package com.jainhardik120.jiitcompanion.data.repository

import com.jainhardik120.jiitcompanion.data.remote.FeedApi
import com.jainhardik120.jiitcompanion.data.remote.model.FeedItem
import com.jainhardik120.jiitcompanion.domain.repository.FeedRepository
import com.jainhardik120.jiitcompanion.util.Resource
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val api:FeedApi
) : FeedRepository{
    override suspend fun getAllRows(): Resource<List<FeedItem>> {
        try {
            val result = api.getAllRows()
            val json = JSONObject(result).getJSONArray("values")
            val items = List(json.length()){
                val currItem = json.getJSONArray(it)
                FeedItem(
                    title=currItem.getString(0),
                    description = currItem.getString(1),
                    date=currItem.getString(2),
                    imageUrl = currItem.getString(3),
                    webUrl=currItem.getString(4),
                )
            }
            return Resource.Success(data = items, isOnline = true)
        }catch (e:Exception){
            return Resource.Error(message = e.message.toString())
        }
    }
}