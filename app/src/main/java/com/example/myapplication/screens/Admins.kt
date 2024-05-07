package com.example.myapplication


import android.content.ContentValues
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.storage.storage


data class Admin(val name: String, val email: String, val uid: String,val address: String, val phone: String, val familyName: String,val image: String)

@Composable
fun AdminList(admins: List<Admin>,innerPadding: PaddingValues, onItemClick: (Admin) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color.White),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(admins) { admin ->
            Text(
                text = admin.name,
                modifier = Modifier
                    .padding(10.dp)
                    .clickable { onItemClick(admin) }
            )
        }
    }
}

@Composable
fun AdminDetails(
    admin: Admin,
    innerPadding: PaddingValues,
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    var name by remember { mutableStateOf(admin.name) }
    var email by remember { mutableStateOf(admin.email) }
    var role by remember { mutableStateOf(admin.uid) }
    var address by remember { mutableStateOf(admin.address) }
    var phone by remember { mutableStateOf(admin.phone) }
    var familyName by remember { mutableStateOf(admin.familyName) }
    var image by remember { mutableStateOf(admin.image) }
    var imageUrl by remember { mutableStateOf(admin.image) }

    val scope = rememberCoroutineScope()

    val getContent = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {

        }
    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = familyName,
            onValueChange = { familyName = it },
            label = { Text("Family Name") },
            modifier = Modifier.padding(8.dp)
        )

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.padding(8.dp)
        )

        TextField(
            value = image,
            onValueChange = { image = it },
            label = { Text("Image") },
            modifier = Modifier.padding(8.dp)
        )

        Button(
            onClick = {
                getContent.launch("image/*")
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Select Image")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back to Menu")
            }
            Spacer(modifier = Modifier.width(60.dp))
            IconButton(onClick = {
                val storageRef = Firebase.storage.reference.child("images/${admin.uid}")
                val uploadTask = storageRef.putFile(it)

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        imageUrl = downloadUri.toString()

                        // Update image URL in Realtime Database
                        updateUserData(admin.uid, name, role, email, address, phone, familyName, imageUrl)
                    }
                }

            }) {
                Icon(Icons.Filled.Check, contentDescription = "Update User Data")
            }

            Spacer(modifier = Modifier.width(60.dp))
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}






private val admins = mutableStateOf(mutableListOf<Admin>())
private enum class LoadState {
    Idle, Loading, Loaded
}

@Composable
fun HomeAdmins(innerPadding: PaddingValues) {
    var selectedAdmin by remember { mutableStateOf<Admin?>(null) }
    var loadState by remember { mutableStateOf(LoadState.Idle) }
    val context = LocalContext.current

    // Use the top-level admins state
    AdminList(admins.value,innerPadding = innerPadding) { admin ->
        selectedAdmin = admin
    }

    selectedAdmin?.let { admin ->
        AdminDetails(
            admin = admin,
            innerPadding = innerPadding,
            onDeleteClick = { deleteUserData(admin.uid)/* Handle delete click here */ },
            onBackClick = { selectedAdmin = null /* Handle back click here */ },

        )
    }

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
                            val admin = Admin(name, email,uid,address,phone,familyName,image)
                            adminsFromFirebase.add(admin)
                        }
                        // Update the list of admins with data from Firebase
                        admins.value.clear()
                        admins.value.addAll(adminsFromFirebase)
                        loadState = LoadState.Loaded
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                        loadState = LoadState.Loaded // Consider handling this differently
                    }
                })
        }
    }
}
// يمثل Firebase Realtime Database instance
val database = Firebase.database

// دالة لتحديث معلومات المستخدم باستخدام uid
fun updateUserData(uid: String, name: String, role: String, email: String,address: String,phone: String,familyName: String,image: String) {
    // يمثل مسار المستند في Firebase Realtime Database
    val userRef = database.reference.child("users").child("admins").child(uid)

    // يمثل البيانات التي تم تحديثها
    val data: Map<String, Any> = hashMapOf(
        "name" to name,
        "role" to role,
        "email" to email,
        "address" to address,
        "phone" to phone,
        "familyName" to familyName,
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
// يمثل Firebase Realtime Database instance


// دالة لحذف بيانات المستخدم باستخدام uid
fun deleteUserData(uid: String) {

    // يمثل مسار المستند في Firebase Realtime Database
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



