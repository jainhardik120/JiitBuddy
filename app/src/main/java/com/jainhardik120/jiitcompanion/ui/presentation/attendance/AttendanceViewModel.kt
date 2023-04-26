package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.domain.model.AttendanceItem
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.util.Resource
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PortalRepository
) : ViewModel() {
    companion object {
        private const val TAG = "AttendanceViewModel"
    }

    var state by mutableStateOf(AttendanceScreenState())
    private lateinit var token: String
    private lateinit var user: UserEntity

    fun getRegistrations() {
        viewModelScope.launch(Dispatchers.IO) {
            token = savedStateHandle.get<String>("token") ?: return@launch
            user = Moshi.Builder().build().adapter(UserEntity::class.java).lenient()
                .fromJson(savedStateHandle.get<String>("userInfo") ?: return@launch)!!
            Log.d(TAG, "UserInfo: ${savedStateHandle.get<String>("userInfo")}")
            val result = repository.getAttendanceRegistrationDetails(
                user.clientid,
                user.instituteValue,
                user.memberid,
                user.membertype,
                token
            )
            when (result) {
                is Resource.Success -> {
                    state = result.data?.let { state.copy(registrations = it) }!!
                    for (i in state.registrations) {
                        Log.d(
                            TAG,
                            "AttendanceMatching: ${i.registrationid}:${user.lastAttendanceRegistrationId}"
                        )
                        if (i.registrationid == user.lastAttendanceRegistrationId) {
                            state = state.copy(
                                selectedSemesterCode = i.registrationcode,
                                selectedSemesterId = i.registrationid
                            )
                            loadAttendanceDetails()
                        }
                    }
                }

                is Resource.Error -> {
                }
            }

        }
    }


    fun onEvent(event: AttendanceScreenEvent) {
        when (event) {
            is AttendanceScreenEvent.OnSemesterChanged -> {
                state = state.copy(
                    selectedSemesterCode = event.semester.registrationcode,
                    selectedSemesterId = event.semester.registrationid
                )
                loadAttendanceDetails()
                viewModelScope.launch(Dispatchers.IO) {
                    repository.updateUserLastAttendanceRegistrationId(
                        user.enrollmentno,
                        event.semester.registrationid
                    )
                }
            }

            is AttendanceScreenEvent.OnAttendanceItemClicked -> {
                state = state.copy(isDetailDataReady = false, isBottomSheetExpanded = true)
                loadSubjectAttendanceDetails(event.attendanceItem)
            }
        }
    }

    private fun loadSubjectAttendanceDetails(attendanceItem: AttendanceItem) {
        viewModelScope.launch(Dispatchers.IO) {
            var componentIdString = ""
            for (i in attendanceItem.componentIdText) {
                if (componentIdString != "") {
                    componentIdString += ","
                }
                componentIdString += "{\"subjectcomponentid\":\"${
                    i
                }\"}"
            }
            val result = repository.getSubjectAttendanceDetails(
                user.clientid,
                user.instituteValue,
                user.memberid,
                attendanceItem.subjectId,
                state.selectedSemesterId,
                componentIdString,
                token
            )
            when (result) {
                is Resource.Success -> {
                    if(result.data!=null){
                        state = state.copy(attendanceEntries = result.data, isDetailDataReady = true)
                    }
                }
                is Resource.Error -> {

                }
            }
        }
    }

    private fun loadAttendanceDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getAttendanceDetails(
                user.clientid,
                user.instituteValue,
                user.memberid,
                1,
                state.selectedSemesterId,
                token
            )
            when (result) {
                is Resource.Success -> {
                    if (result.data != null) {
                        val attendanceData = List(result.data.size) {
                            val attendanceEntity = result.data[it]
                            var totalPres = 0
                            var totalClass = 0
                            var attendanceText = ""
                            val attendanceWarning = state.attendanceWarning
                            val componentIdText = ArrayList<String>()
                            if (attendanceEntity.Lsubjectcomponentcode == "L") {
                                try {
                                    if (attendanceEntity.Ltotalclass == 0.0) {
                                        attendanceText += "Lecture : " + "No Data\n"
                                    } else {
                                        attendanceText += "Lecture : " + attendanceEntity.Lpercentage
                                        totalPres += attendanceEntity.Ltotalpres?.toInt() ?: 0
                                        totalClass += attendanceEntity.Ltotalclass?.toInt() ?: 0
                                        attendanceEntity.Lsubjectcomponentid?.let { it1 ->
                                            componentIdText.add(
                                                it1
                                            )
                                        }
                                        attendanceText += "\n"
                                    }

                                } catch (e: Exception) {
                                    Log.d("myApp", e.message.toString())
                                }
                            }
                            if (attendanceEntity.Tsubjectcomponentcode == "T") {
                                try {
                                    if (attendanceEntity.Ttotalclass == 0.0) {
                                        attendanceText += "Tutorial : " + "No Data\n"
                                    } else {
                                        attendanceText += "Tutorial : " + attendanceEntity.Tpercentage
                                        totalPres += attendanceEntity.Ttotalpres?.toInt() ?: 0
                                        totalClass += attendanceEntity.Ttotalclass?.toInt() ?: 0
                                        attendanceEntity.Tsubjectcomponentid?.let { it1 ->
                                            componentIdText.add(
                                                it1
                                            )
                                        }
                                        attendanceText += "\n"
                                    }

                                } catch (e: Exception) {
                                    Log.d("myApp", e.message.toString())
                                }
                            }
                            val absent = attendanceEntity.abseent?.minus((totalClass - totalPres))
                                ?.toInt()
                            if (attendanceEntity.Psubjectcomponentcode == "P") {
                                try {
                                    if (attendanceEntity.Ppercentage == 100.0 || absent == 0) {
                                        attendanceText += "Practical : " + "100\n"
                                    } else {
                                        attendanceText += "Practical : " + attendanceEntity.Ppercentage
                                        totalPres +=
                                            ((100 / ((100 - attendanceEntity.Ppercentage!!) / absent!!)).roundToInt() - attendanceEntity.abseent.roundToInt())
                                        totalClass +=
                                            ((100 / ((100 - attendanceEntity.Ppercentage) / absent)).roundToInt())
                                        attendanceText += "\n"
                                    }
                                    attendanceEntity.Psubjectcomponentid?.let { it1 ->
                                        componentIdText.add(
                                            it1
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.d("myApp", e.message.toString())
                                }
                            }
                            var warningNumber =
                                ((attendanceWarning * (totalClass)) - (100 * totalPres
                                    .toDouble()
                                    .roundToInt())) / (100 - attendanceWarning)
                            if (totalClass != 0 && (totalClass + warningNumber) != 0) {
                                if (((totalPres + warningNumber)) / (totalClass + warningNumber) < attendanceWarning.toDouble()) {
                                    warningNumber++
                                }
                            }
                            val percentage = if (totalClass == 0) {
                                100
                            } else {
                                (totalPres * 100) / totalClass
                            }
                            AttendanceItem(
                                attendanceEntity.subjectid,
                                attendanceEntity.subjectcode,
                                percentage,
                                "$totalPres\n----\n$totalClass",
                                attendanceText,
                                componentIdText,
                                warningNumber
                            )
                        }
                        state = state.copy(attendanceData = attendanceData)
                    }
                    Log.d(TAG, "loadAttendanceDetails: ${result.data}")
                }

                is Resource.Error -> {
                    state = state.copy(attendanceData = listOf())
                }
            }
        }
    }
}