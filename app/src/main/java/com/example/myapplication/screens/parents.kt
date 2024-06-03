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
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.room.Update

data class Parents(
    val id: String="",
    val familyname: String = "",
    val name: String = "",
    val email: String = "",
    val address: String ="",
    val image: String = "",
    val phone: String = "",
    val role: String="",

)
data class ParentsCreatUser(
    val familyname: String = "",
    val name: String = "",
    val email: String = "",
    val address: String ="",
    var image: String = "",
    val phone: String = "",
    val role: String="",

)
val database = Firebase.database

@Composable
fun ParentsScreen(innerPadding: PaddingValues) {
    var selectedAdmi by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedRoom: Parents? by remember { mutableStateOf(null) }

    Column {
        ParentsLis(
            innerPadding = innerPadding,
            onClickAdd = {},
            onDeleteRoom = {  room -> deleteParentsDatabase(context, room)},
            onUpdateRoom = {
            }
        )
    }

    /*    if (showEditDialog && selectedRoom != null) {
            RoomUpdateDialog(
                room = selectedRoom!!,

                onCloseDialog = {
                    showEditDialog = false
                    selectedRoom = null
                }
            )
        }*/
}
data class Roommn(val id: String, val name: String, val capacity: String)
@Composable
fun ParentsLis(
    innerPadding: PaddingValues,
    onClickAdd: () -> Unit,
    onDeleteRoom: (Parents) -> Unit,
    onUpdateRoom: (Parents) -> Unit
) {
    var rooms by remember { mutableStateOf(listOf<Parents>()) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showAddTeacherForm by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val database = Firebase.database
        val roomsRef = database.getReference("users").child("parents")

        roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedRooms = snapshot.children.mapNotNull { roomSnapshot ->
                    val id = roomSnapshot.key.orEmpty()
                    val familyName = roomSnapshot.child("familyname").getValue(String::class.java).orEmpty()
                    val name = roomSnapshot.child("name").getValue(String::class.java).orEmpty()
                    val email = roomSnapshot.child("email").getValue(String::class.java).orEmpty()
                    val address = roomSnapshot.child("address").getValue(String::class.java).orEmpty()
                    val phone = roomSnapshot.child("phone").getValue(String::class.java).orEmpty()
                    val image = roomSnapshot.child("image").getValue(String::class.java).orEmpty()

                    Parents(id, familyName, name, email, address,image,phone, "parent")
                }
                rooms = updatedRooms
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("parentsList", "Error loading rooms", error.toException())
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
                onClick = { showAddTeacherForm = true }
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add parents" +
                        "")
            }
        }

        if (showAddTeacherForm) {
            AddParentsForm(onDismiss = { showAddTeacherForm = false })

        }

        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {



            val filteredRooms = rooms.filter { room ->
                room.name.contains(searchQuery, ignoreCase = true) ||
                   room.address.contains(searchQuery, ignoreCase = true) ||
                        room.phone.contains(searchQuery, ignoreCase = true) ||
                        room.familyname.contains(searchQuery, ignoreCase = true) ||
                        room.email.contains(searchQuery, ignoreCase = true)
            }
            items(filteredRooms) { room ->
                ParentsIte(
                    room = room,
                    onDelete = { onDeleteRoom(room) },
                    onUpdate = { onUpdateRoom(room) }
                )
            }
        }
    }
}

@Composable

fun ParentsIte(
    room: Parents,
    onDelete: () -> Unit,
    onUpdate: () -> Unit

) {
    var name by remember { mutableStateOf(room.name) }
    var email by remember { mutableStateOf(room.email) }
    var role by remember { mutableStateOf(room.id) }
    var address by remember { mutableStateOf(room.address) }
    var phone by remember { mutableStateOf(room.phone) }
    var familyName by remember { mutableStateOf(room.familyname) }
    var image by remember { mutableStateOf(room.image) }

    var updatedImage by remember { mutableStateOf<String?>(null) }
    var imageUpdated by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) } // تتبع حالة وضع التحرير
    val getContent = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            updatedImage = uri.toString()
            imageUpdated = true
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this parent?") },
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
            title = { Text("parent Details") },
            text = {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Box {
                        Column(  horizontalAlignment = Alignment.CenterHorizontally
                        ) {


                            FirebaseImage(url = room.image)

                            if (editMode){
                                IconButton(
                                    onClick = {
                                        getContent.launch("image/*")
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Delete parent")
                                }



                            }
                        } }

                    OutlinedTextField(
                        value = familyName,
                        onValueChange = { familyName = it  },
                        label = { Text("Family Name") },
                        readOnly = !editMode // قراءة فقط في وضع العرض
                    )

                    // تغيير حقول النص القراءة فقط إلى حقول النص القابلة للتحرير
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name=it/* تحديث القيمة عند التغيير */ },
                        label = { Text("Name") },
                        readOnly = !editMode // قراءة فقط في وضع العرض
                    )

                    // حقل النص لعرض قائمة الدورات



                    // تغيير حقول النص القراءة فقط إلى حقول النص القابلة للتحرير
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email=it/* تحديث القيمة عند التغيير */ },
                        label = { Text("Email") },
                        readOnly = !editMode // قراءة فقط في وضع العرض
                    )

                    // تغيير حقول النص القراءة فقط إلى حقول النص القابلة للتحرير
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone=it/* تحديث القيمة عند التغيير */ },
                        label = { Text("Phone") },
                        readOnly = !editMode // قراءة فقط في وضع العرض
                    )

                    // تغيير حقول النص القراءة فقط إلى حقول النص القابلة للتحرير
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address=it/* تحديث القيمة عند التغيير */ },
                        label = { Text("Address") },
                        readOnly = !editMode // قراءة فقط في وضع العرض
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // إغلاق مربع حوار المعلومات
                        showInfoDialog = false
                        // إذا كان في وضع التحرير، احفظ التحديثات
                        if (editMode) {

                            // احفظ التحديثات
                            if (imageUpdated) {
                                // Update the image
                                val storageRef = Firebase.storage.reference.child("images/ProfileUsers/${room.id}")
                                val uploadTask = storageRef.putFile(Uri.parse(updatedImage))

                                uploadTask.addOnCompleteListener { uploadTask ->
                                    if (uploadTask.isSuccessful) {
                                        storageRef.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                                            if (downloadUrlTask.isSuccessful) {
                                                val downloadUri = downloadUrlTask.result
                                                val imageUrl = downloadUri.toString()

                                                // Update the image URL in Realtime Database
                                                updateParentsData(room.id, familyName, name, email, address, phone, imageUrl)

                                            }
                                        }
                                    } else {
                                        uploadTask.exception?.let {
                                            // Handle the exception
                                        }
                                    }
                                }
                            } else {
                                // Use the current image
                                updateParentsData(room.id, familyName, name, email, address, phone, image)

                            }
                            // اخرج من وضع التحرير بعد حفظ التحديثات
                            editMode = false
                        }
                    }
                ) {
                    Text(if (editMode) "Save" else "Close") // تغيير نص الزر حسب وضع التحرير
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
                FirebaseImage(url = room.image)
            }
            Column(
                modifier = Modifier.weight(1f)
            )    {
                Text(
                    text = "${room.familyname} ${room.name}",
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))
                Row( verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 20.dp)) {
                    Text(
                        text = "",
                        style = TextStyle(
                            fontFamily = FontFamily.Default,
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    )


                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = room.email,
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp,
                        color = Color.Gray
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )

            }

            IconButton(
                onClick = { showInfoDialog = true
                    editMode  = true}  ,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit parent")
            }

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete parent")
            }
        }
    }
}
fun updateParentsData(uid: String, familyName: String, name: String, email: String, address: String, phone: String, image: String) {
    val userRef = database.reference.child("users").child("parents").child(uid)

    // تحديث البيانات
    val data: Map<String, Any> = hashMapOf(
        "name" to name,
        "familyname" to familyName,
        "email" to email,
        "address" to address,
        "phone" to phone,
        "image" to image,


    )

    // تحديث البيانات في Firebase Realtime Database
    userRef
        .updateChildren(data)
        .addOnSuccessListener {
            // التحديث ناجح
        }
        .addOnFailureListener {
            // حدث خطأ أثناء التحديث
        }
}


@Composable
fun AddParentsForm(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var familyName by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    var selectImage by remember { mutableStateOf<String?>(null) }
    var imageSelect by remember { mutableStateOf(false) }


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
    AlertDialog(
        onDismissRequest = {  onDismiss()  },
        title = { Text("Add New Parent") },
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
                    onClick = {
                        getContent.launch("image/*")
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(if (selectImage != null) "Selected Image" else "Select Image")
                }

                OutlinedTextField(
                    value = familyName,
                    onValueChange = { familyName = it },
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





            }},
        confirmButton = {
            Button(
                onClick = {


                    if (email.isEmpty() || password.isEmpty() || !isEmailValid) {
                        // Handle empty or invalid email or password
                        return@Button
                    }
                    val adminA = TeacherCreatUser(
                        familyName,
                        name,
                        email,
                        address,
                        "",
                        phone,
                        "parent",

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

                                                        FirebaseDatabase.getInstance()
                                                            .reference.child("users").child("parents")
                                                            .child(uid).setValue(adminA)
                                                    }
                                                }
                                            } else {
                                                uploadTask.exception?.let {
                                                    // Handle the exception
                                                }
                                            }
                                        }
                                    } else {


                                        FirebaseDatabase.getInstance().reference.child("users")
                                            .child("parents").child(uid).setValue(adminA)
                                    }

                                    name = ""
                                    email = ""
                                    password = ""
                                    address = ""
                                    phone = ""
                                    familyName = ""
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



fun deleteParentsDatabase(context: Context, room: Parents) {
    val database = Firebase.database
    val roomsRef = database.getReference("users/parents").child(room.id)
    roomsRef.removeValue()
    Toast.makeText(context, "Teacher deleted", Toast.LENGTH_SHORT).show()
}
@Composable
fun FirebaseImage(url: String) {
    val painter = rememberImagePainter(url)
    Image(
        painter = painter,
        contentDescription = "Profile Picture",
        modifier = Modifier
            .size(80.dp)
            .clip(shape = CircleShape), // تحويل الصورة إلى دائرة
        contentScale = ContentScale.Crop,
    )}