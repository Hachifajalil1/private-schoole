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
fun AdminPaymentScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var parents by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedParentId by remember { mutableStateOf<String?>(null) }
    var children by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedChildId by remember { mutableStateOf<String?>(null) }
    var sessionsCount by remember { mutableStateOf("") }
    var courses by remember { mutableStateOf(mapOf<String, String>()) }
    var selectedCourseId by remember { mutableStateOf<String?>(null) }
    var coursePrices by remember { mutableStateOf(mapOf<String, Double>()) }
    var unpaidSessions by remember { mutableStateOf(0) }
    var totalPrice by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        scope.launch {
            parents = fetchParents()
            courses = fetchCoursesWithPrices()
            coursePrices = fetchCoursePrices()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        DropdownMenuSelection(
            label = "Select Parent",
            options = parents,
            selectedOptionId = selectedParentId
        ) { parentId ->
            selectedParentId = parentId
            scope.launch {
                children = fetchChildrenForParent(parentId!!)
                selectedChildId = null
            }
        }

        if (selectedParentId != null) {
            DropdownMenuSelection(
                label = "Select Child",
                options = children,
                selectedOptionId = selectedChildId
            ) { childId ->
                selectedChildId = childId
                scope.launch {
                    if (selectedCourseId != null) {
                        unpaidSessions = fetchUnpaidSessionsCount(selectedChildId!!, selectedCourseId!!)
                    }
                }
            }
        }

        if (selectedChildId != null) {
            DropdownMenuSelection(
                label = "Select Course",
                options = courses,
                selectedOptionId = selectedCourseId
            ) { courseId ->
                selectedCourseId = courseId
                scope.launch {
                    if (selectedChildId != null) {
                        unpaidSessions = fetchUnpaidSessionsCount(selectedChildId!!, selectedCourseId!!)
                        totalPrice = calculateTotalPrice(sessionsCount, coursePrices[selectedCourseId])
                    }
                }
            }
        }

        if (selectedChildId != null && selectedCourseId != null) {
            OutlinedTextField(
                value = unpaidSessions.toString(),
                onValueChange = {},
                label = { Text("Unpaid Sessions Count") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = sessionsCount,
                onValueChange = { input ->
                    if (input.toIntOrNull() ?: 1 <= unpaidSessions) {
                        sessionsCount = input
                        totalPrice = calculateTotalPrice(sessionsCount, coursePrices[selectedCourseId])
                    } else {
                        Toast.makeText(context, "Number of sessions cannot exceed unpaid sessions", Toast.LENGTH_SHORT).show()
                    }
                },
                label = { Text("Number of Sessions") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = totalPrice.toString(),
                onValueChange = {},
                label = { Text("Total Price") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                if (selectedParentId.isNullOrBlank() || selectedChildId.isNullOrBlank() || selectedCourseId.isNullOrBlank() || sessionsCount.isBlank()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else {
                    val sessionsToPay = sessionsCount.toInt()
                    if (sessionsToPay > unpaidSessions) {
                        Toast.makeText(context, "You can't pay for more sessions than unpaid ones", Toast.LENGTH_SHORT).show()
                    } else {
                        scope.launch {
                            savePaymentRecord(selectedParentId!!, selectedChildId!!, selectedCourseId!!, sessionsToPay, totalPrice)
                            Toast.makeText(context, "Payment recorded successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Payment")
        }
    }
}





suspend fun fetchCoursesWithPrices(): Map<String, String> {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("courses")
    val snapshot = ref.get().await()
    val coursesMap = mutableMapOf<String, String>()
    for (courseSnapshot in snapshot.children) {
        val courseId = courseSnapshot.key ?: continue
        val courseName = courseSnapshot.child("name").getValue(String::class.java) ?: "Unknown Course"
        coursesMap[courseId] = courseName
    }
    return coursesMap
}

suspend fun fetchCoursePrices(): Map<String, Double> {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("coursePrices")
    val snapshot = ref.get().await()
    val pricesMap = mutableMapOf<String, Double>()
    for (priceSnapshot in snapshot.children) {
        val courseId = priceSnapshot.key ?: continue
        val price = priceSnapshot.getValue(Double::class.java) ?: 0.0
        pricesMap[courseId] = price
    }
    return pricesMap
}

suspend fun fetchUnpaidSessionsCount(studentId: String, courseId: String): Int {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("attendance")
    val snapshot = ref.get().await()
    var count = 0
    for (attendanceSnapshot in snapshot.children) {
        for (dateSnapshot in attendanceSnapshot.children) {
            val attendanceRecord = dateSnapshot.child(studentId)
            if (attendanceRecord.exists() && attendanceRecord.child("status").getValue(String::class.java) == "Present") {
                val timetableSnapshot = attendanceSnapshot.key?.let { database.getReference("timetable/$it").get().await() }
                val timetableCourseId = timetableSnapshot?.child("courseId")?.getValue(String::class.java)
                val paidStatus = attendanceRecord.child("paid").getValue(Boolean::class.java) ?: false
                if (timetableCourseId == courseId && !paidStatus) {
                    count++
                }
            }
        }
    }
    return count
}



suspend fun savePaymentRecord(parentId: String, childId: String, courseId: String, sessionsCount: Int, totalPrice: Double) {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("payments").push()

    // الحصول على التاريخ الحالي بتنسيق مناسب
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val paymentRecord = mapOf(
        "parentId" to parentId,
        "childId" to childId,
        "courseId" to courseId,
        "sessionsCount" to sessionsCount,
        "totalPrice" to totalPrice,
        "paymentDate" to currentDate // إضافة تاريخ الدفع
    )
    ref.setValue(paymentRecord).await()

    // تحديث حالة الدفع في سجلات الحضور
    val attendanceRef = database.getReference("attendance")
    val attendanceSnapshot = attendanceRef.get().await()
    var sessionsToUpdate = sessionsCount
    for (attendanceSnapshot in attendanceSnapshot.children) {
        for (dateSnapshot in attendanceSnapshot.children) {
            val attendanceRecord = dateSnapshot.child(childId)
            if (attendanceRecord.exists() && attendanceRecord.child("status").getValue(String::class.java) == "Present") {
                val timetableSnapshot = attendanceSnapshot.key?.let { database.getReference("timetable/$it").get().await() }
                val timetableCourseId = timetableSnapshot?.child("courseId")?.getValue(String::class.java)
                val paidStatus = attendanceRecord.child("paid").getValue(Boolean::class.java) ?: false
                if (timetableCourseId == courseId && !paidStatus && sessionsToUpdate > 0) {
                    attendanceRecord.ref.child("paid").setValue(true).await()
                    attendanceRecord.ref.child("paymentDate").setValue(currentDate).await() // حفظ تاريخ الدفع مع الحصة
                    sessionsToUpdate--
                    if (sessionsToUpdate == 0) {
                        break
                    }
                }
            }
        }
        if (sessionsToUpdate == 0) {
            break
        }
    }
}


fun calculateTotalPrice(sessionsCount: String, coursePrice: Double?): Double {
    return (sessionsCount.toIntOrNull() ?: 0) * (coursePrice ?: 0.0)
}

@Composable
fun AdminSessionPricingScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var courses by remember { mutableStateOf(mapOf<String, String>()) }
    var prices by remember { mutableStateOf(mapOf<String, Double>()) }

    LaunchedEffect(Unit) {
        scope.launch {
            courses = fetchCourses()
            prices = fetchCoursePrices()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Set Session Prices", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn {
            items(courses.toList()) { (courseId, courseName) ->
                var price by remember { mutableStateOf(prices[courseId]?.toString() ?: "") }
                val priceLabel = "${prices[courseId]?.toString() ?: "Not Set"}"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = courseName, style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text(priceLabel) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val priceValue = price.toDoubleOrNull()
                                if (priceValue != null) {
                                    scope.launch {
                                        saveCoursePrice(courseId, priceValue)
                                        prices = fetchCoursePrices()
                                        Toast.makeText(context, "Price updated successfully", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Invalid price", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Price")
                        }
                    }
                }
            }
        }
    }
}



suspend fun saveCoursePrice(courseId: String, price: Double) {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("coursePrices/$courseId")
    ref.setValue(price).await()
}
