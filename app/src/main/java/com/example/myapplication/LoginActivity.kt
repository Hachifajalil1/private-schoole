package com.example.myapplication

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
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
        setContent {
            // تمرير السياق إلى شاشة تسجيل الدخول
            LoginScreen(this)
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
            .verticalScroll(rememberScrollState()), // تمكين التمرير العمودي
        verticalArrangement = Arrangement.Center
    ) {
        // عرض الصورة كخلفية
        Image(
            painter = painterResource(id = R.drawable.screen),
            contentDescription = "Background Image",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f) // يمكن تعديل هذا الرقم لتناسب ارتفاع الصورة
        )

        // حقل النص للبريد الإلكتروني
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

        // حقل كلمة المرور
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

        // زر تسجيل الدخول
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

        // زر إرسال رابط إعادة كلمة المرور
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

// تسجيل الدخول باستخدام Firebase Auth
fun loginUser(context: Context, email: String, password: String, onLoginSuccess: () -> Unit) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // عرض رسالة نجاح تسجيل الدخول
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()

                // فحص صلاحيات المستخدم وتوجيهه إلى الشاشة المناسبة
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val uid = user.uid
                    val database = FirebaseDatabase.getInstance().reference
                    val userRef = database.child("users").child("admins").child(uid)
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // المستخدم هو مسؤول
                                val role = dataSnapshot.child("role").getValue(String::class.java)
                                Toast.makeText(context, role, Toast.LENGTH_SHORT).show()
                                // يمكن توجيه المسؤول إلى النشاط المناسب
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                // المستخدم غير مسؤول، يتم التحقق من دوره
                                val usersRef = database.child("users")
                                val userId = user.uid

                                // تحقق من دور المستخدم (والدي، معلم، طالب)
                                usersRef.child("parents").child(userId)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                // المستخدم هو ولي أمر
                                                val role = snapshot.child("role").getValue(String::class.java)
                                                Toast.makeText(context, role, Toast.LENGTH_SHORT).show()
                                                // يمكن توجيه الوالد إلى النشاط المناسب
                                            } else {
                                                // المستخدم ليس ولي أمر، يتم التحقق من دوره كمعلم
                                                usersRef.child("teachers").child(userId)
                                                    .addListenerForSingleValueEvent(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            if (snapshot.exists()) {
                                                                // المستخدم هو معلم
                                                                val role =
                                                                    snapshot.child("role").getValue(String::class.java)
                                                                Toast.makeText(context, role, Toast.LENGTH_SHORT).show()
                                                                // يمكن توجيه المعلم إلى النشاط المناسب
                                                            } else {
                                                                // المستخدم ليس ولي أمر ولا معلم، يتم التحقق من دوره كطالب
                                                                usersRef.child("students")
                                                                    .addListenerForSingleValueEvent(object :
                                                                        ValueEventListener {
                                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                                            snapshot.children.forEach { level ->
                                                                                level.children.forEach { group ->
                                                                                    group.children.forEach { student ->
                                                                                        if (student.key == userId) {
                                                                                            // المستخدم هو طالب
                                                                                            val role =
                                                                                                student.child("role")
                                                                                                    .getValue(
                                                                                                        String::class.java
                                                                                                    )
                                                                                            Toast.makeText(context, role, Toast.LENGTH_SHORT).show()
                                                                                            // يمكن توجيه الطالب إلى النشاط المناسب
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }

                                                                        override fun onCancelled(error: DatabaseError) {
                                                                            println("فشل في استرداد البيانات: ${error.message}")
                                                                        }
                                                                    })
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            println("فشل في استرداد البيانات: ${error.message}")
                                                        }
                                                    })
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            println("فشل في استرداد البيانات: ${error.message}")
                                        }
                                    })
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            println("فشل في استرداد البيانات: ${databaseError.message}")
                        }
                    })
                }
            } else {
                // عرض رسالة خطأ في حالة فشل تسجيل الدخول
                val errorMessage = task.exception?.message ?: "حدث خطأ غير معروف"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
}

// إرسال رابط إعادة كلمة المرور باستخدام Firebase Auth
fun sendPasswordResetEmail(context: Context, email: String) {
    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // تم إرسال رابط إعادة كلمة المرور بنجاح
                Toast.makeText(context, "تم إرسال رابط إعادة كلمة المرور إلى البريد الإلكتروني", Toast.LENGTH_SHORT).show()
            } else {
                // عرض رسالة خطأ في حالة فشل إرسال رابط إعادة كلمة المرور
                val errorMessage = task.exception?.message ?: "حدث خطأ غير معروف"
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
