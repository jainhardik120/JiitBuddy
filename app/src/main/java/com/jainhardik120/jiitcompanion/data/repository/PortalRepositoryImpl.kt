package com.jainhardik120.jiitcompanion.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.jainhardik120.jiitcompanion.data.local.PortalDatabase
import com.jainhardik120.jiitcompanion.data.local.entity.ResultEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.data.remote.PortalApi
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.util.Resource
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortalRepositoryImpl @Inject constructor(
    private val api: PortalApi,
    private val db: PortalDatabase,
    private val sharedPreferences: SharedPreferences
) : PortalRepository {
    private val TAG = "PortalRepositoryDebug"
    private val dao = db.dao

    override fun lastUser(): Resource<Pair<String, String>> {
        val data = Pair(sharedPreferences.getString("enroll", "null")!!, sharedPreferences.getString("password", "null")!!)
        return Resource.Success(data = data)
    }

    override fun loginUser(enrollmentno: String, password: String): Flow<Resource<Pair<UserEntity, String>>> =
        flow {
            emit(Resource.Loading())
            val allUsers = dao.getUserByEnrollPass(enrollmentno, password)
            var token = "offline"
            var requiredUser: UserEntity? = null
            if (allUsers.isNotEmpty()) {
                requiredUser = allUsers[0]
                emit(Resource.Loading(data = Pair<UserEntity, String>(requiredUser, token)))
            }
            try {
                var jsonObject =
                    JSONObject("{\"otppwd\":\"PWD\",\"username\":\"$enrollmentno\",\"passwordotpvalue\":\"$password\",\"Modulename\":\"STUDENTMODULE\"}")
                val regdata = api.login(RequestBody(jsonObject), "Bearer")
                with(sharedPreferences.edit()){
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
                dao.insertUser(loggedInUser)
                Log.d(TAG, "loginUser: $generalInfo")
            } catch (e: HttpException) {
                Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
                emit(
                    Resource.Error(
                        message = "Oops, something went wrong!",
                        data = Pair(requiredUser!!, token)
                    )
                )
            } catch (e: IOException) {
                Log.d(TAG, "loginUser: IO Exception : ${e.message}")
                emit(
                    Resource.Error(
                        message = "Couldn't reach server, check your internet connection.",
                        data = Pair(requiredUser!!, token)
                    )
                )
            } catch (e: Exception) {
                Log.d(TAG, "loginUser: Kotlin Exception : ${e.message}")
                emit(
                    Resource.Error(
                        message = e.message.toString(),
                        data = Pair(requiredUser!!, token)
                    )
                )
            }
            val newAllUsers = dao.getUserByEnrollPass(enrollmentno, password)
            if (newAllUsers.isNotEmpty()) {
                requiredUser = newAllUsers[0]
            }
            if(requiredUser!=null){
                emit(Resource.Success(data = Pair(requiredUser, token)))
            }
        }

    override fun getAttendanceRegistrationDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        membertype: String,
        token: String
    ): Flow<Resource<List<StudentAttendanceRegistrationEntity>>> = flow {
        emit(Resource.Loading())
        val oldData = dao.getStudentAttendanceRegistrationDetails(studentid)
        if(oldData.isNotEmpty()){
            emit(Resource.Loading(data= oldData))
        }
        try{
            val payload = JSONObject(
                "{\"clientid\":\"${clientid}\",\"instituteid\":\"${instituteid}\",\"studentid\":\"${studentid}\",\"membertype\":\"${membertype}\"}\n"
            )
            val registrationData = api.registrationForAttendance(RequestBody(payload), "Bearer $token")
            Log.d(TAG, "getAttendanceRegistrationDetails: ${registrationData}")
        
        } catch (e: HttpException) {
            Log.d(TAG, "attendanceRegistration: HTTP Exception : ${e.message()}")
            emit(
                Resource.Error(
                    message = "Oops, something went wrong!",
                    data = oldData
                )
            )
        } catch (e: IOException) {
            Log.d(TAG, "attendanceRegistration: IO Exception : ${e.message}")
            emit(
                Resource.Error(
                    message = "Couldn't reach server, check your internet connection.",
                    data = oldData
                )
            )
        } catch (e: Exception) {
            Log.d(TAG, "attendanceRegistration: Kotlin Exception : ${e.message}")
            emit(
                Resource.Error(
                    message = e.message.toString(),
                    data = oldData
                )
            )
        }
    }

    override fun getStudentResultData(
        instituteid: String,
        studentid: String,
        stynumber: Int,
        token: String
    ): Flow<Resource<List<ResultEntity>>> = flow {
        emit(Resource.Loading())
        try {
            val payload = JSONObject(
                "{\"instituteid\":\"${instituteid}\",\"studentid\":\"${studentid}\",\"stynumber\":\"${stynumber}\"}\n"
            )
            val registrationData = api.studentResultData(RequestBody(payload), "Bearer $token")
            val array = JSONObject(registrationData).getJSONObject("response").getJSONArray("semesterList")
//            var resultList:List<ResultEntity> = emptyList()
            val adapter = Moshi.Builder().build().adapter(ResultEntity::class.java).lenient()
            val resultList = List<ResultEntity>(array.length()){it->
                adapter.fromJson(array.getJSONObject(it).toString())!!
            }
            emit(Resource.Success(data = resultList))
            Log.d(TAG, "getStudentResultData: $registrationData")
        } catch (e: HttpException) {
            Log.d(TAG, "getStudentResultData: HTTP Exception : ${e.message()}")

        } catch (e: IOException) {
            Log.d(TAG, "getStudentResultData: IO Exception : ${e.message}")

        } catch (e: Exception) {
            Log.d(TAG, "getStudentResultData: Kotlin Exception : ${e.message}")
        }
    }

    private fun RequestBody(jsonObject: JSONObject): RequestBody {
        return jsonObject.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())
    }
}