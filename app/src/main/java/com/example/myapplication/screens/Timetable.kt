package com.example.myapplication

import android.app.TimePickerDialog
import android.content.Context
import android.util.Log // استيراد مكتبة Log من Android
import android.widget.Toast // استيراد مكتبة Toast من Android
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement // استيراد مكتبة Arrangement من compose.foundation.layout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column // استيراد مكتبة Column من compose.foundation.layout
import androidx.compose.foundation.layout.PaddingValues // استيراد مكتبة PaddingValues من compose.foundation.layout
import androidx.compose.foundation.layout.Row // استيراد مكتبة Row من compose.foundation.layout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding // استيراد مكتبة padding من compose.foundation.layout
import androidx.compose.foundation.lazy.LazyColumn // استيراد مكتبة LazyColumn من compose.foundation.lazy
import androidx.compose.foundation.lazy.items // استيراد مكتبة items من compose.foundation.lazy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons // استيراد مكتبة Icons من compose.material.icons
import androidx.compose.material.icons.filled.Delete // استيراد مكتبة Delete من compose.material.icons.filled
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button // استيراد مكتبة Button من compose.material3
import androidx.compose.material3.Icon // استيراد مكتبة Icon من compose.material3
import androidx.compose.material3.IconButton // استيراد مكتبة IconButton من compose.material3
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text // استيراد مكتبة Text من compose.material3
import androidx.compose.runtime.Composable // استيراد مكتبة Composable من compose.runtime
import androidx.compose.runtime.LaunchedEffect // استيراد مكتبة LaunchedEffect من compose.runtime
import androidx.compose.runtime.getValue // استيراد مكتبة getValue من compose.runtime
import androidx.compose.runtime.mutableStateOf // استيراد مكتبة mutableStateOf من compose.runtime
import androidx.compose.runtime.remember // استيراد مكتبة remember من compose.runtime
import androidx.compose.runtime.setValue // استيراد مكتبة setValue من compose.runtime
import androidx.compose.ui.Alignment // استيراد مكتبة Alignment من compose.ui
import androidx.compose.ui.Modifier // استيراد مكتبة Modifier من compose.ui
import androidx.compose.ui.platform.LocalContext // استيراد مكتبة LocalContext من compose.ui.platform
import androidx.compose.ui.unit.dp // استيراد مكتبة dp من compose.ui.unit
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.room.Update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

suspend fun fetchTimetable(
    classrooms: Map<String, String>,
    teachers: Map<String, String>,
    courses: Map<String, String>,
    groupId: String
): Map<String, TimetableEntry> {
    val database = FirebaseDatabase.getInstance()
    val timetableRef = database.getReference("timetable")

    val timetableSnapshot = timetableRef.get().await()
    val timetableMap = mutableMapOf<String, TimetableEntry>()
    for (timetableEntrySnapshot in timetableSnapshot.children) {
        val timetableEntry = timetableEntrySnapshot.getValue(TimetableEntry::class.java)
        if (timetableEntry != null && timetableEntry.groupId == groupId) {
            timetableEntry.classroomName = classrooms[timetableEntry.classroomId] ?: ""
            timetableEntry.teacherName = teachers[timetableEntry.teacherId] ?: ""
            timetableEntry.courseName = courses[timetableEntry.courseId] ?: ""
            timetableEntrySnapshot.key?.let { timetableMap[it] = timetableEntry }
        }
    }
    return timetableMap
}

data class TimetableEntry(
    var sessionId: String = "",
    val levelId: String? = null,
    val groupId: String? = null,
    val classroomId: String? = null,
    val teacherId: String? = null,
    val courseId: String? = null,
    val startTime: String = "",
    val endTime: String = "",
    val selectedDay: String = "",
    val date: String = "",
    var classroomName: String = "",
    var teacherName: String = "",
    var courseName: String = "",
    var levelName: String = "", // New field
    var groupName: String = ""  // New field
)

@Composable
fun TimetableScreen(innerPadding: PaddingValues) {
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(innerPadding)) {
        IconButton(onClick = { showDialog = true }) {
            Icon(Icons.Default.AddCircle, contentDescription = "Add Timetable Entry")
        }

        if (showDialog) {
            LevelGroupSelectionDialog(
                onDismiss = { showDialog = false },
                onConfirm = { levelId, groupId, classroomId, teacherId, courseId, startTime, endTime, selectedDay ->
                    scope.launch {
                        saveTimetable(
                            levelId,
                            groupId,
                            classroomId,
                            teacherId,
                            courseId,
                            startTime,
                            endTime,
                            selectedDay
                        )
                        showDialog = false
                    }
                }
            )
        }

        LevelGroupTimetable()
    }
}

@Composable
fun LevelGroupTimetable() {
    var levels by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedLevelId by remember { mutableStateOf<String?>(null) }
    var groups by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var timetable by remember { mutableStateOf(mapOf<String, TimetableEntry>()) }
    var classrooms by remember { mutableStateOf(mapOf<String, String>()) }
    var teachers by remember { mutableStateOf(mapOf<String, String>()) }
    var courses by remember { mutableStateOf(mapOf<String, String>()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        levels = fetchLevels()
        classrooms = fetchClassrooms()
        teachers = fetchTeachers()
        courses = fetchCourses()
    }

    LaunchedEffect(selectedLevelId) {
        if (selectedLevelId != null) {
            scope.launch {
                fetchGroupsForLevel(selectedLevelId!!) { fetchedGroups ->
                    groups = fetchedGroups
                }
            }
        } else {
            groups = emptyMap()
        }
    }

    LaunchedEffect(selectedGroupId) {
        if (selectedGroupId != null) {
            scope.launch {
                timetable = fetchTimetable(classrooms, teachers, courses, selectedGroupId!!)
            }
        } else {
            timetable = emptyMap()
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        DropdownMenuSelection(
            label = "Select Level",
            options = levels,
            selectedOptionId = selectedLevelId
        ) { levelId ->
            selectedLevelId = levelId
            selectedGroupId = null // Reset group selection when level changes
        }

        if (selectedLevelId != null) {
            DropdownMenuSelection(
                label = "Select Group",
                options = groups,
                selectedOptionId = selectedGroupId
            ) { groupId ->
                selectedGroupId = groupId
            }
        }

        if (selectedGroupId != null) {
            GroupTimetable(selectedGroupId!!, timetable)
        }
    }
}

@Composable
fun GroupTimetable(groupId: String, timetable: Map<String, TimetableEntry>) {
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    val scrollStateHorizontal = rememberScrollState()
    val scrollStateVertical = rememberScrollState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(8.dp).verticalScroll(scrollStateVertical)) {
        // Header Row
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).horizontalScroll(scrollStateHorizontal)) {
            Text(text = "Time", style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(100.dp))
            daysOfWeek.forEach { day ->
                Text(text = day, style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(200.dp))
            }
        }

        // Divider after header row
        Divider()

        // Find all unique time slots
        val timeSlots = timetable.values.map { it.startTime to it.endTime }.distinct().sortedBy { it.first }

        // For each time slot, create a row with classes scheduled in that time slot
        timeSlots.forEach { (startTime, endTime) ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).horizontalScroll(scrollStateHorizontal)) {
                Text(text = "$startTime - $endTime", modifier = Modifier.width(100.dp))

                daysOfWeek.forEach { day ->
                    val dayTimetable = timetable.filter { it.value.groupId == groupId && it.value.selectedDay == day && it.value.startTime == startTime && it.value.endTime == endTime }
                    Column(modifier = Modifier.width(200.dp).padding(4.dp)) {
                        if (dayTimetable.isNotEmpty()) {
                            dayTimetable.forEach { (entryId, entry) ->
                                Column {
                                    Text(text = entry.courseName)
                                    Text(text = "${entry.classroomName}")
                                    Text(text = "${entry.teacherName}")
                                    IconButton(onClick = {
                                        scope.launch {
                                            deleteTimetableEntry(entryId)
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Timetable Entry")
                                    }
                                }
                            }
                        } else {
                            Text(text = "/")
                        }
                    }
                }
            }
            // Divider between rows
            Divider()
        }
    }
}

@Composable
fun LevelGroupSelectionDialog(
    onDismiss: () -> Unit,
    onConfirm: suspend (String?, String?, String?, String?, String?, String, String, String) -> Unit
) {
    var selectedLevelId by remember { mutableStateOf<String?>(null) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var selectedClassroomId by remember { mutableStateOf<String?>(null) }
    var selectedTeacherId by remember { mutableStateOf<String?>(null) }
    var selectedCourseId by remember { mutableStateOf<String?>(null) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf<String?>(null) }

    var levels by remember { mutableStateOf(mapOf<String, String>()) }
    var groups by remember { mutableStateOf(mapOf<String, String>()) }
    var classrooms by remember { mutableStateOf(mapOf<String, String>()) }
    var teachers by remember { mutableStateOf(mapOf<String, String>()) }
    var courses by remember { mutableStateOf(mapOf<String, String>()) }
    var availableClassrooms by remember { mutableStateOf(mapOf<String, String>()) }
    var availableTeachers by remember { mutableStateOf(mapOf<String, String>()) }
    var availableGroups by remember { mutableStateOf(mapOf<String, String>()) }
    var availableTimeSlots by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        levels = fetchLevels()
        classrooms = fetchClassrooms()
        teachers = fetchTeachers()
        courses = fetchCourses()
    }

    LaunchedEffect(selectedLevelId) {
        if (selectedLevelId != null) {
            scope.launch {
                fetchGroupsForLevel(selectedLevelId!!) { fetchedGroups ->
                    groups = fetchedGroups
                }
            }
        } else {
            groups = emptyMap()
        }
    }

    LaunchedEffect(selectedDay) {
        if (selectedDay != null && selectedGroupId != null) {
            scope.launch {
                val timetable = fetchTimetableForDay(selectedDay!!, selectedGroupId!!)
                val timeSlots = generateAvailableTimeSlots(timetable)
                availableTimeSlots = timeSlots
            }
        } else {
            availableTimeSlots = emptyList()
        }
    }

    LaunchedEffect(selectedCourseId) {
        if (selectedCourseId != null) {
            fetchTeachersForCourse(selectedCourseId!!) { fetchedTeachers ->
                availableTeachers = fetchedTeachers
            }
        } else {
            availableTeachers = emptyMap()
        }
    }

    LaunchedEffect(selectedDay, startTime, endTime) {
        if (selectedDay != null && startTime.isNotEmpty() && endTime.isNotEmpty()) {
            scope.launch {
                val unavailableResources = fetchUnavailableResources(selectedDay!!, startTime, endTime)
                availableClassrooms = classrooms.filter { it.key !in unavailableResources.classrooms }
                availableGroups = groups.filter { it.key !in unavailableResources.groups }
                availableTeachers = availableTeachers.filter { it.key !in unavailableResources.teachers }
            }
        }
    }

    val daysOfWeek = mapOf(
        "Monday" to "Monday",
        "Tuesday" to "Tuesday",
        "Wednesday" to "Wednesday",
        "Thursday" to "Thursday",
        "Friday" to "Friday",
        "Saturday" to "Saturday",
        "Sunday" to "Sunday"
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Select Timetable Details") },
        text = {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DropdownMenuSelection(
                    label = "Select Level",
                    options = levels,
                    selectedOptionId = selectedLevelId
                ) { levelId ->
                    selectedLevelId = levelId
                    selectedGroupId = null // Reset group selection when level changes
                }

                if (selectedLevelId != null) {
                    DropdownMenuSelection(
                        label = "Select Group",
                        options = groups,
                        selectedOptionId = selectedGroupId
                    ) { groupId ->
                        selectedGroupId = groupId
                    }
                }

                if (selectedGroupId != null) {
                    DropdownMenuSelection(
                        label = "Select Day",
                        options = daysOfWeek,
                        selectedOptionId = selectedDay
                    ) { day ->
                        selectedDay = day
                    }
                }

                if (selectedDay != null) {
                    DropdownMenuTimeSelection(
                        label = "Select Time Slot",
                        options = availableTimeSlots,
                        selectedStartTime = startTime,
                        selectedEndTime = endTime
                    ) { start, end ->
                        startTime = start
                        endTime = end
                    }
                }

                DropdownMenuSelection(
                    label = "Select Classroom",
                    options = availableClassrooms,
                    selectedOptionId = selectedClassroomId
                ) { classroomId ->
                    selectedClassroomId = classroomId
                }

                DropdownMenuSelection(
                    label = "Select Course",
                    options = courses,
                    selectedOptionId = selectedCourseId
                ) { courseId ->
                    selectedCourseId = courseId
                }

                if (selectedCourseId != null) {
                    DropdownMenuSelection(
                        label = "Select Teacher",
                        options = availableTeachers,
                        selectedOptionId = selectedTeacherId
                    ) { teacherId ->
                        selectedTeacherId = teacherId
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                scope.launch {
                    onConfirm(
                        selectedLevelId,
                        selectedGroupId,
                        selectedClassroomId,
                        selectedTeacherId,
                        selectedCourseId,
                        startTime,
                        endTime,
                        selectedDay ?: ""
                    )
                }
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DropdownMenuSelection(
    label: String,
    options: Map<String, String>,
    selectedOptionId: String?,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[selectedOptionId]) }

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = selectedOptionText ?: "",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, Modifier.clickable { expanded = true })
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { (key, value) ->
                DropdownMenuItem(onClick = {
                    selectedOptionText = value
                    onOptionSelected(key)
                    expanded = false
                }, text = {
                    Text(text = value)
                })
            }
        }
    }
}

@Composable
fun DropdownMenuTimeSelection(
    label: String,
    options: List<Pair<String, String>>,
    selectedStartTime: String?,
    selectedEndTime: String?,
    onOptionSelected: (String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("$selectedStartTime - $selectedEndTime") }

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = selectedOptionText ?: "",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, Modifier.clickable { expanded = true })
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { (start, end) ->
                DropdownMenuItem(onClick = {
                    selectedOptionText = "$start - $end"
                    onOptionSelected(start, end)
                    expanded = false
                }, text = {
                    Text(text = "$start - $end")
                })
            }
        }
    }
}

suspend fun fetchClassrooms(): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val classroomsRef = database.getReference("classrooms")

    val classroomsSnapshot = classroomsRef.get().await()
    val classroomsMap = mutableMapOf<String, String>()
    for (classroomSnapshot in classroomsSnapshot.children) {
        val classroomId = classroomSnapshot.key
        val classroomName = classroomSnapshot.child("name").getValue(String::class.java)
        if (classroomId != null && classroomName != null) {
            classroomsMap[classroomId] = classroomName
        }
    }
    return classroomsMap
}

suspend fun fetchTeachers(): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val teachersRef = database.getReference("users/teachers")
    val teachersSnapshot = teachersRef.get().await()
    val teachersMap = mutableMapOf<String, String>()
    for (teacherSnapshot in teachersSnapshot.children) {
        val teacherId = teacherSnapshot.key
        val teacherName = teacherSnapshot.child("name").getValue(String::class.java)
        val teacherFamilyName = teacherSnapshot.child("familyname").getValue(String::class.java)
        if (teacherId != null && teacherName != null && teacherFamilyName != null) {
            teachersMap[teacherId] = "$teacherFamilyName $teacherName"
        }
    }
    return teachersMap
}

suspend fun fetchTeachersForCourse(courseId: String, onTeachersFetched: (Map<String, String>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val teachersRef = database.getReference("users/teachers")
    val teachersSnapshot = teachersRef.get().await()
    val teachersMap = mutableMapOf<String, String>()
    for (teacherSnapshot in teachersSnapshot.children) {
        val teacherId = teacherSnapshot.key
        val teacherName = teacherSnapshot.child("name").getValue(String::class.java)
        val teacherFamilyName = teacherSnapshot.child("familyname").getValue(String::class.java)
        val courses = teacherSnapshot.child("courses").children.mapNotNull { it.getValue(String::class.java) }
        if (teacherId != null && teacherName != null && teacherFamilyName != null && courses.contains(courseId)) {
            teachersMap[teacherId] = "$teacherFamilyName $teacherName"
        }
    }
    onTeachersFetched(teachersMap)
}

suspend fun fetchCourses(): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val coursesRef = database.getReference("courses")


    val coursesSnapshot = coursesRef.get().await()
    val coursesMap = mutableMapOf<String, String>()
    for (courseSnapshot in coursesSnapshot.children) {
        val courseId = courseSnapshot.key
        val courseName = courseSnapshot.child("name").getValue(String::class.java)
        if (courseId != null && courseName != null) {
            coursesMap[courseId] = courseName
        }
    }
    return coursesMap
}

suspend fun fetchLevelsa(): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val levelsRef = database.getReference("levels")

    val levelsSnapshot = levelsRef.get().await()
    val levelsMap = mutableMapOf<String, String>()
    for (levelSnapshot in levelsSnapshot.children) {
        val levelId = levelSnapshot.key
        val levelName = levelSnapshot.child("name").getValue(String::class.java)
        if (levelId != null && levelName != null) {
            levelsMap[levelId] = levelName
        }
    }
    return levelsMap
}

suspend fun fetchGroupsForLevela(levelId: String, onGroupsFetched: (Map<String, String>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val groupsRef = database.getReference("levels/$levelId/groups")

    val groupsSnapshot = groupsRef.get().await()
    val groupsMap = mutableMapOf<String, String>()
    for (groupSnapshot in groupsSnapshot.children) {
        val groupId = groupSnapshot.key
        val groupName = groupSnapshot.child("name").getValue(String::class.java)
        if (groupId != null && groupName != null) {
            groupsMap[groupId] = groupName
        }
    }
    onGroupsFetched(groupsMap)
}

suspend fun fetchTimetableForDay(day: String, groupId: String): List<TimetableEntry> {
    val database = FirebaseDatabase.getInstance()
    val timetableRef = database.getReference("timetable")

    val timetableSnapshot = timetableRef.get().await()
    val timetableList = mutableListOf<TimetableEntry>()
    for (timetableEntrySnapshot in timetableSnapshot.children) {
        val timetableEntry = timetableEntrySnapshot.getValue(TimetableEntry::class.java)
        if (timetableEntry != null && timetableEntry.selectedDay == day && timetableEntry.groupId == groupId) {
            timetableList.add(timetableEntry)
        }
    }
    return timetableList
}



fun generateAvailableTimeSlots(timetable: List<TimetableEntry>): List<Pair<String, String>> {
    val allTimeSlots = listOf(
        "08:00" to "09:00", "09:00" to "10:00", "10:00" to "11:00", "11:00" to "12:00",
        "12:00" to "13:00", "13:00" to "14:00", "14:00" to "15:00", "15:00" to "16:00",
        "16:00" to "17:00", "17:00" to "18:00"
    )
    val occupiedTimeSlots = timetable.map { it.startTime to it.endTime }
    return allTimeSlots.filter { it !in occupiedTimeSlots }
}

data class UnavailableResources(
    val classrooms: Set<String>,
    val groups: Set<String>,
    val teachers: Set<String>
)

suspend fun fetchUnavailableResources(day: String, startTime: String, endTime: String): UnavailableResources {
    val database = FirebaseDatabase.getInstance()
    val timetableRef = database.getReference("timetable")

    val timetableSnapshot = timetableRef.get().await()
    val occupiedClassrooms = mutableSetOf<String>()
    val occupiedGroups = mutableSetOf<String>()
    val occupiedTeachers = mutableSetOf<String>()

    for (timetableEntrySnapshot in timetableSnapshot.children) {
        val entry = timetableEntrySnapshot.getValue(TimetableEntry::class.java)
        if (entry != null && entry.selectedDay == day) {
            val isOverlapping = (startTime < entry.endTime && endTime > entry.startTime)
            if (isOverlapping) {
                occupiedClassrooms.add(entry.classroomId ?: "")
                occupiedGroups.add(entry.groupId ?: "")
                occupiedTeachers.add(entry.teacherId ?: "")
            }
        }
    }
    return UnavailableResources(occupiedClassrooms, occupiedGroups, occupiedTeachers)
}

fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(context, { _, selectedHour, selectedMinute ->
        val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
        onTimeSelected(formattedTime)
    }, hour, minute, true)

    timePickerDialog.show()
}

suspend fun saveTimetable(
    levelId: String?,
    groupId: String?,
    classroomId: String?,
    teacherId: String?,
    courseId: String?,
    startTime: String,
    endTime: String,
    selectedDay: String
) {
    val database = FirebaseDatabase.getInstance()
    val timetableRef = database.getReference("timetable").push()

    val timetableEntry = mapOf(
        "levelId" to levelId,
        "groupId" to groupId,
        "classroomId" to classroomId,
        "teacherId" to teacherId,
        "courseId" to courseId,
        "startTime" to startTime,
        "endTime" to endTime,
        "selectedDay" to selectedDay
    )

    timetableRef.setValue(timetableEntry).await()
}



suspend fun deleteTimetableEntry(entryId: String) {
    val database = FirebaseDatabase.getInstance()
    val timetableRef = database.getReference("timetable").child(entryId)
    timetableRef.removeValue().await()
}


@Composable
fun CurrentSessionPage(teacherId: String) {
    var timetable by remember { mutableStateOf(mapOf<String, TimetableEntry>()) }
    var classrooms by remember { mutableStateOf(mapOf<String, String>()) }
    var courses by remember { mutableStateOf(mapOf<String, String>()) }
    var levels by remember { mutableStateOf(mapOf<String, String>()) }
    var groups by remember { mutableStateOf(mapOf<String, String>()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(teacherId) {
        scope.launch {
            classrooms = fetchClassrooms()
            courses = fetchCourses()
            levels = fetchLevels()
            groups = fetchAllGroups()
            timetable = fetchTeacherTimetable(classrooms, courses, levels, groups, teacherId)
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        if (timetable.isNotEmpty()) {
            CurrentSessionScreen(timetable)
        } else {
            Text(text = "No timetable available for this teacher.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun DisplayGroupTimetable(groupId: String) {
    var timetable by remember { mutableStateOf(mapOf<String, TimetableEntry>()) }
    var classrooms by remember { mutableStateOf(mapOf<String, String>()) }
    var teachers by remember { mutableStateOf(mapOf<String, String>()) }
    var courses by remember { mutableStateOf(mapOf<String, String>()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(groupId) {
        scope.launch {
            classrooms = fetchClassrooms()
            teachers = fetchTeachers()
            courses = fetchCourses()
            timetable = fetchTimetable(classrooms, teachers, courses, groupId)
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        if (timetable.isNotEmpty()) {
            GroupTimetable(groupId, timetable)
        } else {
            Text(text = "No timetable available for this group.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
@Composable
fun DisplayTeacherTimetable(teacherId: String) {
    var timetable by remember { mutableStateOf(mapOf<String, TimetableEntry>()) }
    var classrooms by remember { mutableStateOf(mapOf<String, String>()) }
    var courses by remember { mutableStateOf(mapOf<String, String>()) }
    var levels by remember { mutableStateOf(mapOf<String, String>()) }
    var groups by remember { mutableStateOf(mapOf<String, String>()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(teacherId) {
        scope.launch {
            classrooms = fetchClassrooms()
            courses = fetchCourses()
            levels = fetchLevels()
            groups = fetchAllGroups()
            timetable = fetchTeacherTimetable(classrooms, courses, levels, groups, teacherId)
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        if (timetable.isNotEmpty()) {
            TeacherScreen(timetable)
        } else {
            Text(text = "No timetable available for this teacher.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
@Composable
fun TeacherScreen(timetable: Map<String, TimetableEntry>) {
    Column {
        Text("Full Timetable", style = MaterialTheme.typography.labelMedium)
        TimetableScreen(timetable)

    }
}


suspend fun fetchStudentsForGroup(groupId: String): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val studentsRef = database.getReference("users/students")

    val studentsSnapshot = studentsRef.get().await()
    val studentsMap = mutableMapOf<String, String>()
    for (studentSnapshot in studentsSnapshot.children) {
        val studentId = studentSnapshot.key
        val studentGroupId = studentSnapshot.child("groupId").getValue(String::class.java)
        val studentName = studentSnapshot.child("name").getValue(String::class.java)
        val studentFamilyName = studentSnapshot.child("familyname").getValue(String::class.java)
        if (studentId != null && studentGroupId == groupId && studentName != null) {
            studentsMap[studentId] = studentFamilyName+" "+studentName
        }
    }
    return studentsMap
}






suspend fun fetchTeacherTimetable(classrooms: Map<String, String>, courses: Map<String, String>, levels: Map<String, String>, groups: Map<String, String>, teacherId: String): Map<String, TimetableEntry> {
    val database = FirebaseDatabase.getInstance()
    val timetableRef = database.getReference("timetable")

    val timetableSnapshot = timetableRef.get().await()
    val timetableMap = mutableMapOf<String, TimetableEntry>()
    for (timetableEntrySnapshot in timetableSnapshot.children) {
        val timetableEntry = timetableEntrySnapshot.getValue(TimetableEntry::class.java)
        if (timetableEntry != null && timetableEntry.teacherId == teacherId) {
            timetableEntry.classroomName = classrooms[timetableEntry.classroomId] ?: ""
            timetableEntry.courseName = courses[timetableEntry.courseId] ?: ""
            timetableEntry.levelName = levels[timetableEntry.levelId] ?: ""
            timetableEntry.groupName = groups[timetableEntry.groupId] ?: ""
            timetableEntrySnapshot.key?.let { timetableEntry.sessionId = it } // Ensure session ID is set
            timetableEntrySnapshot.key?.let { timetableMap[it] = timetableEntry }
            Log.d("fetchTeacherTimetable", "Timetable Entry Added: $timetableEntry")
        }
    }
    Log.d("fetchTeacherTimetable", "Total Entries Fetched: ${timetableMap.size}")
    return timetableMap
}



suspend fun fetchAllGroups(): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val groupsRef = database.getReference("levels")

    val groupsSnapshot = groupsRef.get().await()
    val groupsMap = mutableMapOf<String, String>()
    for (levelSnapshot in groupsSnapshot.children) {
        val levelId = levelSnapshot.key
        val groups = levelSnapshot.child("groups").children
        for (groupSnapshot in groups) {
            val groupId = groupSnapshot.key
            val groupName = groupSnapshot.child("name").getValue(String::class.java)
            if (groupId != null && groupName != null) {
                groupsMap[groupId] = groupName
            }
        }
    }
    return groupsMap
}


/********************************************************************************************/
suspend fun saveAttendance(
    timetableId: String,
    attendanceData: Map<String, String>,
    date: String
) {
    val database = FirebaseDatabase.getInstance()
    val attendanceRef = database.getReference("attendance").child(timetableId).child(date)

    val attendanceEntries = attendanceData.mapValues { (_, status) ->
        mapOf(
            "status" to status,

        )
    }

    attendanceRef.setValue(attendanceEntries).await()
}


@Composable
fun StudentDialog(timetableId: String, todayDate: String, students: Map<String, String>, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var selectedStatus by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("List of Students") },
        text = {
            LazyColumn {
                items(students.toList()) { (studentId, studentName) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {
                        Text(text = studentName, modifier = Modifier.weight(1f))
                        DropdownMenu(
                            expanded = selectedStatus != null,
                            onDismissRequest = { selectedStatus = null },
                            content = {
                                DropdownMenuItem(onClick = {
                                    selectedStatus = "Present"
                                    scope.launch {

                                    }
                                    selectedStatus = null
                                }, text =  {
                                    Text("Present")
                                })
                                DropdownMenuItem(onClick = {
                                    selectedStatus = "Absent"
                                    scope.launch {

                                    }
                                    selectedStatus = null
                                }, text =  {
                                    Text("Absent")
                                })
                                DropdownMenuItem(onClick = {
                                    selectedStatus = "Late"
                                    scope.launch {

                                    }
                                    selectedStatus = null
                                }, text = {
                                    Text("Late")
                                })
                            }
                        )
                        IconButton(onClick = { selectedStatus = studentId }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Status")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Close")
            }
        }
    )
}

@Composable
fun TeacherTimetable(timetable: Map<String, TimetableEntry>) {
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    val scrollStateHorizontal = rememberScrollState()
    val scrollStateVertical = rememberScrollState()
    var showStudentDialog by remember { mutableStateOf(false) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var selectedTimetableId by remember { mutableStateOf<String?>(null) }
    var students by remember { mutableStateOf(mapOf<String, String>()) }
    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(8.dp).verticalScroll(scrollStateVertical)) {
        // Header Row
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).horizontalScroll(scrollStateHorizontal)) {
            Text(text = "Time", style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(100.dp))
            daysOfWeek.forEach { day ->
                Text(text = day, style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(200.dp))
            }
        }

        // Divider after header row
        Divider()

        // Find all unique time slots
        val timeSlots = timetable.values.map { it.startTime to it.endTime }.distinct().sortedBy { it.first }

        // For each time slot, create a row with classes scheduled in that time slot
        timeSlots.forEach { (startTime, endTime) ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).horizontalScroll(scrollStateHorizontal)) {
                Text(text = "$startTime - $endTime", modifier = Modifier.width(100.dp))

                daysOfWeek.forEach { day ->
                    val dayTimetable = timetable.filter { it.value.selectedDay == day && it.value.startTime == startTime && it.value.endTime == endTime }
                    Column(modifier = Modifier.width(200.dp).padding(4.dp)) {
                        if (dayTimetable.isNotEmpty()) {
                            dayTimetable.forEach { (timetableId, entry) ->
                                Column(modifier = Modifier.clickable {
                                    selectedGroupId = entry.groupId
                                    selectedTimetableId = timetableId
                                    showStudentDialog = true
                                }) {
                                    Text(text = " ${entry.courseName}")
                                    Text(text = " ${entry.classroomName}")
                                    Text(text = " ${entry.levelName}")
                                    Text(text = " ${entry.groupName}")
                                }
                            }
                        } else {
                            Text(text = "/")
                        }
                    }
                }
            }
            // Divider between rows
            Divider()
        }
    }

    if (showStudentDialog && selectedGroupId != null && selectedTimetableId != null) {
        LaunchedEffect(selectedGroupId) {
            students = fetchStudentsForGroup(selectedGroupId!!)
        }

        StudentDialog(
            timetableId = selectedTimetableId!!,
            todayDate = todayDate,
            students = students,
            onDismiss = { showStudentDialog = false }
        )
    }
}
/**********************************************************************************************************/








@Composable
fun TimetableScreen(timetable: Map<String, TimetableEntry>) {
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    val scrollStateHorizontal = rememberScrollState()
    val scrollStateVertical = rememberScrollState()
    var showStudentDialog by remember { mutableStateOf(false) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var selectedTimetableId by remember { mutableStateOf<String?>(null) }
    var students by remember { mutableStateOf(mapOf<String, String>()) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(8.dp).verticalScroll(scrollStateVertical)) {
        // Header Row
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).horizontalScroll(scrollStateHorizontal)) {
            Text(text = "Time", style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(100.dp))
            daysOfWeek.forEach { day ->
                Text(text = day, style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(200.dp))
            }
        }

        // Divider after header row
        Divider()

        // Find all unique time slots
        val timeSlots = timetable.values.map { it.startTime to it.endTime }.distinct().sortedBy { it.first }

        // For each time slot, create a row with classes scheduled in that time slot
        timeSlots.forEach { (startTime, endTime) ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).horizontalScroll(scrollStateHorizontal)) {
                Text(text = "$startTime - $endTime", modifier = Modifier.width(100.dp))

                daysOfWeek.forEach { day ->
                    val dayTimetable = timetable.filter { it.value.selectedDay == day && it.value.startTime == startTime && it.value.endTime == endTime }
                    Column(modifier = Modifier.width(200.dp).padding(4.dp)) {
                        if (dayTimetable.isNotEmpty()) {
                            dayTimetable.forEach { (timetableId, entry) ->
                                Column(modifier = Modifier.clickable {
                                    selectedGroupId = entry.groupId
                                    selectedTimetableId = timetableId
                                    showStudentDialog = true
                                }) {
                                    Text(text = " ${entry.levelName}")
                                    Text(text = " ${entry.groupName}")
                                    Text(text = " ${entry.courseName}")
                                    Text(text = " ${entry.classroomName}")

                                }
                            }
                        } else {
                            Text(text = "/")
                        }
                    }
                }
            }
            // Divider between rows
            Divider()
        }
    }

    if (showStudentDialog && selectedGroupId != null && selectedTimetableId != null) {
        LaunchedEffect(selectedGroupId) {
            students = fetchStudentsForGroup(selectedGroupId!!)
        }

        StudentListDialog(
            students = students,
            onDismiss = { showStudentDialog = false }
        )
    }
}
@Composable
fun StudentListDialog(students: Map<String, String>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("List of Students") },
        text = {
            LazyColumn {
                items(students.toList()) { (_, studentName) ->
                    Text(text = studentName, modifier = Modifier.padding(8.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Close")
            }
        }
    )
}
fun getCurrentTimetableEntry(timetable: Map<String, TimetableEntry>): TimetableEntry? {
    val currentTime = Calendar.getInstance()
    val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
    val currentMinute = currentTime.get(Calendar.MINUTE)
    val currentDay = SimpleDateFormat("EEEE", Locale.getDefault()).format(currentTime.time) // Get current day

    return timetable.values.firstOrNull { entry ->
        val startHour = entry.startTime.split(":")[0].toInt()
        val startMinute = entry.startTime.split(":")[1].toInt()
        val endHour = entry.endTime.split(":")[0].toInt()
        val endMinute = entry.endTime.split(":")[1].toInt()

        val isToday = entry.selectedDay.equals(currentDay, ignoreCase = true)
        val isCurrentTime =
            (currentHour > startHour || (currentHour == startHour && currentMinute >= startMinute)) &&
                    (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute))

        isToday && isCurrentTime
    }
}


@Composable
fun CurrentSessionScreen(timetable: Map<String, TimetableEntry>) {
    val currentTimetableEntry = getCurrentTimetableEntry(timetable)
    var showStudentDialog by remember { mutableStateOf(false) }
    var students by remember { mutableStateOf(mapOf<String, String>()) }
    var attendance by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        if (currentTimetableEntry != null) {
            LaunchedEffect(currentTimetableEntry.groupId, currentTimetableEntry.sessionId) {
                students = fetchStudentsForGroup(currentTimetableEntry.groupId!!)
                attendance = fetchAttendance(currentTimetableEntry.sessionId, todayDate)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { showStudentDialog = true },

            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Current cours", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Time: ${currentTimetableEntry.startTime} - ${currentTimetableEntry.endTime}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Level: ${currentTimetableEntry.levelName}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Group: ${currentTimetableEntry.groupName}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Course: ${currentTimetableEntry.courseName}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Classroom: ${currentTimetableEntry.classroomName}", style = MaterialTheme.typography.titleMedium)
                }
            }

            DisplaySavedAttendance(
                timetableId = currentTimetableEntry.sessionId,
                date = todayDate,
                students = students,
                attendance = attendance
            )
        } else {
            Text(
                text = "No ongoing session right now.",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    if (showStudentDialog && currentTimetableEntry != null) {
        StudentAttendanceDialog(
            timetableId = currentTimetableEntry.sessionId,
            todayDate = todayDate,
            students = students,
            onDismiss = {
                scope.launch {
                    attendance = fetchAttendance(currentTimetableEntry.sessionId, todayDate)
                }
                showStudentDialog = false
            }
        )
    }
}




@Composable
fun StudentAttendanceDialog(
    timetableId: String,
    todayDate: String,
    students: Map<String, String>,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val attendanceStatus = remember { mutableStateOf(students.keys.associateWith { "" }) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Record Attendance") },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        attendanceStatus.value = attendanceStatus.value.keys.associateWith { "Present" }
                    }) {
                        Text("All Present")
                    }
                    Button(onClick = {
                        attendanceStatus.value = attendanceStatus.value.keys.associateWith { "Absent" }
                    }) {
                        Text("All Absent")
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Student Name", modifier = Modifier.weight(1f))
                    Text(text = "Present", modifier = Modifier.weight(1f))
                    Text(text = "Absent", modifier = Modifier.weight(1f))
                    Text(text = "Late", modifier = Modifier.weight(1f))
                }

                Divider()

                LazyColumn {
                    items(students.toList()) { (studentId, studentName) ->
                        val status = attendanceStatus.value[studentId] ?: ""

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = studentName, modifier = Modifier.weight(1f))

                            RadioButton(
                                selected = status == "Present",
                                onClick = {
                                    attendanceStatus.value = attendanceStatus.value.toMutableMap().apply {
                                        put(studentId, "Present")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            RadioButton(
                                selected = status == "Absent",
                                onClick = {
                                    attendanceStatus.value = attendanceStatus.value.toMutableMap().apply {
                                        put(studentId, "Absent")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            RadioButton(
                                selected = status == "Late",
                                onClick = {
                                    attendanceStatus.value = attendanceStatus.value.toMutableMap().apply {
                                        put(studentId, "Late")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                scope.launch {
                    saveAttendance(timetableId, attendanceStatus.value, todayDate)
                }
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}


/******************************************************/
@Composable
fun DisplaySavedAttendance(
    timetableId: String,
    date: String,
    students: Map<String, String>,
    attendance: Map<String, String>
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Attendance for $date", style = MaterialTheme.typography.titleLarge)
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn {
            items(students.toList()) { (studentId, studentName) ->
                val status = attendance[studentId] ?: "Not Recorded"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),

                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = studentName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                        Text(text = status, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}


suspend fun fetchAttendance(timetableId: String, date: String): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val attendanceRef = database.getReference("attendance").child(timetableId).child(date)

    val attendanceSnapshot = attendanceRef.get().await()
    val attendanceMap = mutableMapOf<String, String>()
    for (attendanceEntry in attendanceSnapshot.children) {
        val studentId = attendanceEntry.key
        val status = attendanceEntry.child("status").getValue(String::class.java)
        if (studentId != null && status != null) {
            attendanceMap[studentId] = status
        }
    }
    return attendanceMap
}
/****************************************************/
