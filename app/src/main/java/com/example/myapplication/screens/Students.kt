package com.example.myapplication

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
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.semantics.Role
import androidx.room.Update
import kotlinx.coroutines.tasks.await

data class Student(
    val id: String = "",
    val familyName: String = "",
    val name: String = "",
    val email: String = "",
    val address: String = "",
    val image: String = "",
    val phone: String = "",
    val role: String = "",
    val levelId: String = "",
    val groupId: String = "",
    val parentId: String = "",
)

data class StudentCreateUser(
    val familyname: String = "",
    val name: String = "",
    val email: String = "",
    val address: String = "",
    var image: String = "",
    val phone: String = "",
    val role: String = "",

)

@Composable
fun StudentsScreen(innerPadding: PaddingValues) {
    var selectedAdmi by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedRoom: Student? by remember { mutableStateOf(null) }

    Column {
        StudentList(
            innerPadding = innerPadding,
            onClickAdd = {},
            onDeleteStudent = { student -> deleteStudentDatabase(context, student) },
            onUpdateStudent = {
            }
        )
    }
}

data class Roomms(val id: String, val name: String, val capacity: String)
@Composable
fun StudentList(
    innerPadding: PaddingValues,
    onClickAdd: () -> Unit,
    onDeleteStudent: (Student) -> Unit,
    onUpdateStudent: (Student) -> Unit
) {
    var students by remember { mutableStateOf(listOf<Student>()) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddStudentForm by remember { mutableStateOf(false) }
    var selectedLevelId by remember { mutableStateOf<String?>(null) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var levels by remember { mutableStateOf(mapOf<String, String>()) }
    var groups by remember { mutableStateOf(mapOf<String, String>()) }

    // Function to fetch students from Firebase
    fun fetchStudents() {
        val database = Firebase.database
        val studentsRef = database.getReference("users").child("students")
        studentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedStudents = snapshot.children.mapNotNull { studentSnapshot ->
                    val id = studentSnapshot.key.orEmpty()
                    val familyName = studentSnapshot.child("familyname").getValue(String::class.java).orEmpty()
                    val name = studentSnapshot.child("name").getValue(String::class.java).orEmpty()
                    val email = studentSnapshot.child("email").getValue(String::class.java).orEmpty()
                    val address = studentSnapshot.child("address").getValue(String::class.java).orEmpty()
                    val phone = studentSnapshot.child("phone").getValue(String::class.java).orEmpty()
                    val image = studentSnapshot.child("image").getValue(String::class.java).orEmpty()
                    val levelId = studentSnapshot.child("levelId").getValue(String::class.java).orEmpty()
                    val groupId = studentSnapshot.child("groupId").getValue(String::class.java).orEmpty()
                     val parentId = studentSnapshot.child("parentId").getValue(String::class.java).orEmpty()
                    Student(id, familyName, name, email, address, image, phone, "student", levelId, groupId,parentId)
                }
                students = updatedStudents
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StudentList", "Error loading students", error.toException())
            }
        })
    }


    LaunchedEffect(Unit) {
        levels = fetchLevels()
        fetchStudents()
    }

    LaunchedEffect(selectedLevelId) {
        students = listOf() // Clear the student list before fetching new data
        if (selectedLevelId != null && selectedLevelId != "all") {
            fetchGroupsForLevel(selectedLevelId!!) { fetchedGroups ->
                groups = fetchedGroups
            }
        } else {
            groups = emptyMap()
        }
        selectedGroupId = null // Reset selected group when level changes
        fetchStudents()
    }

    LaunchedEffect(selectedGroupId) {
        if (selectedLevelId == null || selectedLevelId == "all") {
          //  students = listOf() // Clear the student list when only group changes

        }else{ students = listOf()
            fetchStudents()}

    }

    Column(modifier = Modifier.padding(innerPadding)) {
        Column(modifier = Modifier.padding(start = 60.dp, end = 50.dp,top = 20.dp)) {
            DropdownMenuSelection(
                label = "Select Level",
                options = levels + mapOf("all" to "All"),
                selectedOptionId = selectedLevelId
            ) { levelId ->
                selectedLevelId = if (levelId == "all") null else levelId
            }

            if (selectedLevelId != null && selectedLevelId != "all") {
                Spacer(modifier = Modifier.height(10.dp))
                DropdownMenuSelection(
                    label = "Select Group",
                    options = groups,
                    selectedOptionId = selectedGroupId
                ) { groupId ->
                    selectedGroupId = groupId
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 40.dp, start = 8.dp, top = 20.dp)
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
                onClick = { showAddStudentForm = true }
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add student")
            }
        }

        if (showAddStudentForm) {
            AddStudentForm(onDismiss = { showAddStudentForm = false })
        }

        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filteredStudents = students.filter { student ->
                student.name.contains(searchQuery, ignoreCase = true) ||
                        student.address.contains(searchQuery, ignoreCase = true) ||
                        student.phone.contains(searchQuery, ignoreCase = true) ||
                        student.familyName.contains(searchQuery, ignoreCase = true) ||
                        student.email.contains(searchQuery, ignoreCase = true)
            }.filter { student ->
                (selectedLevelId == null || student.levelId == selectedLevelId) &&
                        (selectedGroupId == null || student.groupId == selectedGroupId)
            }
            items(filteredStudents) { student ->
                StudentItem(
                    student = student,
                    onDelete = { onDeleteStudent(student) },
                    onUpdate = { onUpdateStudent(student) }
                )
            }
        }
    }
}




@Composable
fun StudentItem(
    student: Student,
    onDelete: () -> Unit,
    onUpdate: () -> Unit
) {
    var name by remember { mutableStateOf(student.name) }
    var email by remember { mutableStateOf(student.email) }
    var address by remember { mutableStateOf(student.address) }
    var phone by remember { mutableStateOf(student.phone) }
    var familyName by remember { mutableStateOf(student.familyName) }
    var image by remember { mutableStateOf(student.image) }
    var parentId by remember { mutableStateOf(student.parentId) }
    var parentName by remember { mutableStateOf("") }
    var updatedImage by remember { mutableStateOf<String?>(null) }
    var imageUpdated by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    val getContent = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            updatedImage = uri.toString()
            imageUpdated = true
        }
    }

    LaunchedEffect(parentId) {
        parentName = fetchParentById(parentId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this student?") },
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

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("Student Details") },
            text = {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            FirebaseImage(url = image)

                            if (editMode) {
                                IconButton(
                                    onClick = {
                                        getContent.launch("image/*")
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Student")
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = familyName,
                        onValueChange = { familyName = it },
                        label = { Text("Family Name") },
                        readOnly = !editMode
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        readOnly = !editMode
                    )



                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        readOnly = !editMode
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        readOnly = !editMode
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        readOnly = !editMode
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInfoDialog = false
                        if (editMode) {

                            if (imageUpdated) {
                                val storageRef = Firebase.storage.reference.child("images/ProfileUsers/${student.id}")
                                val uploadTask = storageRef.putFile(Uri.parse(updatedImage))

                                uploadTask.addOnCompleteListener { uploadTask ->
                                    if (uploadTask.isSuccessful) {
                                        storageRef.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                                            if (downloadUrlTask.isSuccessful) {
                                                val downloadUri = downloadUrlTask.result
                                                val imageUrl = downloadUri.toString()
                                                updateStudentData(student.id, familyName, name, email, address, phone, imageUrl)
                                            }
                                        }
                                    }
                                }
                            } else {
                                updateStudentData(student.id, familyName, name, email, address, phone, image)
                            }
                            editMode = false
                        }
                    }
                ) {
                    Text(if (editMode) "Save" else "Close")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { showInfoDialog = true }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                FirebaseImage(url = image)
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$familyName $name",
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 20.dp)
                ) {
                    Text(
                        text = parentName,
                        style = TextStyle(
                            fontFamily = FontFamily.Default,
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    )

                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = email,
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp,
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            IconButton(
                onClick = { showInfoDialog = true; editMode = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Student")
            }

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Student")
            }
        }
    }
}



    fun updateStudentData(uid: String, familyName: String, name: String, email: String, address: String, phone: String, image: String) {
        val database = Firebase.database
        val userRef = database.reference.child("users").child("students").child(uid)

        val data: Map<String, Any> = hashMapOf(
            "name" to name,
            "familyname" to familyName,
            "email" to email,
            "address" to address,
            "phone" to phone,
            "image" to image,

        )

        userRef
            .updateChildren(data)
            .addOnSuccessListener {
                // Update successful
            }
            .addOnFailureListener {
                // Update failed
            }
    }




 @Composable
fun StudentsScreenn(innerPadding: PaddingValues) {
    AddStudentForm {

    }
}


@Composable
fun AddStudentForm(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var familyname by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    var selectImage by remember { mutableStateOf<String?>(null) }
    var imageSelect by remember { mutableStateOf(false) }
    var selectedLevelId by remember { mutableStateOf<String?>(null) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var selectedParentId by remember { mutableStateOf<String?>(null) }
    var levels by remember { mutableStateOf(mapOf<String, String>()) }
    var groups by remember { mutableStateOf(mapOf<String, String>()) }
    var parents by remember { mutableStateOf(mapOf<String, String>()) }

    val isEmailEmpty = email.isEmpty()
    val isPasswordEmpty = password.isEmpty()
    val isEmailValid by remember(email) {
        mutableStateOf(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    val getContent =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectImage = uri.toString()
                imageSelect = true
            }
        }

    LaunchedEffect(Unit) {
        levels = fetchLevels()
        parents = fetchParents()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New Student") },
        text = {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { getContent.launch("image/*") },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(if (selectImage != null) "Selected Image" else "Select Image")
                }

                OutlinedTextField(
                    value = familyname,
                    onValueChange = { familyname = it },
                    label = { Text("Family Name") }
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email *") },
                    isError = isEmailEmpty || !isEmailValid,
                    textStyle = if (isEmailEmpty || !isEmailValid) LocalTextStyle.current.copy(color = Color.Red) else LocalTextStyle.current
                )
                if (!isEmailValid && email.isNotEmpty()) {
                    Text(
                        text = "Invalid Email Format",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Red,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password *") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = isPasswordEmpty,
                    textStyle = if (isPasswordEmpty) LocalTextStyle.current.copy(color = Color.Red) else LocalTextStyle.current
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") }
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") }
                )

                DropdownMenuSelection(
                    label = "Select Level",
                    options = levels,
                    selectedOptionId = selectedLevelId
                ) { levelId ->
                    selectedLevelId = levelId
                    if (levelId != null) {
                        fetchGroupsForLevel(levelId) { groups = it }
                    }
                }

                if (selectedLevelId != null) {
                    DropdownMenuSelection(
                        label = "Select Group",
                        options = groups,
                        selectedOptionId = selectedGroupId
                    ) { selectedGroupId = it }
                }

                DropdownMenuSelection(
                    label = "Select Parent",
                    options = parents,
                    selectedOptionId = selectedParentId
                ) { selectedParentId = it }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty() || !isEmailValid) {
                        // Handle empty or invalid email or password
                        return@Button
                    }

                    val adminA = studentsCreatUser(
                        familyname,
                        name,
                        email,
                        address,
                        "",
                        phone,
                        "student",
                        selectedParentId ?: "",
                        selectedLevelId ?: "",
                        selectedGroupId ?: ""
                    )

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = task.result?.user
                                if (user != null) {
                                    val uid = user.uid
                                    if (imageSelect) {
                                        val storageRef =
                                            Firebase.storage.reference.child("images/ProfileUsers/${uid}")
                                        val uploadTask = storageRef.putFile(Uri.parse(selectImage))

                                        uploadTask.addOnCompleteListener { uploadTask ->
                                            if (uploadTask.isSuccessful) {
                                                storageRef.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                                                    if (downloadUrlTask.isSuccessful) {
                                                        val downloadUri = downloadUrlTask.result
                                                        val imageUrl = downloadUri.toString()
                                                        adminA.image = imageUrl

                                                        saveStudentData(uid, adminA, selectedLevelId, selectedGroupId)
                                                    }
                                                }
                                            } else {
                                                uploadTask.exception?.let {
                                                    // Handle the exception
                                                }
                                            }
                                        }
                                    } else {
                                        saveStudentData(uid, adminA, selectedLevelId, selectedGroupId)
                                    }

                                    name = ""
                                    email = ""
                                    password = ""
                                    address = ""
                                    phone = ""
                                    familyname = ""
                                    image = ""
                                }
                            } else {
                                // Handle registration failure
                            }
                        }
                }
            ) {
                Text("Add")
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuSelection(
    label: String,
    options: Map<String, String>,
    selectedOptionId: String?,
    onOptionSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOptionText = options[selectedOptionId] ?: label

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedOptionText,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                label = { Text(label) },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropDown,
                        contentDescription = null
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                   // backgroundColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.medium
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { (optionId, optionName) ->
                    DropdownMenuItem(
                        onClick = {
                            onOptionSelected(optionId)
                            expanded = false
                        },
                        text = { Text(text = optionName) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedLevelId by remember { mutableStateOf<String?>(null) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    val levels = mapOf("1" to "Level 1", "2" to "Level 2")
    val groups = mapOf("A" to "Group A", "B" to "Group B")


}


fun saveStudentData(uid: String, student: studentsCreatUser, levelId: String?, groupId: String?) {
    val database = FirebaseDatabase.getInstance()
    val studentRef = database.getReference("users/students")
  //  val groupRef = levelId?.let { database.getReference("levels").child(it).child("groups").child(groupId!!) }

    studentRef.child(uid).setValue(student)
        .addOnSuccessListener {
         //   if (groupRef != null) {
            //    groupRef.child(uid).setValue(true)
              //      .addOnSuccessListener {
                        // Handle success for saving student ID in group
                //    }

               //     .addOnFailureListener {
                        // Handle failure for saving student ID in group
                //    }
         //   }
        }
        .addOnFailureListener {
            // Handle failure for saving student data
        }
}

suspend fun fetchLevels(): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val levelsRef = database.getReference("levels")

    return try {
        val snapshot = levelsRef.get().await()
        snapshot.children.associate { it.key.toString() to it.child("name").value.toString() }
    } catch (e: Exception) {
        emptyMap()
    }
}

fun fetchGroupsForLevel(levelId: String, onResult: (Map<String, String>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val groupsRef = database.getReference("levels").child(levelId).child("groups")

    groupsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val groups = snapshot.children.associate { it.key.toString() to it.child("name").value.toString() }
            onResult(groups)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })
}

suspend fun fetchParents(): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val parentsRef = database.getReference("users/parents")

    return try {
        val snapshot = parentsRef.get().await()
        snapshot.children.associate { it.key.toString() to it.child("familyname").value.toString()+" "+it.child("name").value.toString() }
    } catch (e: Exception) {
        emptyMap()
    }
}
suspend fun fetchParentById(id: String): String {
    val database = FirebaseDatabase.getInstance()
    val parentRef = database.getReference("users/parents").child(id)

    return try {
        val snapshot = parentRef.get().await()
        if (snapshot.exists()) {
            snapshot.child("familyname").value.toString() + " " + snapshot.child("name").value.toString()
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}

data class studentsCreatUser(
    val familyname: String,
    val name: String,
    val email: String,
    val address: String,
    var image: String,
    val phone: String,
    val role: String,
    val parentId: String,
    val levelId: String,
    val groupId: String
)
fun deleteStudentDatabase(context: Context, room: Student) {
    val database = Firebase.database
    val roomsRef = database.getReference("users/students").child(room.id)
    roomsRef.removeValue()
    Toast.makeText(context, "Student deleted", Toast.LENGTH_SHORT).show()
}
/*
@Composable
fun StudentsScreen(innerPadding: PaddingValues) {
    var levels by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedLevelId by remember { mutableStateOf<String?>(null) }
    var groups by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedGroupId by remember { mutableStateOf<String?>(null) }
    var studentName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        levels = fetchLevels()
    }

    Column(modifier = Modifier.padding(innerPadding)) {
        DropdownMenuSelection(
            label = "Select Level",
            options = levels,
            selectedOptionId = selectedLevelId
        ) { levelId ->
            selectedLevelId = levelId
            if (levelId != null) {
                fetchGroupsForLevel(levelId) { groups = it }
            }
        }

        if (selectedLevelId != null) {
            DropdownMenuSelection(
                label = "Select Group",
                options = groups,
                selectedOptionId = selectedGroupId
            ) { selectedGroupId = it }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = studentName,
            onValueChange = { studentName = it },
            label = { Text("Student Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = { saveStudentData(selectedLevelId, selectedGroupId, studentName) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Save Student")
        }
    }
}

@Composable
fun DropdownMenuSelection(
    label: String,
    options: Map<String, String>,
    selectedOptionId: String?,
    onOptionSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Box {
            OutlinedTextField(
                value = options[selectedOptionId] ?: label,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                readOnly = true,
                label = { Text(label) }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { (optionId, optionName) ->
                    DropdownMenuItem(
                        onClick = {
                            onOptionSelected(optionId)
                            expanded = false
                        },
                        text = { Text(text = optionName) }
                    )
                }
            }
        }
    }
}

fun saveStudentData(levelId: String?, groupId: String?, studentName: String) {
    if (levelId == null || groupId == null || studentName.isBlank()) {
        // Handle error: Level, Group, or Student Name is not selected or empty
        return
    }

    val database = FirebaseDatabase.getInstance()
    val studentRef = database.getReference("users/students")
    val groupRef = database.getReference("levels").child(levelId).child("groups").child(groupId)

    val studentId = studentRef.push().key
    if (studentId != null) {
        val studentData = mapOf(
            "name" to studentName
        )

        // Save student data in the main students table
        studentRef.child(studentId).setValue(studentData)
            .addOnSuccessListener {
                // Save student ID in the specific group within the level
                groupRef.child(studentId).setValue(true)
                    .addOnSuccessListener {
                        // Handle success for both operations
                    }
                    .addOnFailureListener {
                        // Handle failure for saving student ID in group
                    }
            }
            .addOnFailureListener {
                // Handle failure for saving student data
            }
    }
}

suspend fun fetchLevels(): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val levelsRef = database.getReference("levels")

    return try {
        val snapshot = levelsRef.get().await()
        snapshot.children.associate { it.key.toString() to it.child("name").value.toString() }
    } catch (e: Exception) {
        emptyMap()
    }
}

fun fetchGroupsForLevel(levelId: String, onResult: (Map<String, String>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val groupsRef = database.getReference("levels").child(levelId).child("groups")

    groupsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val groups = snapshot.children.associate { it.key.toString() to it.child("name").value.toString() }
            onResult(groups)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })
}
*/