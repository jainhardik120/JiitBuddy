package com.jainhardik120.jiitcompanion.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.jainhardik120.jiitcompanion.data.local.PortalDatabase
import com.jainhardik120.jiitcompanion.data.local.entity.ResultEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.data.remote.PortalApi
import com.jainhardik120.jiitcompanion.data.repository.model.AttendanceEntry
import com.jainhardik120.jiitcompanion.domain.model.MarksRegistration
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.util.Resource
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


@Singleton
class PortalRepositoryImpl @Inject constructor(
    private val api: PortalApi,
    db: PortalDatabase,
    private val sharedPreferences: SharedPreferences,
    @Named("FilesDir") private val externalFilesDir:String
) : PortalRepository {
    companion object{
        private const val TAG = "PortalRepositoryDebug"
    }

    private val dao = db.dao

    override fun lastUser(): Resource<Pair<String, String>> {
        val data = Pair(
            sharedPreferences.getString("enroll", "null")!!,
            sharedPreferences.getString("password", "null")!!
        )
        return Resource.Success(data = data, true)
    }

    override suspend fun updateUserLastAttendanceRegistrationId(
        enrollmentno: String,
        registrationid: String
    ) {
        dao.updateUserLastAttendanceRegistration(enrollmentno, registrationid)
    }

    override suspend fun getMarksRegistration(
        instituteid: String,
        studentid: String,
        token: String
    ): Resource<List<MarksRegistration>> {
        try {
            val payload = JSONObject(
                "{\"instituteid\":\"${instituteid}\",\"studentid\":\"${studentid}\"}\n"
            )
            val registrationData =
                api.studentMarksRegistrations(RequestBody(payload), "Bearer $token")
            Log.d(TAG, "getMarksRegistration: $registrationData")
            val array: JSONArray =
                JSONObject(registrationData).getJSONObject("response").getJSONArray("semestercode")
            val adapter = Moshi.Builder().build().adapter(MarksRegistration::class.java).lenient()
            val resultList = List(array.length()) {
                adapter.fromJson(array.getJSONObject(it).toString())!!
            }
            return Resource.Success(data = resultList, true)
        } catch (e: HttpException) {
            Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
        } catch (e: IOException) {
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
        } catch (e: Exception) {
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
        }
        return Resource.Error("Not Found")
    }

    override suspend fun getMarksPdf(
        studentid: String,
        instituteid: String,
        registrationid: String,
        registrationCode: String,
        token: String
    ):Resource<String> {
        return try {
            val response = api.getMarksPdf("https://webportal.jiit.ac.in:6011/StudentPortalAPI/studentsexamview/printstudent-exammarks/$studentid/$instituteid/$registrationid/$registrationCode", "Bearer $token")
            val file = File(externalFilesDir + "/${registrationCode}.pdf")
            Log.d(TAG, "getMarksPdf: ${file.path}")
            if(!file.exists()){
                file.createNewFile()
            }
            val fos = FileOutputStream(file)
            fos.write(response.body()?.bytes() ?: ByteArray(0))
            fos.close()
            Resource.Success(data = file.path, true)
        }catch (e:Exception){
            Log.d(TAG, "getMarksPdf: ${e.printStackTrace()}")
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun loginUser(
        enrollmentno: String,
        password: String
    ): Resource<Pair<UserEntity, String>> {
        val allUsers = dao.getUserByEnrollPass(enrollmentno, password)
        var token = "offline"
        var requiredUser: UserEntity? = null
        if (allUsers.isNotEmpty()) {
            requiredUser = allUsers[0]
        }
        var isOnline = false
        try {
            var jsonObject =
                JSONObject("{\"otppwd\":\"PWD\",\"username\":\"$enrollmentno\",\"passwordotpvalue\":\"$password\",\"Modulename\":\"STUDENTMODULE\"}")
            val regdata = api.login(RequestBody(jsonObject), "Bearer")
            with(sharedPreferences.edit()) {
                clear()
                putString("enroll", enrollmentno)
                putString("password", password)
                apply()
            }
            val loginDetails =
                JSONObject(regdata).getJSONObject("response").getJSONObject("regdata")
            token = loginDetails.getString("token")
            Log.d(TAG, "login Success: $regdata")
            jsonObject = JSONObject(
                "{\"clientid\":\"SOAU\",\"memberid\":\"${loginDetails.getString("memberid")}\",\"instituteid\":\"${
                    loginDetails.getJSONArray("institutelist").getJSONObject(0)
                        .getString("value")
                }\"}"
            )
            val generalInfo = api.personalInformation(
                RequestBody(jsonObject),
                "Bearer $token"
            )
            val generalInformation = JSONObject(generalInfo).getJSONObject("response")
                .getJSONObject("generalinformation")
            val loggedInUser = UserEntity(
                password,
                loginDetails.getString("clientid"),
                loginDetails.getString("enrollmentno"),
                loginDetails.getString("memberid"),
                loginDetails.getString("membertype"),
                loginDetails.getJSONArray("institutelist").getJSONObject(0).getString("label"),
                loginDetails.getJSONArray("institutelist").getJSONObject(0).getString("value"),
                loginDetails.getString("name"),
                loginDetails.getString("userDOB"),
                loginDetails.getString("userid"),
                generalInformation.getString("admissionyear"),
                generalInformation.getString("batch"),
                generalInformation.getString("branch"),
                generalInformation.getString("gender"),
                generalInformation.getString("institutecode"),
                generalInformation.getString("programcode"),
                generalInformation.getInt("semester"),
                generalInformation.getString("studentcellno"),
                generalInformation.getString("studentpersonalemailid")
            )
            isOnline = true
            dao.insertUser(loggedInUser)
        } catch (e: HttpException) {
            Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
        } catch (e: IOException) {
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
        } catch (e: Exception) {
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
        }
        val newAllUsers = dao.getUserByEnrollPass(enrollmentno, password)
        if (newAllUsers.isNotEmpty()) {
            requiredUser = newAllUsers[0]
        }
        if (requiredUser != null) {
            return (Resource.Success(data = Pair(requiredUser, token), isOnline = isOnline))
        }
        return Resource.Error("Not Found")
    }

    override suspend fun getAttendanceRegistrationDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        membertype: String,
        token: String
    ): Resource<List<StudentAttendanceRegistrationEntity>> {
        val oldData = dao.getStudentAttendanceRegistrationDetails(studentid)
        try {
            val payload = JSONObject(
                "{\"clientid\":\"${clientid}\",\"instituteid\":\"${instituteid}\",\"studentid\":\"${studentid}\",\"membertype\":\"${membertype}\"}\n"
            )
            val registrationData =
                api.registrationForAttendance(RequestBody(payload), "Bearer $token")
            val data =
                JSONObject(registrationData).getJSONObject("response").getJSONArray("semlist")
            val registrationList = List(data.length()) {
                StudentAttendanceRegistrationEntity(
                    studentid,
                    data.getJSONObject(it).getString("registrationcode"),
                    data.getJSONObject(it).getString("registrationid")
                )
            }
            dao.insertAttendanceRegistrations(registrationList)
            return Resource.Success(data = registrationList, true)
        } catch (e: HttpException) {
            Log.d(TAG, "attendanceRegistration: HTTP Exception : ${e.message()}")
        } catch (e: IOException) {
            Log.d(TAG, "attendanceRegistration: IO Exception : ${e.message}")
        } catch (e: Exception) {
            Log.d(TAG, "attendanceRegistration: Kotlin Exception : ${e.message}")
        }
        if (oldData.isNotEmpty()) {
            return Resource.Success(data = oldData, false)
        }
        return Resource.Error("Not Found")
    }

    override suspend fun getAttendanceDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        stynumber: Int,
        registrationid: String,
        token: String
    ): Resource<List<StudentAttendanceEntity>> {
        val oldData = dao.getStudentAttendanceOfRegistrationId(studentid, registrationid)
        try {
            val payload = JSONObject(
                "{\"clientid\":\"${clientid}\",\"instituteid\":\"${instituteid}\",\"studentid\":\"${studentid}\",\"stynumber\":\"$stynumber\",\"registrationid\":\"${registrationid}\"}"
            )
            val attendanceData = api.attendanceDetail(RequestBody(payload), "Bearer $token")
            val array =
                JSONObject(attendanceData).getJSONObject("response")
                    .getJSONArray("studentattendancelist")
            val resultList = List(array.length()) {
                val jsonObject = array.getJSONObject(it)
                StudentAttendanceEntity(
                    studentid,
                    registrationid,
                    if (jsonObject.getString("Lsubjectcomponentcode") == "L" && jsonObject.getString(
                            "Tsubjectcomponentcode"
                        ) == "T"
                    ) {
                        jsonObject.getDouble("LTpercantage")
                    } else {
                        0.0
                    },
                    if (jsonObject.getString("Lsubjectcomponentcode") == "L") {
                        jsonObject.getDouble("Lpercentage")
                    } else {
                        0.0
                    },
                    jsonObject.getString("Lsubjectcomponentcode") ?: "",
                    jsonObject.getString("Lsubjectcomponentid") ?: "",
                    if (jsonObject.getString("Lsubjectcomponentcode") == "L") {
                        jsonObject.getDouble("Ltotalclass")
                    } else {
                        0.0
                    },
                    if (jsonObject.getString("Lsubjectcomponentcode") == "L") {
                        jsonObject.getDouble("Ltotalpres")
                    } else {
                        0.0
                    },
                    if (jsonObject.getString("Psubjectcomponentcode") == "P") {
                        jsonObject.getDouble("Ppercentage")
                    } else {
                        0.0
                    },
                    jsonObject.getString("Psubjectcomponentcode") ?: "",
                    jsonObject.getString("Psubjectcomponentid") ?: "",
                    if (jsonObject.getString("Tsubjectcomponentcode") == "T") {
                        jsonObject.getDouble("Tpercentage")
                    } else {
                        0.0
                    },
                    jsonObject.getString("Tsubjectcomponentcode") ?: "",
                    jsonObject.getString("Tsubjectcomponentid") ?: "",
                    if (jsonObject.getString("Tsubjectcomponentcode") == "T") {
                        jsonObject.getDouble("Ttotalclass")
                    } else {
                        0.0
                    },
                    if (jsonObject.getString("Tsubjectcomponentcode") == "T") {
                        jsonObject.getDouble("Ttotalpres")
                    } else {
                        0.0
                    },
                    (jsonObject.getString("abseent") ?: "0.0").toDouble(),
                    jsonObject.getInt("slno"),
                    jsonObject.getString("subjectcode") ?: "",
                    jsonObject.getString("subjectid") ?: ""
                )
            }
            dao.insertAttendanceEntities(resultList)
            return Resource.Success(data = resultList, true)
        } catch (e: HttpException) {
            Log.d(TAG, "getStudentResultData: HTTP Exception : ${e.message()}")
        } catch (e: IOException) {
            Log.d(TAG, "getStudentResultData: IO Exception : ${e.message}")
        } catch (e: Exception) {
            Log.d(TAG, "getStudentResultData: Kotlin Exception : ${e.message}")
        }
        if (oldData.isNotEmpty()) {
            return Resource.Success(data = oldData, false)
        }
        return Resource.Error(message = "Not Found")
    }

    override suspend fun getStudentResultData(
        instituteid: String,
        studentid: String,
        stynumber: Int,
        token: String
    ): Resource<List<ResultEntity>> {
        try {
            val payload = JSONObject(
                "{\"instituteid\":\"${instituteid}\",\"studentid\":\"${studentid}\",\"stynumber\":\"${stynumber}\"}\n"
            )
            val registrationData = api.studentResultData(RequestBody(payload), "Bearer $token")
            val array =
                JSONObject(registrationData).getJSONObject("response").getJSONArray("semesterList")
            val adapter = Moshi.Builder().build().adapter(ResultEntity::class.java).lenient()
            val resultList = List(array.length()) {
                adapter.fromJson(array.getJSONObject(it).toString())!!
            }
            return Resource.Success(data = resultList, true)
        } catch (e: HttpException) {
            Log.d(TAG, "getStudentResultData: HTTP Exception : ${e.message()}")

        } catch (e: IOException) {
            Log.d(TAG, "getStudentResultData: IO Exception : ${e.message}")

        } catch (e: Exception) {
            Log.d(TAG, "getStudentResultData: Kotlin Exception : ${e.message}")
        }
        return Resource.Error(message = "Not Found")
    }

    private fun RequestBody(jsonObject: JSONObject): RequestBody {
        return jsonObject.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())
    }

    override suspend fun getSubjectAttendanceDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        subjectId: String,
        registrationid: String,
        cmpidkey: String,
        token: String
    ): Resource<List<AttendanceEntry>> {
        try {
            val postParams = JSONObject(
                "{\"clientid\":\"${
                    clientid
                }\",\"instituteid\":\"${
                    instituteid
                }\",\"registrationid\":\"${
                    registrationid
                }\",\"studentid\":\"${
                    studentid
                }\",\"subjectid\":\"${
                    subjectId
                }\",\"cmpidkey\":[${cmpidkey}]}"
            )
            val data = api.subjectSubjectAttendanceDetail(RequestBody(postParams), "Bearer $token")
            val array =
                JSONObject(data).getJSONObject("response").getJSONArray("studentAttdsummarylist")
            val adapter = Moshi.Builder().build().adapter(AttendanceEntry::class.java).lenient()
            val resultList = List(array.length()) {
                adapter.fromJson(array.getJSONObject(it).toString())!!
            }
            return Resource.Success(data = resultList, isOnline = true)
        } catch (e: HttpException) {
            Log.d(TAG, "getStudentResultData: HTTP Exception : ${e.message()}")

        } catch (e: IOException) {
            Log.d(TAG, "getStudentResultData: IO Exception : ${e.message}")

        } catch (e: Exception) {
            Log.d(TAG, "getStudentResultData: Kotlin Exception : ${e.message}")
        }
        return Resource.Error(message = "Unknown")
    }
}