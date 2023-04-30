package com.jainhardik120.jiitcompanion.ui.presentation.exams

import com.jainhardik120.jiitcompanion.data.local.entity.ExamEventsEntity
import com.jainhardik120.jiitcompanion.data.local.entity.ExamRegistrationsEntity

sealed class ExamScreenEvent{
    data class OnSemesterChanged(val semester : ExamRegistrationsEntity): ExamScreenEvent()
    data class OnExamEventChanged(val event : ExamEventsEntity): ExamScreenEvent()
}
