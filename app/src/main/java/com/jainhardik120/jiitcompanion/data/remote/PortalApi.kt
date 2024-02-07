package com.jainhardik120.jiitcompanion.data.remote

import com.jainhardik120.jiitcompanion.data.repository.PortalRepositoryImpl
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url
import java.security.SecureRandom
import java.time.LocalDate

class RetrofitInterceptor : Interceptor {
    private fun generateLocalName() : String{
        val l = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
        val random = SecureRandom()
        val stringBuilder = StringBuilder()

        repeat(9) {
            val randomIndex = random.nextInt(l.length)
            stringBuilder.append(l[randomIndex])
        }

        val o = stringBuilder.toString()
        val r = o.substring(0, 4)
        val u = o.substring(4, 9)

        val s = LocalDate.now()
        val d = s.dayOfMonth.toString().padStart(2, '0')
        val c = (s.dayOfWeek.value % 7).toString()
        val p = s.monthValue.toString().padStart(2, '0')
        val h = s.year.toString().substring(2)

        val v = "${d[0]}${p[0]}${h[0]}$c${d[1]}${p[1]}${h[1]}"
        val loc= PortalRepositoryImpl.aes256Encryption(r+v+u)
        println(loc)
        return loc
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Accept", "application/json, text/plain, */*")
            .addHeader("Host", "webportal.jiit.ac.in:6011")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/116.0")
            .addHeader("Accept-Language", "en-GB,en;q=0.5")
            .addHeader("Accept-Encoding", "gzip, deflate, br")
            .addHeader("Content-Type", "application/json")
            .addHeader("Origin", "https://webportal.jiit.ac.in:6011")
            .addHeader("Connection", "keep-alive")
            .addHeader("Referer", "https://webportal.jiit.ac.in:6011/studentportal/")
            .addHeader("Sec-Fetch-Dest", "empty")
            .addHeader("Sec-Fetch-Mode", "cors")
            .addHeader("Sec-Fetch-Site", "same-origin")
            .addHeader("Localname", generateLocalName().trim())
            .build()
        return chain.proceed(request)
    }
}

interface PortalApi {
    companion object{
        const val BASE_URL = "https://webportal.jiit.ac.in:6011/StudentPortalAPI/"
    }

    @GET("token/getcaptcha")
    suspend fun getCaptcha(@Header("Authorization") authorization: String): String

    @POST("token/pretoken-check")
    suspend fun validateCaptcha(@Body body: String, @Header("Authorization") authorization: String) : String

    @POST("token/generate-token1")
    suspend fun login(@Body body: String, @Header("Authorization") authorization: String): String

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