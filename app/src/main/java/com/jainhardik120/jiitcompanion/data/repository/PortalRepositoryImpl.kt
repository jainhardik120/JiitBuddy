package com.jainhardik120.jiitcompanion.data.repository

import android.util.Log
import com.jainhardik120.jiitcompanion.core.util.Resource
import com.jainhardik120.jiitcompanion.data.local.PortalDatabase
import com.jainhardik120.jiitcompanion.data.local.UserEntity
import com.jainhardik120.jiitcompanion.data.remote.PortalApi
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
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
    private val db: PortalDatabase
) : PortalRepository {
    private val TAG = "PortalRepositoryDebug"

    private fun RequestBody(jsonObject: JSONObject): RequestBody {
        return jsonObject.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())
    }

    private val dao = db.dao
    override fun loginUser(enrollmentno: String, password: String): Flow<Resource<UserEntity>> =
        flow {
            emit(Resource.Loading())

            val allUsers = dao.getUserByEnrollPass(enrollmentno, password)
            var requiredUser : UserEntity? = null
            if (allUsers.isNotEmpty()) {
                requiredUser = allUsers[0]
                Log.d(TAG, "loginUser: ${requiredUser.toString()}")
            }
            emit(Resource.Loading(data = requiredUser))
            try{
                var jsonObject =
                    JSONObject("{\"otppwd\":\"PWD\",\"username\":\"$enrollmentno\",\"passwordotpvalue\":\"$password\",\"Modulename\":\"STUDENTMODULE\"}")
                val regdata = api.login(RequestBody(jsonObject), "Bearer")
                val loginDetails = JSONObject(regdata).getJSONObject("response").getJSONObject("regdata")
                val token = loginDetails.getString("token")
                Log.d(TAG, "login Success: ${regdata.toString()}")
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
                val generalInformation = JSONObject(generalInfo).getJSONObject("response").getJSONObject("generalinformation")
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
                Log.d(TAG, "loginUser: ${generalInfo.toString()}")
            } catch(e: HttpException){
                Log.d(TAG, "loginUser: HTTP Exception : ${e.message()}")
                emit(Resource.Error(
                    message = "Oops, something went wrong!",
                    data = requiredUser
                ))
            } catch(e: IOException){
                Log.d(TAG, "loginUser: IO Exception : ${e.message}")
                emit(Resource.Error(
                    message = "Couldn't reach server, check your internet connection.",
                    data = requiredUser
                ))
            } catch (e: Exception){
                Log.d(TAG, "loginUser: Kotlin Exception : ${e.message}")
                emit(Resource.Error(
                    message = e.message.toString(),
                    data = requiredUser
                ))
            }
            val newAllUsers = dao.getUserByEnrollPass(enrollmentno, password)
            if (newAllUsers.isNotEmpty()) {
                requiredUser = newAllUsers[0]
            }
            emit(Resource.Success(data = requiredUser))
        }

}