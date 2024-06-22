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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TeacherGradeEntryScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "No userId")
    var selectedOption by remember { mutableStateOf("Add Grades") }

    if (userId != null) {
        Column(modifier = Modifier.padding(innerPadding)) {
            DropdownMenuSelection(
                label = "Choose Option",
                options = mapOf("Add Grades" to "Add Grades", "View Grades" to "View Grades"),
                selectedOptionId = selectedOption
            ) { option ->
                selectedOption = option!!
            }

            if (selectedOption == "Add Grades") {
                TeacherGradeEntryScreena(userId)
            } else {
                TeacherGradesDisplayScreenContent(userId)
            }
        }
    } else {
        Text(text = "No user ID found", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun TeacherGradeEntryScreena(teacherId: String) {
    val scope = rememberCoroutineScope()

    var levels by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedLevelId by remember { mutableStateOf<String?>(null) }
    var groups by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var courses by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedCourseId by remember { mutableStateOf<String?>(null) }
    var students by remember { mutableStateOf(mapOf<String, String>()) }
    var grades by remember { mutableStateOf(mapOf<String, Map<String, String>>()) }
    var examName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scope.launch {
            levels = fetchLevelsForTeacher(teacherId)
            courses = fetchCoursesForTeacher(teacherId)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        DropdownMenuSelection(
            label = "Select Level",
            options = levels,
            selectedOptionId = selectedLevelId
        ) { levelId ->
            selectedLevelId = levelId
            scope.launch {
                groups = fetchGroupsForTeacherAndLevel(teacherId, selectedLevelId!!)
                selectedGroupId = null
                selectedCourseId = null
                students = mapOf()
                grades = mapOf()
            }
        }

        if (selectedLevelId != null) {
            DropdownMenuSelection(
                label = "Select Group",
                options = groups,
                selectedOptionId = selectedGroupId
            ) { groupId ->
                selectedGroupId = groupId
                scope.launch {
                    selectedCourseId?.let {
                        students = fetchStudentsForGroup(groupId!!)
                        grades = fetchGradesForGroupAndCourse(selectedLevelId!!, groupId, it, teacherId)
                    }
                }
            }
        }

        if (selectedGroupId != null) {
            DropdownMenuSelection(
                label = "Select Course",
                options = courses,
                selectedOptionId = selectedCourseId
            ) { courseId ->
                selectedCourseId = courseId
                scope.launch {
                    selectedGroupId?.let {
                        students = fetchStudentsForGroup(it)
                        grades = fetchGradesForGroupAndCourse(selectedLevelId!!, it, courseId!!, teacherId)
                    }
                }
            }
        }

        if (selectedGroupId != null && selectedCourseId != null) {
            TextField(
                value = examName,
                onValueChange = { examName = it },
                label = { Text("Exam Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            StudentGradesEntry(
                students = students,
                grades = grades[examName] ?: mapOf(),
                onGradeChange = { studentId, grade ->
                    grades = grades.toMutableMap().apply {
                        this[examName] = (this[examName] ?: mapOf()).toMutableMap().apply {
                            put(studentId, grade)
                        }
                    }
                },
                onSave = {
                    scope.launch {
                        saveGrades(selectedLevelId!!, selectedGroupId!!, selectedCourseId!!, grades[examName] ?: mapOf(), teacherId, examName)
                    }
                }
            )
        }
    }
}

@Composable
fun StudentGradesEntry(
    students: Map<String, String>,
    grades: Map<String, String>,
    onGradeChange: (String, String) -> Unit,
    onSave: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
     Row {

       Text(text = "Enter Grades", style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = onSave,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Save Grades")
        } }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn {
            items(students.toList()) { (studentId, studentName) ->
                val grade = grades[studentId] ?: ""
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
                        Text(text = studentName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                        OutlinedTextField(
                            value = grade,
                            onValueChange = { newGrade ->
                                val gradeValue = newGrade.toIntOrNull()
                                if (gradeValue != null && gradeValue in 0..20) {
                                    onGradeChange(studentId, newGrade)
                                }
                            },
                            label = { Text("Grade") },
                            modifier = Modifier.weight(1f),
                            trailingIcon = { Text("/20") }
                        )
                    }
                }


            }
        }


    }
}




suspend fun fetchLevelsForTeacher(teacherId: String): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val timetableRef = database.getReference("timetable")

    val levelsMap = mutableMapOf<String, String>()
    val timetableSnapshot = timetableRef.get().await()
    for (timetableEntrySnapshot in timetableSnapshot.children) {
        val timetableEntry = timetableEntrySnapshot.getValue(TimetableEntry::class.java)
        if (timetableEntry?.teacherId == teacherId) {
            val levelId = timetableEntry.levelId ?: continue
            val levelName = fetchLevelName(levelId)
            if (levelName != null) {
                levelsMap[levelId] = levelName
            }
        }
    }
    return levelsMap
}

suspend fun fetchGroupsForTeacherAndLevel(teacherId: String, levelId: String): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val timetableRef = database.getReference("timetable")

    val groupsMap = mutableMapOf<String, String>()
    val timetableSnapshot = timetableRef.get().await()
    for (timetableEntrySnapshot in timetableSnapshot.children) {
        val timetableEntry = timetableEntrySnapshot.getValue(TimetableEntry::class.java)
        if (timetableEntry?.teacherId == teacherId && timetableEntry.levelId == levelId) {
            val groupId = timetableEntry.groupId ?: continue
            val groupName = fetchGroupName(levelId, groupId)
            if (groupName != null) {
                groupsMap[groupId] = groupName
            }
        }
    }
    return groupsMap
}

suspend fun fetchCoursesForTeacher(teacherId: String): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val coursesRef = database.getReference("users/teachers/$teacherId/courses")

    val coursesSnapshot = coursesRef.get().await()
    val coursesMap = mutableMapOf<String, String>()
    for (courseSnapshot in coursesSnapshot.children) {
        val courseId = courseSnapshot.value.toString()
        val courseName = fetchCourseName(courseId)
        if (courseId.isNotEmpty() && courseName.isNotEmpty()) {
            coursesMap[courseId] = courseName
        }
    }
    return coursesMap
}

suspend fun fetchCourseName(courseId: String): String {
    val database = FirebaseDatabase.getInstance()
    val courseRef = database.getReference("courses/$courseId")

    val courseSnapshot = courseRef.get().await()
    return courseSnapshot.child("name").getValue(String::class.java) ?: "Unknown Course"
}

suspend fun fetchGradesForGroupAndCourse(levelId: String, groupId: String, courseId: String, teacherId: String): Map<String, Map<String, String>> {
    val database = FirebaseDatabase.getInstance()
    val gradesRef = database.getReference("grades/$levelId/$groupId/$courseId/$teacherId")

    val gradesSnapshot = gradesRef.get().await()
    val gradesMap = mutableMapOf<String, Map<String, String>>()
    for (examSnapshot in gradesSnapshot.children) {
        val examName = examSnapshot.key ?: continue
        val examGrades = mutableMapOf<String, String>()
        for (gradeEntry in examSnapshot.children) {
            val studentId = gradeEntry.key
            val grade = gradeEntry.child("grade").getValue(String::class.java)
            if (studentId != null && grade != null) {
                examGrades[studentId] = grade
            }
        }
        gradesMap[examName] = examGrades
    }
    return gradesMap
}

suspend fun saveGrades(levelId: String, groupId: String, courseId: String, grades: Map<String, String>, teacherId: String, examName: String) {
    val database = FirebaseDatabase.getInstance()
    val gradesRef = database.getReference("grades/$levelId/$groupId/$courseId/$teacherId")

    grades.forEach { (studentId, grade) ->
        gradesRef.child(examName).child(studentId).child("grade").setValue(grade).await()
    }
}


@Composable
fun TeacherGradesDisplayScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "No userId")

    if (userId != null) {
        Column(modifier = Modifier.padding(innerPadding)) {
            TeacherGradesDisplayScreenContent(userId)
        }
    } else {
        Text(text = "No user ID found", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun TeacherGradesDisplayScreenContent(teacherId: String) {
    val scope = rememberCoroutineScope()

    var levels by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedLevelId by remember { mutableStateOf<String?>(null) }
    var groups by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var courses by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedCourseId by remember { mutableStateOf<String?>(null) }
    var examNames by remember { mutableStateOf(listOf<String>()) }
    var selectedExamName by remember { mutableStateOf<String?>(null) }
    var grades by remember { mutableStateOf(mapOf<String, String>()) }

    LaunchedEffect(Unit) {
        scope.launch {
            levels = fetchLevelsForTeacher(teacherId)
            courses = fetchCoursesForTeacher(teacherId)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        DropdownMenuSelection(
            label = "Select Level",
            options = levels,
            selectedOptionId = selectedLevelId
        ) { levelId ->
            selectedLevelId = levelId
            scope.launch {
                groups = fetchGroupsForTeacherAndLevel(teacherId, selectedLevelId!!)
                selectedGroupId = null
                selectedCourseId = null
                selectedExamName = null
                grades = mapOf()
            }
        }

        if (selectedLevelId != null) {
            DropdownMenuSelection(
                label = "Select Group",
                options = groups,
                selectedOptionId = selectedGroupId
            ) { groupId ->
                selectedGroupId = groupId
                scope.launch {
                    selectedCourseId?.let {
                        examNames = fetchExamNamesForGroupAndCourse(selectedLevelId!!, groupId!!, it, teacherId)
                        selectedExamName = null
                        grades = mapOf()
                    }
                }
            }
        }

        if (selectedGroupId != null) {
            DropdownMenuSelection(
                label = "Select Course",
                options = courses,
                selectedOptionId = selectedCourseId
            ) { courseId ->
                selectedCourseId = courseId
                scope.launch {
                    selectedGroupId?.let {
                        examNames = fetchExamNamesForGroupAndCourse(selectedLevelId!!, it, courseId!!, teacherId)
                        selectedExamName = null
                        grades = mapOf()
                    }
                }
            }
        }

        if (selectedCourseId != null) {
            DropdownMenuSelection(
                label = "Select Exam Name",
                options = examNames.associateWith { it },
                selectedOptionId = selectedExamName
            ) { examName ->
                selectedExamName = examName
                scope.launch {
                    grades = fetchGradesForGroupCourseAndExam(selectedLevelId!!, selectedGroupId!!, selectedCourseId!!, teacherId, examName!!)
                }
            }
        }

        if (selectedExamName != null) {
            DisplayGrades(
                grades = grades
            )
        }
    }
}

@Composable
fun DisplayGrades(grades: Map<String, String>) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Grades", style = MaterialTheme.typography.titleLarge)
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn {
            items(grades.toList()) { (studentName, grade) ->

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
                        Text(text = studentName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                        Text(text = grade+"/20", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                    }
                }
                Divider()
            }
        }
    }
}

suspend fun fetchExamNamesForGroupAndCourse(levelId: String, groupId: String, courseId: String, teacherId: String): List<String> {
    val database = FirebaseDatabase.getInstance()
    val examsRef = database.getReference("grades/$levelId/$groupId/$courseId/$teacherId")

    val examsSnapshot = examsRef.get().await()
    return examsSnapshot.children.mapNotNull { it.key }
}

suspend fun fetchGradesForGroupCourseAndExam(levelId: String, groupId: String, courseId: String, teacherId: String, examName: String): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val gradesRef = database.getReference("grades/$levelId/$groupId/$courseId/$teacherId/$examName")

    val gradesSnapshot = gradesRef.get().await()
    val gradesMap = mutableMapOf<String, String>()
    for (gradeEntry in gradesSnapshot.children) {
        val studentId = gradeEntry.key
        val grade = gradeEntry.child("grade").getValue(String::class.java)
        if (studentId != null && grade != null) {
            val studentName = fetchStudentName(studentId)
            gradesMap[studentName] = grade
        }
    }
    return gradesMap
}

suspend fun fetchStudentName(studentId: String): String {
    val database = FirebaseDatabase.getInstance()
    val studentRef = database.getReference("users/students/$studentId")

    val studentSnapshot = studentRef.get().await()
    val studentName = studentSnapshot.child("name").getValue(String::class.java) ?: "Unknown"
    val studentFamilyName = studentSnapshot.child("familyname").getValue(String::class.java) ?: "Student"
    return "$studentFamilyName $studentName"
}

// Reuse the other previously defined functions: fetchLevelsForTeacher, fetchGroupsForTeacherAndLevel, fetchCoursesForTeacher, fetchCourseName, fetchStudentsForGroup, fetchLevelName, fetchGroupName, GradeEntry data class, DropdownMenuSelection composable.


@Composable
fun AllGradesDisplayScreens() {
    val scope = rememberCoroutineScope()

    var levels by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedLevelId by remember { mutableStateOf<String?>(null) }
    var groups by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var courses by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedCourseId by remember { mutableStateOf<String?>(null) }
    var teachers by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedTeacherId by remember { mutableStateOf<String?>(null) }
    var examNames by remember { mutableStateOf(listOf<String>()) }
    var selectedExamName by remember { mutableStateOf<String?>(null) }
    var grades by remember { mutableStateOf(mapOf<String, String>()) }

    LaunchedEffect(Unit) {
        scope.launch {
            levels = fetchAllLevels()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        DropdownMenuSelection(
            label = "Select Level",
            options = levels,
            selectedOptionId = selectedLevelId
        ) { levelId ->
            selectedLevelId = levelId
            scope.launch {
                groups = fetchGroupsForLevel(levelId!!)
                selectedGroupId = null
                selectedCourseId = null
                selectedTeacherId = null
                selectedExamName = null
                grades = mapOf()
            }
        }

        if (selectedLevelId != null) {
            DropdownMenuSelection(
                label = "Select Group",
                options = groups,
                selectedOptionId = selectedGroupId
            ) { groupId ->
                selectedGroupId = groupId
                scope.launch {
                    courses = fetchCoursesForGroup(selectedLevelId!!, groupId!!)
                    selectedCourseId = null
                    selectedTeacherId = null
                    selectedExamName = null
                    grades = mapOf()
                }
            }
        }

        if (selectedGroupId != null) {
            DropdownMenuSelection(
                label = "Select Course",
                options = courses,
                selectedOptionId = selectedCourseId
            ) { courseId ->
                selectedCourseId = courseId
                scope.launch {
                    teachers = fetchTeachersForCourse(selectedLevelId!!, selectedGroupId!!, courseId!!)
                    selectedTeacherId = null
                    selectedExamName = null
                    grades = mapOf()
                }
            }
        }

        if (selectedCourseId != null) {
            DropdownMenuSelection(
                label = "Select Teacher",
                options = teachers,
                selectedOptionId = selectedTeacherId
            ) { teacherId ->
                selectedTeacherId = teacherId
                scope.launch {
                    examNames = fetchExamNamesForGroupAndCourse(selectedLevelId!!, selectedGroupId!!, selectedCourseId!!, teacherId!!)
                    selectedExamName = null
                    grades = mapOf()
                }
            }
        }

        if (selectedTeacherId != null) {
            DropdownMenuSelection(
                label = "Select Exam Name",
                options = examNames.associateWith { it },
                selectedOptionId = selectedExamName
            ) { examName ->
                selectedExamName = examName
                scope.launch {
                    grades = fetchGradesForGroupCourseAndExam(selectedLevelId!!, selectedGroupId!!, selectedCourseId!!, selectedTeacherId!!, examName!!)
                }
            }
        }

        if (selectedExamName != null) {
            DisplayGrades(
                grades = grades
            )
        }
    }
}



suspend fun fetchAllLevels(): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val levelsRef = database.getReference("levels")

    val levelsSnapshot = levelsRef.get().await()
    val levelsMap = mutableMapOf<String, String>()
    for (levelSnapshot in levelsSnapshot.children) {
        val levelId = levelSnapshot.key ?: continue
        val levelName = levelSnapshot.child("name").getValue(String::class.java) ?: "Unknown Level"
        levelsMap[levelId] = levelName
    }
    return levelsMap
}

suspend fun fetchGroupsForLevel(levelId: String): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val groupsRef = database.getReference("levels/$levelId/groups")

    val groupsSnapshot = groupsRef.get().await()
    val groupsMap = mutableMapOf<String, String>()
    for (groupSnapshot in groupsSnapshot.children) {
        val groupId = groupSnapshot.key ?: continue
        val groupName = groupSnapshot.child("name").getValue(String::class.java) ?: "Unknown Group"
        groupsMap[groupId] = groupName
    }
    return groupsMap
}

suspend fun fetchCoursesForGroup(levelId: String, groupId: String): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val timetableRef = database.getReference("timetable")

    val coursesMap = mutableMapOf<String, String>()
    val timetableSnapshot = timetableRef.get().await()
    for (timetableEntrySnapshot in timetableSnapshot.children) {
        val timetableEntry = timetableEntrySnapshot.getValue(TimetableEntry::class.java)
        if (timetableEntry?.levelId == levelId && timetableEntry.groupId == groupId) {
            val courseId = timetableEntry.courseId ?: continue
            val courseName = fetchCourseName(courseId)
            if (courseName.isNotEmpty()) {
                coursesMap[courseId] = courseName
            }
        }
    }
    return coursesMap
}

suspend fun fetchTeachersForCourse(levelId: String, groupId: String, courseId: String): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val timetableRef = database.getReference("timetable")

    val teachersMap = mutableMapOf<String, String>()
    val timetableSnapshot = timetableRef.get().await()
    for (timetableEntrySnapshot in timetableSnapshot.children) {
        val timetableEntry = timetableEntrySnapshot.getValue(TimetableEntry::class.java)
        if (timetableEntry?.levelId == levelId && timetableEntry.groupId == groupId && timetableEntry.courseId == courseId) {
            val teacherId = timetableEntry.teacherId ?: continue
            val teacherName = fetchTeacherName(teacherId)
            if (teacherName.isNotEmpty()) {
                teachersMap[teacherId] = teacherName
            }
        }
    }
    return teachersMap
}
suspend fun fetchTeacherName(teacherId: String): String {
    val database = FirebaseDatabase.getInstance()
    val teacherRef = database.getReference("users/teachers/$teacherId")

    val teacherSnapshot = teacherRef.get().await()
    val teacherName = teacherSnapshot.child("name").getValue(String::class.java) ?: "Unknown"
    val teacherFamilyName = teacherSnapshot.child("familyname").getValue(String::class.java) ?: "Teacher"
    return "$teacherFamilyName $teacherName"
}


