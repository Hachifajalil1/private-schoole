package com.example.myapplication

import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri // استيراد مكتبة Uri من Android
import android.util.Log // استيراد مكتبة Log من Android
import android.widget.Toast // استيراد مكتبة Toast من Android
import androidx.activity.compose.rememberLauncherForActivityResult // استيراد مكتبة rememberLauncherForActivityResult من Android
import androidx.activity.result.contract.ActivityResultContracts // استيراد مكتبة ActivityResultContracts من Android
import androidx.compose.foundation.layout.Arrangement // استيراد مكتبة Arrangement من compose.foundation.layout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column // استيراد مكتبة Column من compose.foundation.layout
import androidx.compose.foundation.layout.PaddingValues // استيراد مكتبة PaddingValues من compose.foundation.layout
import androidx.compose.foundation.layout.Row // استيراد مكتبة Row من compose.foundation.layout
import androidx.compose.foundation.layout.Spacer // استيراد مكتبة Spacer من compose.foundation.layout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height // استيراد مكتبة height من compose.foundation.layout
import androidx.compose.foundation.layout.padding // استيراد مكتبة padding من compose.foundation.layout
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn // استيراد مكتبة LazyColumn من compose.foundation.lazy
import androidx.compose.foundation.lazy.items // استيراد مكتبة items من compose.foundation.lazy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons // استيراد مكتبة Icons من compose.material.icons
import androidx.compose.material.icons.filled.Delete // استيراد مكتبة Delete من compose.material.icons.filled
import androidx.compose.material.icons.filled.Edit // استيراد مكتبة Edit من compose.material.icons.filled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button // استيراد مكتبة Button من compose.material3
import androidx.compose.material3.Card
import androidx.compose.material3.Icon // استيراد مكتبة Icon من compose.material3
import androidx.compose.material3.IconButton // استيراد مكتبة IconButton من compose.material3
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text // استيراد مكتبة Text من compose.material3
import androidx.compose.runtime.Composable // استيراد مكتبة Composable من compose.runtime
import androidx.compose.runtime.LaunchedEffect // استيراد مكتبة LaunchedEffect من compose.runtime
import androidx.compose.runtime.getValue // استيراد مكتبة getValue من compose.runtime
import androidx.compose.runtime.mutableStateOf // استيراد مكتبة mutableStateOf من compose.runtime
import androidx.compose.runtime.remember // استيراد مكتبة remember من compose.runtime
import androidx.compose.runtime.setValue // استيراد مكتبة setValue من compose.runtime
import androidx.compose.ui.Alignment // استيراد مكتبة Alignment من compose.ui
import androidx.compose.ui.Modifier // استيراد مكتبة Modifier من compose.ui
import androidx.compose.ui.graphics.Color // استيراد مكتبة Color من compose.ui
import androidx.compose.ui.platform.LocalContext // استيراد مكتبة LocalContext من compose.ui.platform
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight // استيراد مكتبة FontWeight من compose.ui.text
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp // استيراد مكتبة dp من compose.ui.unit
import androidx.compose.ui.unit.sp // استيراد مكتبة sp من compose.ui.unit
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase // استيراد مكتبة Firebase من com.google.firebase
import com.google.firebase.FirebaseApp // استيراد مكتبة FirebaseApp من com.google.firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot // استيراد مكتبة DataSnapshot من com.google.firebase.database
import com.google.firebase.database.DatabaseError // استيراد مكتبة DatabaseError من com.google.firebase.database
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener // استيراد مكتبة ValueEventListener من com.google.firebase.database
import com.google.firebase.database.database // استيراد مكتبة database من com.google.firebase.database
import com.google.firebase.database.getValue // استيراد مكتبة getValue من com.google.firebase.database
import com.google.firebase.storage.storage // استيراد مكتبة storage من com.google.firebase.storage
import android.graphics.drawable.shapes.OvalShape;
import android.net.LinkAddress
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.room.Update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar





@Composable
fun StudentsAttendanceParents(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "No userId")

    if (userId != null) {
        Column(modifier = Modifier.padding(innerPadding)) {
            ParentSelectorScreens(userId)
        }
    } else {
        Text(text = "No user ID found", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(innerPadding))
    }
}







@Composable
fun ParentSelectorScreens(parentId: String) {
    var students by remember { mutableStateOf(mapOf<String, Triple<String, String, String>>()) }
    var selectedStudentId by remember { mutableStateOf<String?>(null) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var attendance by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var studentTimetableEntries by remember { mutableStateOf(mapOf<String, TimetableEntry>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(parentId) {
        scope.launch {
            students = fetchStudentsByParentId(parentId)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (students.isNotEmpty()) {
            newDropdownMenuSelection(
                label = "Select Student",
                options = students.map { it.key to it.value.first }.toMap(),
                selectedOptionId = selectedStudentId
            ) { studentId ->
                selectedStudentId = studentId
                selectedGroupId = students[studentId]?.third
                scope.launch {
                    selectedGroupId?.let {
                        studentTimetableEntries = newFetchTimetableEntriesForGroup(it)
                    }
                }
            }

            if (selectedStudentId != null && selectedGroupId != null) {


                newDropdownMenuSelection(
                    label = "Select Date",
                    options = newGenerateDateOptions(),
                    selectedOptionId = selectedDate
                ) { date ->
                    selectedDate = date
                }

                if (selectedDate != null) {
                    newDropdownMenuSelection(
                        label = "Select Time",
                        options = studentTimetableEntries.map { it.key to "${it.value.startTime} - ${it.value.endTime}" }.toMap(),
                        selectedOptionId = selectedTime
                    ) { time ->
                        selectedTime = time
                        scope.launch {
                            val selectedTimetableEntry = studentTimetableEntries[time]
                            if (selectedTimetableEntry != null) {
                                attendance = newFetchAttendance(selectedTimetableEntry.sessionId, selectedDate!!)
                            }
                        }
                    }
                }

                if (selectedTime != null && selectedDate != null) {
                    val selectedTimetableEntry = studentTimetableEntries[selectedTime]
                    if (selectedTimetableEntry != null) {
                        newDisplayStudentAttendance(
                            timetableEntry = selectedTimetableEntry,
                            date = selectedDate!!,
                            students = students,
                            attendance = attendance,
                            selectedStudentId = selectedStudentId!!
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun newDisplayStudentAttendance(
    timetableEntry: TimetableEntry,
    date: String,
    students: Map<String, Triple<String, String, String>>,
    attendance: Map<String, String>,
    selectedStudentId: String
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(text = "Course Information", style = MaterialTheme.typography.titleLarge)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Course: ${timetableEntry.courseName}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Teacher: ${timetableEntry.teacherName}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Classroom: ${timetableEntry.classroomName}", style = MaterialTheme.typography.titleMedium)
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(text = "Attendance for $date", style = MaterialTheme.typography.titleLarge)
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        val studentInfo = students[selectedStudentId]
        val studentName = studentInfo?.first ?: "Unknown Student"
        val status = attendance[selectedStudentId] ?: "Not Recorded"

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
                Text(text = studentName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text(text = status, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}


