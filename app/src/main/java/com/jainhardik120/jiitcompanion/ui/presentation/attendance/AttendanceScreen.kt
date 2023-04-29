package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.domain.model.AttendanceItem
import com.jainhardik120.jiitcompanion.ui.presentation.attendance.AttendanceScreenComponents.CalendarHeader
import com.jainhardik120.jiitcompanion.ui.presentation.attendance.AttendanceScreenComponents.Day
import com.jainhardik120.jiitcompanion.ui.presentation.attendance.AttendanceScreenComponents.MonthAndWeekCalendarTitle
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.getRegistrations()
    }
    var expanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var dropMenuSize by remember {
        mutableStateOf(Size.Zero)
    }
    val state = viewModel.state
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            OutlinedTextField(
                value = viewModel.state.selectedSemesterCode,
                onValueChange = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { layoutCoordinates ->
                        dropMenuSize = layoutCoordinates.size.toSize()
                    }
                    .clickable(enabled = true) {
                        expanded = !expanded
                    },
                label = { Text(text = "Semester") },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(icon, "Expand/Collapse Menu")
                    }
                }, readOnly = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { dropMenuSize.width.toDp() })
            ) {
                viewModel.state.registrations.forEach {
                    DropdownMenuItem(text = { Text(text = it.registrationcode) }, onClick = {
                        viewModel.onEvent(AttendanceScreenEvent.OnSemesterChanged(it))
                        expanded = false
                    })
                }
            }
        }

        LazyColumn {
            items(state.attendanceData.size) {
                AttendanceItem(
                    attendanceItem = state.attendanceData[it],
                    enabled = true,
                    onClick = {
                        viewModel.onEvent(AttendanceScreenEvent.OnAttendanceItemClicked(state.attendanceData[it]))
                    })
                if (it != state.attendanceData.size - 1) {
                    Divider(Modifier.padding(horizontal = 12.dp))
                }
            }
        }
    }

    if (state.isBottomSheetExpanded && state.isDetailDataReady) {
        ModalBottomSheet(onDismissRequest = {
            viewModel.onEvent(AttendanceScreenEvent.DismissBottomSheet)
        }, sheetState = bottomSheetState) {

            val adjacentMonths: Long = 500
            val currentDate = remember { LocalDate.now() }
            val currentMonth = remember(currentDate) { currentDate.yearMonth }
            val startMonth = remember(currentDate) { currentMonth.minusMonths(adjacentMonths) }
            val endMonth = remember(currentDate) { currentMonth.plusMonths(adjacentMonths) }
            val daysOfWeek = remember { daysOfWeek() }
            val context = LocalContext.current
            Column(
                modifier = Modifier,
            ) {
                val monthState = rememberCalendarState(
                    startMonth = startMonth,
                    endMonth = endMonth,
                    firstVisibleMonth = currentMonth,
                    firstDayOfWeek = daysOfWeek.first(),
                )
                CalendarTitle(
                    monthState = monthState
                )
                CalendarHeader(daysOfWeek = daysOfWeek)
                Box {
                    HorizontalCalendar(
                        state = monthState,
                        dayContent = { day ->
                            val isSelectable =
                                (day.position == DayPosition.MonthDate && state.map.containsKey(day.date))
                            val tempPair = state.map[day.date]
                            var errorPercentage = 0.0f
                            if (isSelectable && tempPair != null) {
                                errorPercentage =
                                    (tempPair.second.toFloat() / (tempPair.first + tempPair.second))
                            }
                            Day(
                                day.date,
                                isSelectable = isSelectable,
                                errorPercentage = errorPercentage,
                                isMonthDate = (day.position == DayPosition.MonthDate),
                                onClick = { clicked ->
                                    Toast.makeText(context, state.stringMap[clicked].toString(), Toast.LENGTH_LONG).show()
                                })
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarTitle(
    monthState: CalendarState
) {
    val visibleMonth = AttendanceScreenComponents.rememberFirstCompletelyVisibleMonth(monthState)
    MonthAndWeekCalendarTitle(
        currentMonth = visibleMonth.yearMonth,
        monthState = monthState
    )
}

object AttendanceScreenComponents {
    @Composable
    fun Day(
        day: LocalDate,
        isSelectable: Boolean,
        isMonthDate: Boolean,
        onClick: (LocalDate) -> Unit,
        errorPercentage: Float = 0.0f
    ) {
        val primaryColor = MaterialTheme.colorScheme.primaryContainer
        val errorColor = MaterialTheme.colorScheme.errorContainer
        val primaryPercentage = 1.0f - errorPercentage
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(6.dp)
                .clip(CircleShape)
                .drawBehind {
                    if (isSelectable) {
                        drawArc(
                            color = primaryColor,
                            startAngle = -90f,
                            sweepAngle = 360 * primaryPercentage,
                            useCenter = true
                        )
                        drawArc(
                            color = errorColor,
                            startAngle = -90f + 360 * primaryPercentage,
                            sweepAngle = 360 * errorPercentage,
                            useCenter = true
                        )
                    }
                }
                .clickable(
                    enabled = isSelectable,
                    onClick = { onClick(day) },
                ),
            contentAlignment = Alignment.Center,
        ) {
            val textColor = if (isMonthDate) {
                if (isSelectable) {
                    if (errorPercentage > 0.5f) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            } else {
                Color.Transparent
            }

            Text(
                text = day.dayOfMonth.toString(),
                color = textColor,
                fontSize = 14.sp,
            )
        }
    }

    @Composable
    fun CalendarHeader(daysOfWeek: List<DayOfWeek>) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            for (dayOfWeek in daysOfWeek) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    text = dayOfWeek.displayText(),
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }

    private val CalendarLayoutInfo.completelyVisibleMonths: List<CalendarMonth>
        get() {
            val visibleItemsInfo = this.visibleMonthsInfo.toMutableList()
            return if (visibleItemsInfo.isEmpty()) {
                emptyList()
            } else {
                val lastItem = visibleItemsInfo.last()
                val viewportSize = this.viewportEndOffset + this.viewportStartOffset
                if (lastItem.offset + lastItem.size > viewportSize) {
                    visibleItemsInfo.removeLast()
                }
                val firstItem = visibleItemsInfo.firstOrNull()
                if (firstItem != null && firstItem.offset < this.viewportStartOffset) {
                    visibleItemsInfo.removeFirst()
                }
                visibleItemsInfo.map { it.month }
            }
        }

    @Composable
    fun rememberFirstCompletelyVisibleMonth(state: CalendarState): CalendarMonth {
        val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
        LaunchedEffect(state) {
            snapshotFlow { state.layoutInfo.completelyVisibleMonths.firstOrNull() }
                .filterNotNull()
                .collect { month -> visibleMonth.value = month }
        }
        return visibleMonth.value
    }

    private fun DayOfWeek.displayText(uppercase: Boolean = false): String {
        return getDisplayName(TextStyle.SHORT, Locale.ENGLISH).let { value ->
            if (uppercase) value.uppercase(Locale.ENGLISH) else value
        }
    }

    private fun Month.displayText(short: Boolean = true): String {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        return getDisplayName(style, Locale.ENGLISH)
    }

    private fun YearMonth.displayText(short: Boolean = false): String {
        return "${this.month.displayText(short = short)} ${this.year}"
    }

    @Composable
    fun SimpleCalendarTitle(
        modifier: Modifier,
        currentMonth: YearMonth,
        goToPrevious: () -> Unit,
        goToNext: () -> Unit,
    ) {
        Row(
            modifier = modifier.height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CalendarNavigationIcon(
                icon = Icons.Filled.NavigateBefore,
                contentDescription = "Previous",
                onClick = goToPrevious,
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .testTag("MonthTitle"),
                text = currentMonth.displayText(),
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
            )
            CalendarNavigationIcon(
                icon = Icons.Filled.NavigateNext,
                contentDescription = "Next",
                onClick = goToNext,
            )
        }
    }

    @Composable
    private fun CalendarNavigationIcon(
        icon: ImageVector,
        contentDescription: String,
        onClick: () -> Unit,
    ) = Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .clip(shape = CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Icon(
            icon,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .align(Alignment.Center),
            contentDescription = contentDescription,
        )
    }

    @Composable
    fun MonthAndWeekCalendarTitle(
        currentMonth: YearMonth,
        monthState: CalendarState
    ) {
        val coroutineScope = rememberCoroutineScope()
        SimpleCalendarTitle(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            currentMonth = currentMonth,
            goToPrevious = {
                coroutineScope.launch {
                    val targetMonth = monthState.firstVisibleMonth.yearMonth.previousMonth
                    monthState.scrollToMonth(targetMonth)
                }
            },
            goToNext = {
                coroutineScope.launch {
                    val targetMonth = monthState.firstVisibleMonth.yearMonth.nextMonth
                    monthState.scrollToMonth(targetMonth)
                }
            },
        )
    }
}

//@Composable
//fun AttendanceEntryPreview() {
//    AttendanceEntryItem(
//        attendanceEntry = AttendanceEntry(
//            attendanceby = "ARADHANA NARANG",
//            classtype = "REGULAR",
//            datetime = "21/04/2023 (09:00:AM - 09:50 AM)",
//            present = "Present",
//        )
//    )
//}

//@Composable
//fun AttendanceEntryItem(attendanceEntry: AttendanceEntry) {
//    Row(Modifier.padding(8.dp)) {
//        Column(
//            Modifier
//                .weight(1f)
//                .padding(start = 8.dp)
//        ) {
//            Text(text = attendanceEntry.attendanceby)
//            Text(text = attendanceEntry.classtype)
//            Text(text = attendanceEntry.datetime)
//        }
//        Column(
//            Modifier
//                .align(Alignment.CenterVertically)
//                .padding(end = 8.dp)
//        ) {
//            Text(text = attendanceEntry.present)
//        }
//    }
//}

//
//@Composable
//fun AttendanceItemPreview() {
//    Column(Modifier.fillMaxWidth()) {
//        AttendanceItem(
//            attendanceItem = AttendanceItem(
//                subjectId = "JISUB22020000001",
//                subjectDesc = "PRINCIPLES OF MANAGEMENT(15B1NHS434)",
//                attendancePercentage = 82,
//                attendanceFractionText = "24\n----\n29",
//                attendanceDetailText = "Lecture : 58.3 \nTutorial : 75.0\n",
//                componentIdText = ArrayList(),
//                warningNumber = 37
//            ), enabled = true, onClick = {
//
//            }
//        )
//    }
//}

@Composable
fun AttendanceItem(attendanceItem: AttendanceItem, enabled: Boolean, onClick: () -> Unit) {
    Row(Modifier
        .clickable(
            enabled = enabled,
            onClick = {
                onClick()
            }
        )
        .padding(8.dp)) {
        Column(Modifier.fillMaxWidth(0.7f)) {
            Text(
                text = attendanceItem.subjectDesc.substringBefore("(", "("),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = attendanceItem.attendanceDetailText.substringBeforeLast("\n"))
        }
        Column(
            Modifier
                .fillMaxWidth(0.25f)
                .align(Alignment.CenterVertically)
        ) {
            Text(text = attendanceItem.attendanceFractionText, textAlign = TextAlign.Center)
        }
        Column(
            Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Text(text = "${attendanceItem.attendancePercentage}")
                CircularProgressIndicator(
                    (attendanceItem.attendancePercentage / 100.0).toFloat(),
                    modifier = Modifier.size(36.dp)
                )
            }
            if (attendanceItem.warningNumber > 0) {
                Text(text = "Attend: ${attendanceItem.warningNumber}")
            }
        }
    }
}