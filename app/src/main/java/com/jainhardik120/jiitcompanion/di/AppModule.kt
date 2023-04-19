package com.jainhardik120.jiitcompanion.di

import android.app.Application
import androidx.room.Room
import com.jainhardik120.jiitcompanion.data.local.PortalDatabase
import com.jainhardik120.jiitcompanion.data.remote.PortalApi
import com.jainhardik120.jiitcompanion.data.remote.RetrofitInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePortalApi():PortalApi{
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().apply {
                addInterceptor(RetrofitInterceptor())
            }.build())
            .baseUrl(PortalApi.BASE_URL)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun providePortalDatabase(app:Application):PortalDatabase{
        return Room.databaseBuilder(app, PortalDatabase::class.java, "portal_database").build()
    }
}