package com.jainhardik120.jiitcompanion.ui.presentation.grades

import com.jainhardik120.jiitcompanion.data.remote.model.ResultEntity
import com.jainhardik120.jiitcompanion.data.remote.model.ResultDetailEntity
import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration

data class GradesState(
    val results:List<ResultEntity> = emptyList(),
    val isOffline:Boolean = false,
    val isDetailDataReady: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val detailData:List<ResultDetailEntity> = emptyList(),
)