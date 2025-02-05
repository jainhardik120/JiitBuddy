package com.jainhardik120.jiitcompanion.data.remote

import retrofit2.http.GET

interface FeedApi {
    companion object {
        const val BASE_URL = "https://sheets.googleapis.com/v4/spreadsheets/"
    }

    @GET("1DOSHefAF4SsXi3BMbOuHFBnsU3mM_zd_Q06e78E1z58/values/A%3AE?key=AIzaSyBgvynTyHpWOnv3kAlNGNU68_96ovc-jF0")
    suspend fun getAllRows(): String

    @GET("1wDIqkoH7nD1JtNU5zx3U2sK68hylUz_82A7lHkFijAU/values/A%3AB?key=AIzaSyBgvynTyHpWOnv3kAlNGNU68_96ovc-jF0")
    suspend fun getBlockedUsers(): String
}