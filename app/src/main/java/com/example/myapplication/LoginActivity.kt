package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // التحقق من حالة تسجيل الدخول
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val userRole = sharedPreferences.getString("userRole", null)

        if (isLoggedIn && userRole != null) {
            // إذا كان المستخدم مسجلاً دخوله، توجيهه إلى الشاشة المناسبة بناءً على الدور
            val intent = when (userRole) {
                "admin" -> Intent(this, AdminMainActivity::class.java)
                "parent" -> Intent(this, ParentMainActivity::class.java)
                "teacher" -> Intent(this, TeacherMainActivity::class.java)
                "student" -> Intent(this, StudentMainActivity::class.java)
                else -> Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
        } else {
            // إذا لم يكن مسجلاً دخوله، عرض شاشة تسجيل الدخول
            setContent {
                LoginScreen(this)
            }
        }
    }
}

@Composable
fun LoginScreen(
    context: Context,
    onLoginSuccess: () -> Unit = {},
    onForgotPassword: (email: String) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var sendingResetEmail by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app),
            contentDescription = "Background Image",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = { Text("Email") },
            isError = emailError,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            label = { Text("Password") },
            isError = passwordError,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank()) {
                    emailError = true
                }
                if (password.isBlank()) {
                    passwordError = true
                }
                if (email.isNotBlank() && password.isNotBlank()) {
                    loginUser(context, email, password, onLoginSuccess)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                if (email.isNotBlank()) {
                    sendingResetEmail = true
                    sendPasswordResetEmail(context, email)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (sendingResetEmail) {
                Text("Sending password reset email...")
            } else {
                Text("Send password reset email")
            }
        }
    }
}

fun loginUser(context: Context, email: String, password: String, onLoginSuccess: () -> Unit) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()

                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val uid = user.uid
                    val database = FirebaseDatabase.getInstance().reference
                    val userRef = database.child("users").child("admins").child(uid)
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            if (dataSnapshot.exists()) {
                                saveUserData(context, dataSnapshot, sharedPreferences)
                                navigateToRoleBasedActivity(context, dataSnapshot.child("role").getValue(String::class.java))
                            } else {
                                checkOtherRoles(context, user.uid, sharedPreferences)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            println("Failed to retrieve data: ${databaseError.message}")
                        }
                    })
                }
            } else {
                val errorMessage = task.exception?.message ?: "Unknown error occurred"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
}

fun saveUserData(context: Context, dataSnapshot: DataSnapshot, sharedPreferences: SharedPreferences) {
    with(sharedPreferences.edit()) {
        putBoolean("isLoggedIn", true)
        putString("userId", dataSnapshot.key)
        putString("userRole", dataSnapshot.child("role").getValue(String::class.java))
        putString("userName", dataSnapshot.child("name").getValue(String::class.java))
        putString("userEmail", dataSnapshot.child("email").getValue(String::class.java))
        putString("userFamilyName", dataSnapshot.child("familyname").getValue(String::class.java))
        putString("userImageUrl", dataSnapshot.child("image").getValue(String::class.java))

        // إذا كان المستخدم طالبًا، قم بحفظ المعلومات الإضافية
        if (dataSnapshot.child("role").getValue(String::class.java) == "student") {
            putString("parentId", dataSnapshot.child("parentId").getValue(String::class.java))
            putString("groupId", dataSnapshot.child("groupId").getValue(String::class.java))
            putString("levelId", dataSnapshot.child("levelId").getValue(String::class.java))
        }

        apply()
    }
}

fun navigateToRoleBasedActivity(context: Context, role: String?) {
    val intent = when (role) {
        "admin" -> Intent(context, AdminMainActivity::class.java)
        "parent" -> Intent(context, ParentMainActivity::class.java)
        "teacher" -> Intent(context, TeacherMainActivity::class.java)
        "student" -> Intent(context, StudentMainActivity::class.java)
        else -> Intent(context, MainActivity::class.java)
    }
    context.startActivity(intent)
}

fun checkOtherRoles(context: Context, userId: String, sharedPreferences: SharedPreferences) {
    val database = FirebaseDatabase.getInstance().reference
    val usersRef = database.child("users")

    usersRef.child("parents").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                saveUserData(context, snapshot, sharedPreferences)
                val intent = Intent(context, ParentMainActivity::class.java)
                context.startActivity(intent)
            } else {
                usersRef.child("teachers").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            saveUserData(context, snapshot, sharedPreferences)
                            val intent = Intent(context, TeacherMainActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            usersRef.child("students").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        saveUserData(context, snapshot, sharedPreferences)
                                        val intent = Intent(context, StudentMainActivity::class.java)
                                        context.startActivity(intent)
                                    } else {
                                        // Handle case where role is not found
                                        Toast.makeText(context, "Role not found", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    println("Failed to retrieve data: ${error.message}")
                                }
                            })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("Failed to retrieve data: ${error.message}")
                    }                })
            }
        }

        override fun onCancelled(error: DatabaseError) {
            println("Failed to retrieve data: ${error.message}")
        }
    })
}

fun sendPasswordResetEmail(context: Context, email: String) {
    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
            } else {
                val errorMessage = task.exception?.message ?: "Unknown error occurred"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    // استخدام LocalContext للحصول على كائن السياق
    val context = LocalContext.current
    LoginScreen(context)
}
