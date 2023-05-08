package com.jainhardik120.jiitcompanion.ui.presentation.subjects

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainhardik120.jiitcompanion.data.remote.model.RegisteredSubject
import com.jainhardik120.jiitcompanion.domain.model.Faculty
import com.jainhardik120.jiitcompanion.domain.model.LoginInfo
import com.jainhardik120.jiitcompanion.domain.model.SubjectItem
import com.jainhardik120.jiitcompanion.domain.repository.PortalRepository
import com.jainhardik120.jiitcompanion.util.Resource
import com.jainhardik120.jiitcompanion.util.UiEvent
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    private val repository: PortalRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()  {
    companion object{
        private const val TAG = "SubjectsViewModel"
    }

    var state by mutableStateOf(SubjectsState())
    private lateinit var token: String
    private lateinit var user: LoginInfo

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun initialize() {
        viewModelScope.launch {
            token = savedStateHandle.get<String>("token") ?: return@launch
            user = Moshi.Builder().build().adapter(LoginInfo::class.java).lenient()
                .fromJson(savedStateHandle.get<String>("userInfo") ?: return@launch)!!
            var lastRegistrationId = ""
            val lastRegistrationInfo = repository.getLastAttendanceRegistration(user.enrollmentno)
            if(lastRegistrationInfo is Resource.Success){
                lastRegistrationId = lastRegistrationInfo.data?:""
            }
            if(token=="offline"){
                state=state.copy(isOffline = true)
            }else{
                withContext(Dispatchers.IO){
                    when(val result = repository.getSubjectsRegistrations(user.instituteValue, user.memberid, token)){
                        is Resource.Success->{
                            Log.d(TAG, "initialize: ${result.data.toString()}")
                            state = result.data?.let { state.copy(registrations = it) }!!
                            for (i in state.registrations) {
                                if (i.registrationid == lastRegistrationId) {
                                    state = state.copy(
                                        selectedSemesterCode = i.registrationcode,
                                        selectedSemesterId = i.registrationid
                                    )
                                    loadFaculties()
                                }
                            }
                        }
                        is Resource.Error->{
                            sendUiEvent(UiEvent.ShowSnackbar(message = result.message?:"Unknown Error Occurred"))
                        }
                    }
                }
            }
        }
    }
    
    private fun loadFaculties(){
        viewModelScope.launch { 
            when(val result = repository.getSubjects(user.instituteValue, state.selectedSemesterId, user.memberid, token)){
                is Resource.Success->{
                    Log.d(TAG, "loadFaculties: ${result.data.toString()}")
                    if(result.data!=null){
                        parseData(result.data)
                    }
                }
                is Resource.Error->{
                    sendUiEvent(UiEvent.ShowSnackbar(message = result.message?:"Unknown Error Occurred"))
                }
            }
        }
    }

    private fun parseData(data: List<RegisteredSubject>){
        var subjectId: Array<String> = emptyArray()
        for (i in data) {
            if (!subjectId.contains(i.subjectid)) {
                subjectId =
                    subjectId.plusElement(i.subjectid)
            }
        }
        val myData = List(subjectId.size){i->
            var dataItr = 0
            while (dataItr < data.size && (data[dataItr].subjectid) != subjectId[i]
            ) {
                dataItr++
            }
            val listOfFaculties : MutableList<Faculty> = mutableListOf()
            for (it in data) {
                if (it.subjectid == subjectId[i]) {
                    listOfFaculties.add(Faculty(it.employeecode, it.employeename, it.subjectcomponentcode))
                }
            }
            SubjectItem(
                audtsubject = data[dataItr].audtsubject,
                credits = data[dataItr].credits ,
                minorsubject = data[dataItr].minorsubject,
                stytype = data[dataItr].stytype,
                subjectcode = data[dataItr].subjectcode,
                subjectdesc = data[dataItr].subjectdesc,
                subjectid = data[dataItr].subjectid,
                faculties = listOfFaculties,
                remarks = data[dataItr].remarks
            )
        }
        state = state.copy(subjects = myData)
    }

    fun onEvent(event:SubjectsScreenEvent){
        when(event){
            is SubjectsScreenEvent.OnSemesterChanged->{
                state = state.copy(
                    selectedSemesterCode = event.semester.registrationcode,
                    selectedSemesterId = event.semester.registrationid
                )
                loadFaculties()
            }
        }
    }
}