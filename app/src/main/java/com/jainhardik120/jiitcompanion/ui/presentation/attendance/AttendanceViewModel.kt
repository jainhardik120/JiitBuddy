package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.data.repository.model.AttendanceEntry
import com.jainhardik120.jiitcompanion.domain.model.AttendanceItem
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.util.Resource
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Integer.parseInt
import java.time.LocalDate
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

    init {
        viewModelScope.launch {
            val attendanceWarning = repository.getAttendanceWarning()
            state= state.copy(attendanceWarning = attendanceWarning)
        }
    }

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

            AttendanceScreenEvent.DismissBottomSheet -> {
                state = state.copy(isBottomSheetExpanded = false)
            }

            is AttendanceScreenEvent.OnDayClicked -> {
                state = state.copy(selectedDate = event.day)
            }

            is AttendanceScreenEvent.OnAttendanceWarningTextChanged -> {
                if (event.warning.isEmpty()) {
                    state = state.copy(attendanceWarning = 0)
                } else if (event.warning.toIntOrNull() != null) {
                    state = if(event.warning.toInt()<=99){
                        state.copy(attendanceWarning = event.warning.toInt())
                    }else{
                        state.copy(attendanceWarning = 99)
                    }
                }
            }
            AttendanceScreenEvent.OnKeyboardDone -> {
                repository.updateAttendanceWarning(state.attendanceWarning)
                calcWarnings()
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
                    if (result.data != null) {
                        state = state.copy(attendanceEntries = result.data)
                        convertMap()
                        state = state.copy(isDetailDataReady = true)
                    }
                }

                is Resource.Error -> {

                }
            }
        }
    }

    private fun dateTimeStringToLocalDate(dateTime: String): LocalDate {
        val calendarDate = dateTime.split(" ")[0].split("/")
        val year = parseInt(calendarDate[2])
        val month = parseInt(calendarDate[1])
        val date = parseInt(calendarDate[0])
        return LocalDate.of(year, month, date)
    }

    private fun convertMap() {
        val tempMap: MutableMap<LocalDate, Pair<Int, Int>> = mutableMapOf()
        val stringMap: MutableMap<LocalDate, MutableList<AttendanceEntry>> = mutableMapOf()
        var maxDate: LocalDate = LocalDate.of(2003, 10, 17)
        for (i in state.attendanceEntries) {
            val localDate = dateTimeStringToLocalDate(i.datetime)
            if (localDate > maxDate) {
                maxDate = localDate
            }
            if (stringMap[localDate] == null) {
                stringMap[localDate] = mutableListOf()
            }
            stringMap[localDate]?.add(i)
            if (tempMap.containsKey(localDate)) {
                val tempPair = tempMap[localDate]
                if (i.present.equals("present", ignoreCase = true)) {
                    if (tempPair != null) {
                        tempMap[localDate] = Pair(tempPair.first + 1, tempPair.second)
                    }
                } else {
                    if (tempPair != null) {
                        tempMap[localDate] = Pair(tempPair.first, tempPair.second + 1)
                    }
                }
            } else {
                if (i.present.equals("present", ignoreCase = true)) {
                    tempMap[localDate] = Pair(1, 0)
                } else {
                    tempMap[localDate] = Pair(0, 1)
                }
            }
        }
        state = state.copy(map = tempMap, stringMap = stringMap)
        if (state.attendanceEntries.isNotEmpty() && maxDate != LocalDate.of(2003, 10, 17)) {
            state = state.copy(lastAttendanceDate = maxDate, selectedDate = maxDate)
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

                            val percentage = if (totalClass == 0) {
                                100
                            } else {
                                (totalPres * 100) / totalClass
                            }

                            AttendanceItem(
                                attendanceEntity.subjectid,
                                attendanceEntity.subjectcode,
                                percentage,
                                totalClass,
                                totalPres,
                                attendanceText,
                                componentIdText
                            )
                        }
                        state = state.copy(attendanceData = attendanceData)
                        calcWarnings()
                    }
                    Log.d(TAG, "loadAttendanceDetails: ${result.data}")
                }

                is Resource.Error -> {
                    state = state.copy(attendanceData = listOf())
                }
            }
        }
    }

    private fun calcWarnings(){
        val warningNumbers=List(state.attendanceData.size){
            val attendanceWarning = state.attendanceWarning
            val totalClass = state.attendanceData[it].totalClass
            val totalPres = state.attendanceData[it].totalPres
            var warningNumber =
                ((attendanceWarning * (totalClass)) - (100 * totalPres
                    .toDouble()
                    .roundToInt())) / (100 - attendanceWarning)
            if (totalClass != 0 && (totalClass + warningNumber) != 0) {
                if (((totalPres + warningNumber)) / (totalClass + warningNumber) < attendanceWarning.toDouble()) {
                    warningNumber++
                }
            }
            warningNumber
        }
        state = state.copy(attendanceWarningNumbers = warningNumbers)
    }
}