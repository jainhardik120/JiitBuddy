package com.jainhardik120.jiitcompanion.ui.presentation.grades

import com.jainhardik120.jiitcompanion.data.local.entity.ResultEntity

data class GradesState(
    val results:List<ResultEntity> = emptyList()
)