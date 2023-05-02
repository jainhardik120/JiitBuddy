package com.jainhardik120.jiitcompanion.data.remote

import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

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
    suspend fun subjectSubjectAttendanceDetail(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @POST("studentsgpacgpa/loadData")
    suspend fun sgpaLoadData(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @POST("studentsgpacgpa/getallsemesterdata")
    suspend fun studentResultData(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @POST("studentcommonsontroller/getsemestercode-exammarks")
    suspend fun studentMarksRegistrations(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @GET
    suspend fun getMarksPdf(@Url url : String, @Header("Authorization") authorization: String): retrofit2.Response<ResponseBody>

    @POST("reqsubfaculty/getregistrationList")
    suspend fun getSubjectRegistrationList(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @POST("reqsubfaculty/getfaculties")
    suspend fun getFaculties(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @POST("studentcommonsontroller/getsemestercode-withstudentexamevents")
    suspend fun getSemesterCodeExams(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @POST("studentcommonsontroller/getstudentexamevents")
    suspend fun getExamEvents(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @POST("studentsttattview/getstudent-examschedule")
    suspend fun getExamSchedule(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

    @POST("studentsgpacgpa/getallsemesterdatadetail")
    suspend fun resultDetail(@Body body: RequestBody, @Header("Authorization") authorization: String) : String

}