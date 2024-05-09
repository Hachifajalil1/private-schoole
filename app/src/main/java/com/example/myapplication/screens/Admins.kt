package com.example.myapplication

import android.content.ContentValues // استيراد مكتبة ContentValues من Android
import android.net.Uri // استيراد مكتبة Uri من Android
import android.util.Log // استيراد مكتبة Log من Android
import android.widget.Toast // استيراد مكتبة Toast من Android
import androidx.activity.compose.rememberLauncherForActivityResult // استيراد مكتبة rememberLauncherForActivityResult من Android
import androidx.activity.result.ActivityResultLauncher // استيراد مكتبة ActivityResultLauncher من Android
import androidx.activity.result.contract.ActivityResultContracts // استيراد مكتبة ActivityResultContracts من Android
import androidx.compose.foundation.Image
import androidx.compose.foundation.background // استيراد مكتبة background من compose.foundation
import androidx.compose.foundation.clickable // استيراد مكتبة clickable من compose.foundation
import androidx.compose.foundation.layout.Arrangement // استيراد مكتبة Arrangement من compose.foundation.layout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column // استيراد مكتبة Column من compose.foundation.layout
import androidx.compose.foundation.layout.PaddingValues // استيراد مكتبة PaddingValues من compose.foundation.layout
import androidx.compose.foundation.layout.Row // استيراد مكتبة Row من compose.foundation.layout
import androidx.compose.foundation.layout.Spacer // استيراد مكتبة Spacer من compose.foundation.layout
import androidx.compose.foundation.layout.fillMaxSize // استيراد مكتبة fillMaxSize من compose.foundation.layout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height // استيراد مكتبة height من compose.foundation.layout
import androidx.compose.foundation.layout.padding // استيراد مكتبة padding من compose.foundation.layout
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width // استيراد مكتبة width من compose.foundation.layout
import androidx.compose.foundation.lazy.LazyColumn // استيراد مكتبة LazyColumn من compose.foundation.lazy
import androidx.compose.foundation.lazy.items // استيراد مكتبة items من compose.foundation.lazy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons // استيراد مكتبة Icons من compose.material.icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack // استيراد مكتبة ArrowBack من compose.material.icons.filled
import androidx.compose.material.icons.filled.Check // استيراد مكتبة Check من compose.material.icons.filled
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
import androidx.compose.material3.TextField // استيراد مكتبة TextField من compose.material3
import androidx.compose.runtime.Composable // استيراد مكتبة Composable من compose.runtime
import androidx.compose.runtime.LaunchedEffect // استيراد مكتبة LaunchedEffect من compose.runtime
import androidx.compose.runtime.getValue // استيراد مكتبة getValue من compose.runtime
import androidx.compose.runtime.mutableStateOf // استيراد مكتبة mutableStateOf من compose.runtime
import androidx.compose.runtime.remember // استيراد مكتبة remember من compose.runtime
import androidx.compose.runtime.rememberCoroutineScope // استيراد مكتبة rememberCoroutineScope من compose.runtime
import androidx.compose.runtime.setValue // استيراد مكتبة setValue من compose.runtime
import androidx.compose.ui.Alignment // استيراد مكتبة Alignment من compose.ui
import androidx.compose.ui.Modifier // استيراد مكتبة Modifier من compose.ui
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color // استيراد مكتبة Color من compose.ui
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.room.Update

// تعريف الكلاس Admin
data class Admin(
    val name: String,
    val email: String,
    val uid: String,
    val address: String,
    val phone: String,
    val familyName: String,
    val image: String,
    val role : String
)
data class AdminCreatUser(
    val name: String,
    val email: String,
    val address: String,
    val phone: String,
    val familyName: String,
    var image: String,
    val role : String
)
// تعريف وظيفة Composable لعرض قائمة الادمنز


@Composable
fun AdminList(admins: List<Admin>, innerPadding: PaddingValues, onItemClick: (Admin) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddAdminForm by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .padding(top = 120.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Perform search action */ }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search :  email  name  phone") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            IconButton(
                onClick = { showAddAdminForm = true }
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add admin" +
                        "")
            }
        }

        if (showAddAdminForm) {
            AlertDialog(
                onDismissRequest = { showAddAdminForm = false },
                title = { Text("Add New Admin") },
                text = { AddAdminForm() },
                confirmButton = {
                    IconButton(onClick = {
                        // Add admin logic here
                        showAddAdminForm = false
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(admins.filter {it.phone.contains(searchQuery, ignoreCase = true) ||it.name.contains(searchQuery, ignoreCase = true) ||
                it.familyName.contains(searchQuery, ignoreCase = true) || it.email.contains(
                    searchQuery,
                    ignoreCase = true
                )
            }) { admin ->
                Card(
                    Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onItemClick(admin) }
                        .fillMaxWidth(),
                    RoundedCornerShape(16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            FirebaseImage(url = admin.image)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "${admin.familyName} ${admin.name}",
                                style = TextStyle(
                                    fontFamily = FontFamily.Default,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                ),
                                modifier = Modifier.padding(start = 10.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = admin.email,
                                style = TextStyle(
                                    fontFamily = FontFamily.Default,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


// تعريف وظيفة Composable لعرض تفاصيل الادمن
@Composable
fun AdminDetails(
    admin: Admin,
    innerPadding: PaddingValues,
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit,
    onClose: () -> Unit
) {
    // تعريف المتغيرات اللازمة لعرض تفاصيل الادمن
    var name by remember { mutableStateOf(admin.name) }
    var email by remember { mutableStateOf(admin.email) }
    var role by remember { mutableStateOf(admin.uid) }
    var address by remember { mutableStateOf(admin.address) }
    var phone by remember { mutableStateOf(admin.phone) }
    var familyName by remember { mutableStateOf(admin.familyName) }
    var image by remember { mutableStateOf(admin.image) }
    var imageUrl by remember { mutableStateOf(admin.image) }
    var updatedImage by remember { mutableStateOf<String?>(null) }
    var imageUpdated by remember { mutableStateOf(false) }
    // تعريف CoroutineScope لإستخدام الـ Coroutines
    val scope = rememberCoroutineScope()
    var showAdminDetail by remember { mutableStateOf(false) }
    // استخدام ActivityResultContracts.GetContent() للحصول على محتوى الصورة
    val getContent = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            updatedImage = uri.toString()
            imageUpdated = true
        }
    }

    AlertDialog(
        onDismissRequest = onBackClick,
        title = { Text("Admin Details") },
        text = {
            // تصميم عرض تفاصيل الادمن
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box {
                    FirebaseImage(url = admin.image)
                }

                OutlinedTextField(
                    value = familyName,
                    onValueChange = { familyName = it },
                    label = { Text("Family Name") },
                    )



                // حقل نصي لعرض الاسم
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },

                )

                // حقل نصي لعرض البريد الالكتروني
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },

                )

                // حقل نصي لعرض الهاتف
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },

                )

                // حقل نصي لعرض العنوان
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },

                )



                // زر لاختيار الصورة
                Button(
                    onClick = {
                        getContent.launch("image/*")
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(if (updatedImage != null) "selected image " else "select image")
                }

            }
        },
        confirmButton = {
            Row {
                IconButton(     modifier = Modifier.padding( end  =20.dp,),
                    onClick = {
                        if (imageUpdated) {
                            // Update the image
                            val storageRef = Firebase.storage.reference.child("images/ProfileUsers/${admin.uid}")
                            val uploadTask = storageRef.putFile(Uri.parse(updatedImage))

                            uploadTask.addOnCompleteListener { uploadTask ->
                                if (uploadTask.isSuccessful) {
                                    storageRef.downloadUrl.addOnCompleteListener { downloadUrlTask ->
                                        if (downloadUrlTask.isSuccessful) {
                                            val downloadUri = downloadUrlTask.result
                                            val imageUrl = downloadUri.toString()

                                            // Update the image URL in Realtime Database
                                            updateUserData(admin.uid, familyName, name, email, address, phone, imageUrl)
                                            onBackClick
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
                            updateUserData(admin.uid, familyName, name, email, address, phone, image)
                            onBackClick
                        }
                    }
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "Update")
                }
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(
                    modifier = Modifier.padding(end = 80.dp),
                    onClick = {
                        onDeleteClick()
                    }
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                }

            }
        },



    )
}


// تعريف الحالة المستخدمة لتحميل البيانات
private enum class LoadState {
    Idle, Loading, Loaded
}

// تعريف حالة الادمنز كـ MutableState
private val admins = mutableStateOf(mutableListOf<Admin>())

// وظيفة Composable لعرض قائمة الادمنز
@Composable
fun HomeAdmins(innerPadding: PaddingValues) {
    var selectedAdmin by remember { mutableStateOf<Admin?>(null) }
    var loadState by remember { mutableStateOf(LoadState.Idle) }
    val context = LocalContext.current



    // استخدام القائمة المحدثة
    AdminList(admins.value, innerPadding = innerPadding) { admin ->
        selectedAdmin = admin
    }


    // Selected Admin Details
        selectedAdmin?.let { admin ->
            AdminDetails(
                admin = admin,
                innerPadding = innerPadding,
                onDeleteClick = {
                    deleteUserData(admin.uid)
                    selectedAdmin = null
                    loadState = LoadState.Idle
                },
                onBackClick = {
                    selectedAdmin = null
                    loadState = LoadState.Idle
                }, onClose = {}
            )
        }


        // Load data if not loaded yet
        LaunchedEffect(loadState) {
            if (loadState == LoadState.Idle) {
                loadState = LoadState.Loading
                Firebase.database.reference.child("users").child("admins")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val adminsFromFirebase = mutableListOf<Admin>()
                            for (childSnapshot in snapshot.children) {
                                val name = childSnapshot.child("name").getValue(String::class.java) ?: ""
                                val email = childSnapshot.child("email").getValue(String::class.java) ?: ""
                                val uid = childSnapshot.key.toString()
                                val address = childSnapshot.child("address").getValue(String::class.java) ?: ""
                                val phone = childSnapshot.child("phone").getValue(String::class.java) ?: ""
                                val familyName = childSnapshot.child("familyName").getValue(String::class.java) ?: ""
                                val image = childSnapshot.child("image").getValue(String::class.java) ?: ""
                                val admin = Admin(name, email, uid, address, phone, familyName, image,"admin")
                                adminsFromFirebase.add(admin)
                            }
                            admins.value.clear()
                            admins.value.addAll(adminsFromFirebase)
                            loadState = LoadState.Loaded
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                            loadState = LoadState.Loaded // Handle this differently
                        }
                    })
            }
        }
    }


// Firebase Realtime Database instance
val database = Firebase.database

// دالة لتحديث بيانات المستخدم
fun updateUserData(uid: String, familyName: String, name: String, email: String, address: String, phone: String,  image: String) {
    val userRef = database.reference.child("users").child("admins").child(uid)

    // تحديث البيانات
    val data: Map<String, Any> = hashMapOf(
        "name" to name,
        "familyName" to familyName,
        "email" to email,
        "address" to address,
        "phone" to phone,
        "image" to image
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

// دالة لحذف بيانات المستخدم
fun deleteUserData(uid: String) {
    val userRef = database.reference.child("users").child("admins").child(uid)

    // حذف البيانات من Firebase Realtime Database
    userRef
        .removeValue()
        .addOnSuccessListener {
            // الحذف ناجح
        }
        .addOnFailureListener {
            // حدث خطأ أثناء الحذف
        }
}
@Composable
fun ImageWithFirebase(url: String) {
    Box(modifier = Modifier.size(60.dp)) {
        FirebaseImage(url = url)
    }
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
    )

}@Composable
fun AddAdminForm() {
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



        IconButton(
            onClick = {
                if (email.isEmpty() || password.isEmpty() || !isEmailValid) {
                    // Handle empty or invalid email or password
                    return@IconButton
                }
                val adminA = AdminCreatUser(
                    name,
                    email,
                    address,
                    phone,
                    familyName,
                    "",
                    "admin"
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
                                                        .reference.child("users").child("admins")
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
                                        .child("admins").child(uid).setValue(adminA)
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
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Admin")
        }
    }
}



