package com.jainhardik120.jiitcompanion.ui.presentation.subjects

import com.jainhardik120.jiitcompanion.data.remote.model.SubjectSemesterRegistrations

sealed class SubjectsScreenEvent{
    data class OnSemesterChanged(val semester : SubjectSemesterRegistrations): SubjectsScreenEvent()
}