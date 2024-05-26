package com.example.myapplication

import android.content.Context
import android.util.Log // استيراد مكتبة Log من Android
import android.widget.Toast // استيراد مكتبة Toast من Android
import androidx.compose.foundation.layout.Arrangement // استيراد مكتبة Arrangement من compose.foundation.layout
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
import androidx.compose.material.icons.Icons // استيراد مكتبة Icons من compose.material.icons
import androidx.compose.material.icons.filled.Delete // استيراد مكتبة Delete من compose.material.icons.filled
import androidx.compose.material.icons.filled.Edit // استيراد مكتبة Edit من compose.material.icons.filled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button // استيراد مكتبة Button من compose.material3
import androidx.compose.material3.Card
import androidx.compose.material3.Icon // استيراد مكتبة Icon من compose.material3
import androidx.compose.material3.IconButton // استيراد مكتبة IconButton من compose.material3
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight // استيراد مكتبة FontWeight من compose.ui.text
import androidx.compose.ui.unit.dp // استيراد مكتبة dp من compose.ui.unit
import androidx.compose.ui.unit.sp // استيراد مكتبة sp من compose.ui.unit
import com.google.firebase.Firebase // استيراد مكتبة Firebase من com.google.firebase
import com.google.firebase.database.DataSnapshot // استيراد مكتبة DataSnapshot من com.google.firebase.database
import com.google.firebase.database.DatabaseError // استيراد مكتبة DatabaseError من com.google.firebase.database
import com.google.firebase.database.ValueEventListener // استيراد مكتبة ValueEventListener من com.google.firebase.database
import com.google.firebase.database.database // استيراد مكتبة database من com.google.firebase.database
import androidx.compose.material.icons.filled.AddCircle

data class Course(val id: String, val name: String)

@Composable
fun CourseList(
    innerPadding: PaddingValues,
    onClickAdd: () -> Unit,
    onDeleteCourse: (Course) -> Unit,
    onUpdateCourse: (Course) -> Unit
) {
    var courses by remember { mutableStateOf(listOf<Course>()) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val database = Firebase.database
        val coursesRef = database.getReference("courses")

        coursesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedCourses = snapshot.children.mapNotNull { courseSnapshot ->
                    val id = courseSnapshot.key.orEmpty()
                    val name = courseSnapshot.child("name").getValue(String::class.java).orEmpty()
                    Course(id, name)
                }
                courses = updatedCourses
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CourseList", "Error loading courses", error.toException())
            }
        })
    }

    Column(modifier = Modifier.padding(innerPadding)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 40.dp, start = 8.dp,top=50.dp)
        ) {

            IconButton(
                onClick = { /* Perform search action */ }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add Course")
            }
        }

        if (showAddDialog) {
            CourseAdd(
                onAddCourse = {
                    addCourseDatabase(it.courseName)
                },
                onCloseDialog = {
                    showAddDialog = false
                }
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filteredCourses = courses.filter { course ->
                course.name.contains(searchQuery, ignoreCase = true)
            }
            items(filteredCourses) { course ->
                CourseItem(
                    course = course,
                    onDelete = { onDeleteCourse(course) },
                    onUpdate = { onUpdateCourse(course) }
                )
            }
        }
    }
}

@Composable
fun CourseItem(
    course: Course,
    onDelete: () -> Unit,
    onUpdate: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this course?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* Handle click event */ }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${course.name}",
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            IconButton(
                onClick = onUpdate,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Course")
            }

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Course")
            }
        }
    }
}

@Composable
fun CoursesScreen(innerPadding: PaddingValues) {
    var selectedAdmi by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedCourse: Course? by remember { mutableStateOf(null) }

    Column {
        CourseList(
            innerPadding = innerPadding,
            onClickAdd = {},
            onDeleteCourse = { course -> deleteCourseDatabase(context, course) },
            onUpdateCourse = { course ->
                showEditDialog = true
                selectedCourse = course
            }
        )
    }

    if (showEditDialog && selectedCourse != null) {
        CourseUpdateDialog(
            course = selectedCourse!!,

            onCloseDialog = {
                showEditDialog = false
                selectedCourse = null
            }
        )
    }
}

data class CourseData(val courseName: String)
@Composable
fun CourseAdd(onAddCourse: (CourseData) -> Unit, onCloseDialog: () -> Unit) {
    var courseName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onCloseDialog() },
        title = { Text(text = "Add Course") },
        text = {
            Column {
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { courseName = it },
                    label = { Text("Course Name") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (courseName.isNotBlank()) {
                        onAddCourse(CourseData(courseName))
                        courseName = ""
                        onCloseDialog()
                    }
                }
            ) {
                Text("Add")
            }
        }
    )
}

fun addCourseDatabase(courseName: String) {
    val database = Firebase.database
    val coursesRef = database.getReference("courses")

    // Generate a unique key for the new course
    val newCourseRef = coursesRef.push()

    // Set the course data
    newCourseRef.setValue(mapOf("name" to courseName))
}

fun deleteCourseDatabase(context: Context, course: Course) {
    val database = Firebase.database
    val coursesRef = database.getReference("courses").child(course.id)
    coursesRef.removeValue()
    Toast.makeText(context, "Course deleted", Toast.LENGTH_SHORT).show()
}

fun updateCourseDatabase(id: String, name: String) {
    val database = Firebase.database
    val coursesRef = database.getReference("courses").child(id)

    // Update the course data
    val updates = mapOf<String, Any>(
        "name" to name
    )
    coursesRef.updateChildren(updates)
}

@Composable
fun CourseUpdateDialog(
    course: Course,
    onCloseDialog: () -> Unit
) {
    var courseName by remember { mutableStateOf(course.name) }

    AlertDialog(
        onDismissRequest = { onCloseDialog() },
        title = { Text(text = "Update Course") },
        text = {
            Column {
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { courseName = it },
                    label = { Text("Course Name") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (courseName.isNotBlank()) {
                        // Move the updateCourseDatabase call here
                        updateCourseDatabase(course.id, courseName)
                        onCloseDialog()
                    }
                }
            ) {
                Text("Update")
            }
        }

    )
}

