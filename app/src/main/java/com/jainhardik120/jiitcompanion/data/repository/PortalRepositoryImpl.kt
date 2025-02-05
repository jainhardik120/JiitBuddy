package com.jainhardik120.jiitcompanion.data.repository

import android.content.SharedPreferences
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.jainhardik120.jiitcompanion.data.local.PortalDatabase
import com.jainhardik120.jiitcompanion.data.local.entity.ExamEventsEntity
import com.jainhardik120.jiitcompanion.data.local.entity.ExamRegistrationsEntity
import com.jainhardik120.jiitcompanion.data.local.entity.ExamScheduleEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.data.remote.PortalApi
import com.jainhardik120.jiitcompanion.data.remote.model.AttendanceEntry
import com.jainhardik120.jiitcompanion.data.remote.model.CaptchaResponse
import com.jainhardik120.jiitcompanion.data.remote.model.CaptchaValidationResponse
import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration
import com.jainhardik120.jiitcompanion.data.remote.model.RegisteredSubject
import com.jainhardik120.jiitcompanion.data.remote.model.ResultDetailEntity
import com.jainhardik120.jiitcompanion.data.remote.model.ResultEntity
import com.jainhardik120.jiitcompanion.data.remote.model.SubjectSemesterRegistrations
import com.jainhardik120.jiitcompanion.domain.ExamScheduleModel
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.util.Resource
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

enum class SharedPreferencesKeys(val key: String) {
    LastUserName("LastEnroll"),
    LastPassword("LastPass"),
    SheetOpenings("Openings"),
    IsSheetOpened("IsOpened"),
    AttendanceWarningCriteria("attendance_warning")
}

@Singleton
class PortalRepositoryImpl @Inject constructor(
    private val api: PortalApi,
    db: PortalDatabase,
    private val sharedPreferences: SharedPreferences,
    @Named("FilesDir") private val externalFilesDir: String
) : PortalRepository {

    private val dao = db.dao

    companion object {
        private const val TAG = "PortalRepositoryDebug"

        private fun generateKeyValue(): String {
            val e = LocalDate.now()
            val n = e.dayOfMonth
            val t = e.dayOfWeek.value % 7
            val l = e.monthValue
            val o = e.year.toString().substring(2)

            val i = if (n.toString().length < 2) n.toString().padStart(2, '0') else n.toString()
            val a = if (l.toString().length < 2) l.toString().padStart(2, '0') else l.toString()

            val r = "qa8y" + "${i[0]}${a[0]}${o[0]}$t${i[1]}${a[1]}${o[1]}" + "ty1pn"
            println(r)
            return r
        }

        fun aes256Encryption(plainText: String): String {
            val remoteConfig = Firebase.remoteConfig
            val iv = remoteConfig.getString("enc_iv")
            val encKey = generateKeyValue()
            val ivBytes = iv.toByteArray()
            val secretKeyBytes = encKey.toByteArray()
            val secretKeySpec = SecretKeySpec(secretKeyBytes, "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val ivParameterSpec = IvParameterSpec(ivBytes)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray())
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        }
    }

    private fun RequestBody(jsonObject: JSONObject): RequestBody {
        return jsonObject.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())
    }

    private fun RequestBody(vararg pairs: Pair<String, String>): RequestBody {
        return JSONObject(pairs.toMap()).toString()
            .toRequestBody("application/json".toMediaTypeOrNull())
    }


    override fun lastUser(): Pair<String, String> {
        return Pair(
            sharedPreferences.getString(SharedPreferencesKeys.LastUserName.key, "null") ?: "null",
            sharedPreferences.getString(SharedPreferencesKeys.LastPassword.key, "null") ?: "null"
        )
    }

    override fun incrementSheetOpening() {
        val openings = getOpenings()
        with(sharedPreferences.edit()) {
            putInt(SharedPreferencesKeys.SheetOpenings.key, openings + 1)
            apply()
        }
    }

    override fun getOpenings(): Int {
        return sharedPreferences.getInt(SharedPreferencesKeys.SheetOpenings.key, 0)
    }

    override fun getIsOpened(): Boolean {
        return sharedPreferences.getBoolean(SharedPreferencesKeys.IsSheetOpened.key, false)
    }

    override fun updateOpened() {
        with(sharedPreferences.edit()) {
            putBoolean(SharedPreferencesKeys.IsSheetOpened.key, true)
            apply()
        }
    }

    override suspend fun getLastAttendanceRegistration(enrollmentno: String): Resource<String> {
        val result = dao.getStudentLastAttendanceRegistration(enrollmentno)
        return if (result.isNotEmpty()) {
            try {
                Resource.Success(data = result[0], true)
            } catch (e: Exception) {
                Resource.Error(message = e.message ?: "")
            }
        } else {
            Resource.Error("Empty List")
        }
    }

    override fun getAttendanceWarning(): Int {
        return sharedPreferences.getInt(SharedPreferencesKeys.AttendanceWarningCriteria.key, 80)
    }

    override fun updateAttendanceWarning(warning: Int) {
        with(sharedPreferences.edit()) {
            putInt(SharedPreferencesKeys.AttendanceWarningCriteria.key, warning)
            apply()
        }
    }

    override suspend fun getCaptcha(): Resource<CaptchaResponse> {
        val message: String
        try {
            val captchaData = api.getCaptcha("Bearer")
            val data =
                JSONObject(captchaData).getJSONObject("response").getJSONObject("captcha")
            val captchaResponse = CaptchaResponse(data.getString("hidden"), data.getString("image"))
            return Resource.Success(captchaResponse, true)
        } catch (e: HttpException) {
            Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
            message = e.message() ?: ""
        } catch (e: IOException) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
        } catch (e: Exception) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
        }
        return Resource.Error(message = message)
    }

    override suspend fun validateCaptcha(
        username: String,
        captcha: String,
        hidden: String,
        image: String
    ): Resource<CaptchaValidationResponse> {
        val message: String
        try {
            val result = api.validateCaptcha(
                aes256Encryption(JSONObject().apply {
                    put("username", username)
                    put("usertype", "S")
                    put("captcha", JSONObject().apply {
                        put("captcha", captcha)
                        put("hidden", hidden)
                        put("image", image)
                    })
                }.toString()),
                "Bearer"
            )
            val data = JSONObject(result).getJSONObject("response")
            return Resource.Success(
                CaptchaValidationResponse(
                    data.getString("random"),
                    data.getString("otppwd"),
                    data.getString("rejectedData"),
                    data.getString("username")
                ),
                true
            )
        } catch (e: HttpException) {
            Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
            message = e.message() ?: ""
        } catch (e: IOException) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
        } catch (e: Exception) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
        }
        return Resource.Error(message = message)
    }

    override suspend fun loginUser(
        enrollmentno: String,
        password: String, random: String
    ): Resource<Pair<UserEntity, String>> {
        val allUsers = dao.getUserByEnrollPass(enrollmentno, password)
        var token = "offline"
        var requiredUser: UserEntity? = null
        if (allUsers.isNotEmpty()) {
            requiredUser = allUsers[0]
        }
        var isOnline = false
        var errorMessage = ""
        try {
            val encryptedValue = aes256Encryption(
                JSONObject(
                    mapOf(
                        Pair("otppwd", "PWD"),
                        Pair("username", enrollmentno),
                        Pair("passwordotpvalue", password),
                        Pair("Modulename", "STUDENTMODULE"),
                        Pair("random", random)
                    )
                ).toString()
            )
            Log.d(TAG, "loginUser: $encryptedValue")
            val regdata = api.login(
                encryptedValue, "Bearer"
            )
            Log.d(TAG, "loginUser: $regdata")
            val loginDetails =
                JSONObject(regdata).getJSONObject("response").getJSONObject("regdata")
            token = loginDetails.getString("token")
            with(sharedPreferences.edit()) {
                putString(SharedPreferencesKeys.LastUserName.key, enrollmentno)
                putString(SharedPreferencesKeys.LastPassword.key, password)
                apply()
            }
            val generalInfo = api.personalInformation(
                RequestBody(
                    Pair("clientid", "SOAU"),
                    Pair("memberid", loginDetails.getString("memberid")),
                    Pair(
                        "instituteid", loginDetails.getJSONArray("institutelist").getJSONObject(0)
                            .getString("value")
                    )
                ),
                "Bearer $token"
            )
            val generalInformation = JSONObject(generalInfo).getJSONObject("response")
                .getJSONObject("generalinformation")
            val loggedInUser = UserEntity(
                password,
                enrollmentno,
                loginDetails.getString("clientid"),
                loginDetails.getString("enrollmentno"),
                loginDetails.getString("memberid"),
                loginDetails.getString("membertype") ?: "S",
                loginDetails.getJSONArray("institutelist").getJSONObject(0).getString("label"),
                loginDetails.getJSONArray("institutelist").getJSONObject(0).getString("value"),
                loginDetails.getString("name"),
                loginDetails.getString("userDOB"),
                loginDetails.getString("userid"),
                generalInformation.getString("admissionyear") ?: "",
                generalInformation.getString("batch") ?: "",
                generalInformation.getString("branch") ?: "",
                generalInformation.getString("gender") ?: "",
                generalInformation.getString("institutecode") ?: "",
                generalInformation.getString("programcode") ?: "",
                generalInformation.getInt("semester"),
                generalInformation.getString("studentcellno") ?: "",
                generalInformation.getString("studentpersonalemailid") ?: "",
                requiredUser?.lastAttendanceRegistrationId
            )
            dao.insertUser(loggedInUser)
            isOnline = true
        } catch (e: HttpException) {
            Log.d(TAG, "loginUser: HTTP Exception : ${e.response()?.code()}")
            errorMessage = e.message()
            if (e.response()?.code() == 404) {
                return Resource.Error("Wrong Enrollment No or Password")
            }
        } catch (e: IOException) {
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
            errorMessage = e.message ?: "Server Not Reachable"
        } catch (e: Exception) {
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
            errorMessage = e.message ?: e.printStackTrace().toString()
        }
        val newAllUsers = dao.getUserByEnrollPass(enrollmentno, password)
        Log.d(TAG, "loginUser: $newAllUsers")
        if (newAllUsers.isNotEmpty()) {
            requiredUser = newAllUsers[0]
        }
        if (requiredUser != null) {
            return (Resource.Success(data = Pair(requiredUser, token), isOnline = isOnline))
        }
        return Resource.Error(errorMessage)
    }

    override suspend fun logOut(studentid: String) {
        dao.deleteAttendanceEntity(studentid)
        dao.deleteAttendanceRegistrations(studentid)
        dao.deleteUserEntity(studentid)
        dao.deleteExamEvents(studentid)
        dao.deleteExamRegistrations(studentid)
        dao.deleteExamSchedules(studentid)
        with(sharedPreferences.edit()) {
            remove(SharedPreferencesKeys.LastUserName.key)
            remove(SharedPreferencesKeys.LastPassword.key)
            apply()
        }
    }

    override suspend fun updateUserLastAttendanceRegistrationId(
        enrollmentno: String,
        registrationid: String
    ) {
        dao.updateUserLastAttendanceRegistration(enrollmentno, registrationid)
    }

    override suspend fun getAttendanceRegistrationDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        membertype: String,
        token: String
    ): Resource<List<StudentAttendanceRegistrationEntity>> {
        val message: String
        val oldData = dao.getStudentAttendanceRegistrationDetails(studentid)
        try {
            val registrationData =
                api.registrationForAttendance(
                    RequestBody(
                        Pair("clientid", clientid),
                        Pair("instituteid", instituteid),
                        Pair("studentid", studentid),
                        Pair("membertype", membertype),
                    ), "Bearer $token"
                )
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
            Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
            message = e.message() ?: ""
        } catch (e: IOException) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
        } catch (e: Exception) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
        }

        if (oldData.isNotEmpty()) {
            return Resource.Success(data = oldData, false)
        }
        return Resource.Error(message)
    }

    override suspend fun getAttendanceDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        stynumber: Int,
        registrationid: String,
        registrationCode: String,
        token: String
    ): Resource<List<StudentAttendanceEntity>> {
        val message: String
        val oldData = dao.getStudentAttendanceOfRegistrationId(studentid, registrationid)
        try {
            val attendanceData = api.attendanceDetail(
                RequestBody(
                    Pair("clientid", clientid),
                    Pair("instituteid", instituteid),
                    Pair("studentid", studentid),
                    Pair("stynumber", stynumber.toString()),
                    Pair("registrationid", registrationid),
                    Pair("registrationcode", registrationCode)
                ), "Bearer $token"
            )
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
                    jsonObject.getString("subjectid") ?: "",
                    jsonObject.getString("individualsubjectcode") ?: ""
                )
            }
            dao.insertAttendanceEntities(resultList)
            return Resource.Success(data = resultList, true)
        } catch (e: HttpException) {
            Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
            message = e.message() ?: ""
        } catch (e: IOException) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
        } catch (e: Exception) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
        }
        if (oldData.isNotEmpty()) {
            return Resource.Success(data = oldData, false)
        }
        return Resource.Error(message = message)
    }


    override suspend fun getSubjectAttendanceDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        subjectId: String,
        registrationid: String,
        cmpidkey: String,
        registrationCode: String,
        subjectcode: String,
        token: String
    ): Resource<List<AttendanceEntry>> {
        val message: String
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
                }\",\"registrationcode\":\"${
                    registrationCode
                }\",\"subjectcode\":\"${
                    subjectcode
                }\",\"subjectid\":\"${
                    subjectId
                }\",\"cmpidkey\":[${cmpidkey}]}"
            )
            val data = api.subjectSubjectAttendanceDetail(
                RequestBody(
                    /*Pair("clientid",clientid),
                    Pair("instituteid",instituteid),
                    Pair("registrationid",registrationid),
                    Pair("studentid",studentid),
                    Pair("subjectid",subjectId),
                    Pair("cmpidkey",),*/
                    postParams
                ), "Bearer $token"
            )
            val array =
                JSONObject(data).getJSONObject("response").getJSONArray("studentAttdsummarylist")
            val adapter = Moshi.Builder().build().adapter(AttendanceEntry::class.java).lenient()
            val resultList = List(array.length()) {
                adapter.fromJson(array.getJSONObject(it).toString())!!
            }
            return Resource.Success(data = resultList, isOnline = true)
        } catch (e: HttpException) {
            Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
            message = e.message() ?: ""
        } catch (e: IOException) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
        } catch (e: Exception) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
        }
        return Resource.Error(message)
    }

    override suspend fun getStudentResultData(
        instituteid: String,
        studentid: String,
        token: String
    ): Resource<List<ResultEntity>> {
        val message: String
        try {
            val loadingData = api.sgpaLoadData(
                RequestBody(
                    Pair("instituteid", instituteid),
                    Pair("studentid", studentid)
                ), "Bearer $token"
            )
            val registrationData = api.studentResultData(
                RequestBody(
                    Pair("instituteid", instituteid),
                    Pair("studentid", studentid),
                    Pair(
                        "stynumber",
                        JSONObject(loadingData).getJSONObject("response")
                            .getJSONArray("studentInfo")
                            .getJSONObject(0)
                            .getString("stynumber")
                    )
                ), "Bearer $token"
            )
            val array =
                JSONObject(registrationData).getJSONObject("response").getJSONArray("semesterList")
            val adapter = Moshi.Builder().build().adapter(ResultEntity::class.java).lenient()
            val resultList = List(array.length()) {
                adapter.fromJson(array.getJSONObject(it).toString())!!
            }
            return Resource.Success(data = resultList, true)
        } catch (e: HttpException) {
            Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
            message = e.message() ?: ""
        } catch (e: IOException) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
        } catch (e: Exception) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
        }
        return Resource.Error(message)
    }

    override suspend fun getResultDetail(
        studentid: String,
        instituteid: String,
        stynumber: Int,
        token: String
    ): Resource<List<ResultDetailEntity>> {
        val errorMessage: String
        try {
            val registrationData =
                api.resultDetail(
                    RequestBody(
                        Pair("instituteid", instituteid),
                        Pair("studentid", studentid),
                        Pair("stynumber", stynumber.toString())
                    ), "Bearer $token"
                )
            val data =
                JSONObject(registrationData).getJSONObject("response").getJSONArray("semesterList")
            val adapter = Moshi.Builder().build().adapter(ResultDetailEntity::class.java).lenient()
            val resultList = List(data.length()) {
                adapter.fromJson(data.getJSONObject(it).toString())!!
            }
            return Resource.Success(data = resultList, isOnline = true)
        } catch (e: HttpException) {
            Log.d(TAG, "attendanceRegistration: HTTP Exception : ${e.message()}")
            errorMessage = e.message()
        } catch (e: IOException) {
            Log.d(TAG, "attendanceRegistration: IO Exception : ${e.message}")
            errorMessage = e.message.toString()
        } catch (e: Exception) {
            Log.d(TAG, "attendanceRegistration: Kotlin Exception : ${e.message}")
            errorMessage = e.message.toString()
        }
        return Resource.Error(errorMessage)
    }

    override suspend fun getMarksRegistration(
        instituteid: String,
        studentid: String,
        token: String
    ): Resource<List<MarksRegistration>> {
        val message: String
        try {
            val registrationData =
                api.studentMarksRegistrations(
                    RequestBody(
                        Pair("instituteid", instituteid),
                        Pair("studentid", studentid)
                    ), "Bearer $token"
                )
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
            message = e.message() ?: ""
        } catch (e: IOException) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
        } catch (e: Exception) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
        }
        return Resource.Error(message)
    }

    override suspend fun getMarksPdf(
        studentid: String,
        instituteid: String,
        registrationid: String,
        registrationCode: String,
        token: String
    ): Resource<String> {
        return try {
            withContext(Dispatchers.IO) {
                val response = api.getMarksPdf(
                    "https://webportal.jiit.ac.in:6011/StudentPortalAPI/studentsexamview/printstudent-exammarks/$studentid/$instituteid/$registrationid/$registrationCode",
                    "Bearer $token"
                )
                val file = File(externalFilesDir + "/${registrationCode}.pdf")
                Log.d(TAG, "getMarksPdf: ${file.path}")
                if (!file.exists()) {
                    file.createNewFile()
                }
                val fos = FileOutputStream(file)
                fos.write(response.body()?.bytes() ?: ByteArray(0))
                fos.close()
                Resource.Success(data = file.path, true)
            }
        } catch (e: Exception) {
            Log.d(TAG, "getMarksPdf: ${e.printStackTrace()}")
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getExamRegistrations(
        clientid: String,
        instituteid: String,
        studentid: String,
        token: String
    ): Resource<List<ExamRegistrationsEntity>> {
        val oldData = dao.getExamRegistrations(studentid)
        if (token == "offline") {
            return Resource.Success(oldData, isOnline = false)
        }
        val errorMessage: String
        try {
            val registrationData =
                api.getSemesterCodeExams(
                    RequestBody(
                        Pair("clientid", clientid),
                        Pair("instituteid", instituteid),
                        Pair("memberid", studentid),
                    ), "Bearer $token"
                )
            val data =
                JSONObject(registrationData).getJSONObject("response")
                    .getJSONObject("semesterCodeinfo")
                    .getJSONArray("semestercode")
            val registrationList = List(data.length()) {
                ExamRegistrationsEntity(
                    studentid,
                    data.getJSONObject(it).getString("registrationcode"),
                    data.getJSONObject(it).getString("registrationdesc"),
                    data.getJSONObject(it).getString("registrationid")
                )
            }
            dao.insertExamRegistrations(registrationList)
            return Resource.Success(data = registrationList, true)
        } catch (e: HttpException) {
            Log.d(TAG, "attendanceRegistration: HTTP Exception : ${e.message()}")
            errorMessage = e.message()
        } catch (e: IOException) {
            Log.d(TAG, "attendanceRegistration: IO Exception : ${e.message}")
            errorMessage = e.message.toString()
        } catch (e: Exception) {
            Log.d(TAG, "attendanceRegistration: Kotlin Exception : ${e.message}")
            errorMessage = e.message.toString()
        }
        if (oldData.isNotEmpty()) {
            return Resource.Success(data = oldData, false)
        }
        return Resource.Error(errorMessage)
    }

    override suspend fun getExamEvents(
        studentid: String,
        instituteid: String,
        registrationid: String,
        token: String
    ): Resource<List<ExamEventsEntity>> {
        Log.d(TAG, "getExamEvents: $studentid $registrationid")
        var oldData = dao.getExamEvents(studentid, registrationid)
        Log.d(TAG, "getExamEvents: $oldData")
        if (token == "offline") {
            return Resource.Success(oldData, isOnline = false)
        }
        val errorMessage: String
        try {
            val payload = JSONObject(
                "{\"instituteid\":\"${instituteid}\",\"registationid\":\"${registrationid}\"}\n"
            )
            val registrationData =
                api.getExamEvents(RequestBody(payload), "Bearer $token")
            val data =
                JSONObject(registrationData).getJSONObject("response").getJSONObject("eventcode")
                    .getJSONArray("examevent")
            val registrationList = List(data.length()) {
                ExamEventsEntity(
                    studentid,
                    data.getJSONObject(it).getLong("eventfrom"),
                    data.getJSONObject(it).getString("exameventcode"),
                    data.getJSONObject(it).getString("exameventdesc"),
                    data.getJSONObject(it).getString("exameventid"),
                    data.getJSONObject(it).getString("registrationid")
                )
            }
            dao.insertExamEvents(registrationList)
            oldData = dao.getExamEvents(studentid, registrationid)
            return Resource.Success(data = oldData, true)
        } catch (e: HttpException) {
            Log.d(TAG, "attendanceRegistration: HTTP Exception : ${e.message()}")
            errorMessage = e.message()
        } catch (e: IOException) {
            Log.d(TAG, "attendanceRegistration: IO Exception : ${e.message}")
            errorMessage = e.message.toString()
        } catch (e: Exception) {
            Log.d(TAG, "attendanceRegistration: Kotlin Exception : ${e.message}")
            errorMessage = e.message.toString()
        }
        if (oldData.isNotEmpty()) {
            return Resource.Success(data = oldData, false)
        }
        return Resource.Error(errorMessage)
    }

    private fun dayOfDateTime(dateTime: String, dateTimeUpto: String): LocalDateTime? {
        return try {
            val calendarDate = dateTime.split("/")
            val year = Integer.parseInt(calendarDate[2])
            val month = Integer.parseInt(calendarDate[1])
            val date = Integer.parseInt(calendarDate[0])
            val timeVal = dateTimeUpto.split(" ")
            val finishTime = timeVal[3]
            val finishTimeValues = finishTime.split(":")
            var hour = Integer.parseInt(finishTimeValues[0])
            val minutes = Integer.parseInt(finishTimeValues[1])
            if (timeVal[4] == "pm") {
                if (hour < 12) {
                    hour += 12
                }
            } else {
                if (hour == 12) {
                    hour = 0
                }
            }
            LocalDateTime.of(year, month, date, hour, minutes)
        } catch (e: Exception) {
            Log.d(TAG, "dayOfDateTime: ${e.printStackTrace()}")
            null
        }
    }

    override suspend fun getExamSchedules(
        exameventid: String,
        registrationid: String,
        instituteid: String,
        studentid: String,
        token: String
    ): Resource<List<ExamScheduleModel>> {
        Log.d(TAG, "getExamEvents: $studentid $exameventid")
        val dateNow = LocalDateTime.now()
        Log.d(TAG, "getExamSchedules: ${dateNow.hour}:${dateNow.minute}")
        var oldData = dao.getExamSchedules(studentid, exameventid)
        var data = List(oldData.size) {
            val date = dayOfDateTime(oldData[it].datetime, oldData[it].datetimeupto)
            if (date != null) {
                Log.d(TAG, "getExamSchedules: ${date.hour}:${date.minute}")
            }
            ExamScheduleModel(
                oldData[it].datetime,
                oldData[it].datetimeupto,
                oldData[it].subjectdesc,
                oldData[it].roomcode,
                oldData[it].seatno,
                false,
                try {
                    date?.dayOfWeek?.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) ?: "NULL"
                } catch (e: Exception) {
                    "NULL"
                }
            )
        }
        Log.d(TAG, "getExamEvents: $oldData")
        if (token == "offline") {
            return Resource.Success(data, isOnline = false)
        }
        val errorMessage: String
        try {
            val payload = JSONObject(
                "{\"memberid\":\"${
                    studentid
                }\",\"instituteid\":\"${
                    instituteid
                }\",\"exameventid\":\"${exameventid}\",\"registrationid\":\"${registrationid}\"}"
            )
            val registrationData =
                api.getExamSchedule(RequestBody(payload), "Bearer $token")
            val array =
                JSONObject(registrationData).getJSONObject("response").getJSONArray("subjectinfo")
            val registrationList = List(array.length()) {
                val jsonObjectSubData = array.getJSONObject(it)
                ExamScheduleEntity(
                    studentid,
                    exameventid,
                    jsonObjectSubData.getString("datetime"),
                    jsonObjectSubData.getString("datetimeupto"),
                    jsonObjectSubData.getString("subjectdesc"),
                    if (jsonObjectSubData.getString("roomcode") == "null") {
                        "-"
                    } else {
                        jsonObjectSubData.getString("roomcode")
                    },
                    if (jsonObjectSubData.getString("seatno") == "null") {
                        "-"
                    } else {
                        jsonObjectSubData.getString("seatno")
                    }
                )
            }
            dao.insertExamSchedules(registrationList)
            oldData = dao.getExamSchedules(studentid, exameventid)
            data = List(oldData.size) {
                val date = dayOfDateTime(oldData[it].datetime, oldData[it].datetimeupto)
                val bool = if (date == null) {
                    registrationList.contains(oldData[it])
                } else {
                    registrationList.contains(oldData[it]) && (date > dateNow)
                }
                ExamScheduleModel(
                    oldData[it].datetime,
                    oldData[it].datetimeupto,
                    oldData[it].subjectdesc,
                    oldData[it].roomcode,
                    oldData[it].seatno,
                    bool,
                    try {
                        date?.dayOfWeek?.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) ?: "NULL"
                    } catch (e: Exception) {
                        "NULL"
                    }
                )
            }
            return Resource.Success(data = data, true)
        } catch (e: HttpException) {
            Log.d(TAG, "attendanceRegistration: HTTP Exception : ${e.message()}")
            errorMessage = e.message()
        } catch (e: IOException) {
            Log.d(TAG, "attendanceRegistration: IO Exception : ${e.message}")
            errorMessage = e.message.toString()
        } catch (e: Exception) {
            Log.d(TAG, "attendanceRegistration: Kotlin Exception : ${e.message}")
            errorMessage = e.message.toString()
        }
        if (oldData.isNotEmpty()) {
            return Resource.Success(data = data, false)
        }
        return Resource.Error(errorMessage)
    }

    override suspend fun getSubjectsRegistrations(
        instituteid: String,
        studentid: String,
        token: String
    ): Resource<List<SubjectSemesterRegistrations>> {
        val message: String
        try {
            val payload = JSONObject(
                "{\"instituteid\":\"${instituteid}\",\"studentid\":\"${studentid}\"}\n"
            )
            val registrationData =
                api.getSubjectRegistrationList(RequestBody(payload), "Bearer $token")
            Log.d(TAG, "getMarksRegistration: $registrationData")
            val array: JSONArray =
                JSONObject(registrationData).getJSONObject("response").getJSONArray("registrations")
            val adapter =
                Moshi.Builder().build().adapter(SubjectSemesterRegistrations::class.java).lenient()
            val resultList = List(array.length()) {
                adapter.fromJson(array.getJSONObject(it).toString())!!
            }
            return Resource.Success(data = resultList, true)
        } catch (e: HttpException) {
            Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
            message = e.message() ?: ""
        } catch (e: IOException) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
        } catch (e: Exception) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
        }
        return Resource.Error(message)
    }

    override suspend fun getSubjects(
        instituteid: String,
        registrationid: String,
        studentid: String,
        token: String
    ): Resource<List<RegisteredSubject>> {
        val message: String
        try {
            val payload = JSONObject(
                "{\"instituteid\":\"${instituteid}\",\"registrationid\":\"${registrationid}\",\"studentid\":\"${studentid}\"}\n"
            )
            val registrationData =
                api.getFaculties(RequestBody(payload), "Bearer $token")
            Log.d(TAG, "getMarksRegistration: $registrationData")
            val array: JSONArray =
                JSONObject(registrationData).getJSONObject("response").getJSONArray("registrations")
            val adapter = Moshi.Builder().build().adapter(RegisteredSubject::class.java).lenient()
            val resultList = List(array.length()) {
                adapter.fromJson(array.getJSONObject(it).toString())!!
            }
            return Resource.Success(data = resultList, true)
        } catch (e: HttpException) {
            Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
            message = e.message() ?: ""
        } catch (e: IOException) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: IO Exception : ${e.message}")
        } catch (e: Exception) {
            message = e.message ?: ""
            Log.d(TAG, "loginUser: Kotlin Exception : ${e.printStackTrace()}")
        }
        return Resource.Error(message)
    }
}