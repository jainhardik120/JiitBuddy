package com.jainhardik120.jiitcompanion.data.remote

import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

class RetrofitInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json, text/plain, */*")
            .addHeader("Host", "webportal.jiit.ac.in:6011")
            .build()
        return chain.proceed(request)
    }
}

interface PortalApi {
    companion object{
        const val BASE_URL = "https://webportal.jiit.ac.in:6011/StudentPortalAPI/"
    }
    @POST("token/generate-token1")
    suspend fun login(@Body body: RequestBody, @Header("Authorization") authorization: String): String

    @POST("studentpersinfo/getstudent-personalinformation")
    suspend fun personalInformation(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @POST("StudentClassAttendance/getstudentInforegistrationforattendence")
    suspend fun registrationForAttendance(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @POST("StudentClassAttendance/getstudentattendancedetail")
    suspend fun attendanceDetail(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @POST("StudentClassAttendance/getstudentsubjectpersentage")
    suspend fun subjectAttendanceDetail(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

}