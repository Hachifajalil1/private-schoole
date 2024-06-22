
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
fun GradesStudents(innerPadding: PaddingValues) {
    var timetable by remember { mutableStateOf(mapOf<String, TimetableEntry>()) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val levelId = sharedPreferences.getString("levelId", null)
    val groupId = sharedPreferences.getString("groupId", null)

    if (userId != null && levelId != null && groupId != null) {
        Column(modifier = Modifier.padding(innerPadding)) {
            StudentGradesScreen(innerPadding)
        }
    }
}

@Composable
fun StudentGradesScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val userRole = sharedPreferences.getString("userRole", null)

    if (userId != null && userRole == "student") {
        val levelId = sharedPreferences.getString("levelId", null)
        val groupId = sharedPreferences.getString("groupId", null)

        if (levelId != null && groupId != null) {
            StudentGradesDisplay(userId, levelId, groupId)
        } else {
            Text(
                text = "Error: Missing level or group ID.",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        Text(
            text = "No user ID found or user is not a student.",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun StudentGradesDisplay(studentId: String, levelId: String, groupId: String) {
    val scope = rememberCoroutineScope()
    var grades by remember { mutableStateOf<Map<String, String>>(mapOf()) }
    var examNames by remember { mutableStateOf(listOf<String>()) }
    var selectedExamName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            examNames = fetchExamNames(levelId, groupId, studentId)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (examNames.isNotEmpty()) {
            DropdownMenuSelection(
                label = "Select Exam",
                options = examNames.associateWith { it },
                selectedOptionId = selectedExamName
            ) { examName ->
                selectedExamName = examName
                scope.launch {
                    grades = fetchStudentGrades(studentId, levelId, groupId, examName!!)
                }
            }

            if (selectedExamName != null) {
                LazyColumn {
                    items(grades.toList()) { (courseName, grade) ->
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
                                Text(
                                    text = courseName,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = grade,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text(text = "No exams available.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}


suspend fun fetchStudentGrades(studentId: String, levelId: String, groupId: String, examName: String): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val gradesRef = database.getReference("grades/$levelId/$groupId")

    val gradesSnapshot = gradesRef.get().await()
    val gradesMap = mutableMapOf<String, String>()
    for (courseSnapshot in gradesSnapshot.children) {
        for (teacherSnapshot in courseSnapshot.children) {
            val grade = teacherSnapshot.child(examName).child(studentId).child("grade").getValue(String::class.java)
            if (grade != null) {
                val courseName = fetchCourseName(courseSnapshot.key!!)
                gradesMap[courseName] = grade
            }
        }
    }
    return gradesMap
}

suspend fun fetchExamNames(levelId: String, groupId: String, studentId: String): List<String> {
    val database = FirebaseDatabase.getInstance()
    val gradesRef = database.getReference("grades/$levelId/$groupId")

    val gradesSnapshot = gradesRef.get().await()
    val examNames = mutableSetOf<String>()
    for (courseSnapshot in gradesSnapshot.children) {
        for (teacherSnapshot in courseSnapshot.children) {
            for (examSnapshot in teacherSnapshot.children) {
                if (examSnapshot.child(studentId).exists()) {
                    examNames.add(examSnapshot.key!!)
                }
            }
        }
    }
    return examNames.toList()
}


