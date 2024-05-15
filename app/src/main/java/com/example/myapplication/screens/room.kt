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

data class Room(val id: String, val name: String, val capacity: String)
@Composable
fun RoomList(
    innerPadding: PaddingValues,
    onClickAdd: () -> Unit,
    onDeleteRoom: (Room) -> Unit,
    onUpdateRoom: (Room) -> Unit
) {
    var rooms by remember { mutableStateOf(listOf<Room>()) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val database = Firebase.database
        val roomsRef = database.getReference("classrooms")

        roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedRooms = snapshot.children.mapNotNull { roomSnapshot ->
                    val id = roomSnapshot.key.orEmpty()
                    val name = roomSnapshot.child("name").getValue(String::class.java).orEmpty()
                    val capacity = roomSnapshot.child("capacity").getValue(String::class.java).orEmpty()
                    Room(id, name, capacity)
                }
                rooms = updatedRooms
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomList", "Error loading rooms", error.toException())
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
                Icon(Icons.Default.AddCircle, contentDescription = "Add Room")
            }
        }

        if (showAddDialog) {
            RoomAdd(
                onAddRoom = {
                    addRoomDatabase(it.roomName, it.capacity)
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
            val filteredRooms = rooms.filter { room ->
                room.name.contains(searchQuery, ignoreCase = true) ||
                        room.capacity.contains(searchQuery, ignoreCase = true)
            }
            items(filteredRooms) { room ->
                RoomItem(
                    room = room,
                    onDelete = { onDeleteRoom(room) },
                    onUpdate = { onUpdateRoom(room) }
                )
            }
        }
    }
}

@Composable

fun RoomItem(
    room: Room,
    onDelete: () -> Unit,
    onUpdate: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this room?") },
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
            )    {
                Text(
                    text = "${room.name}",
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row( verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = "capacity: ",
                        style = TextStyle(
                            fontFamily = FontFamily.Default,
                            fontSize = 18.sp,

                            color = Color.Gray
                        ),

                        )
                    Text(
                        text = "${room.capacity}",
                        style = TextStyle(
                            fontFamily = FontFamily.Default,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        ),

                        ) }
            }

            IconButton(
                onClick = onUpdate,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Room")
            }

            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Room")
            }
        }
    }
}

@Composable
fun RoomScreen(innerPadding: PaddingValues) {
    var selectedAdmi by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedRoom: Room? by remember { mutableStateOf(null) }

    Column {
        RoomList(
            innerPadding = innerPadding,
            onClickAdd = {},
            onDeleteRoom = { room -> deleteRoomDatabase(context, room) },
            onUpdateRoom = { room ->
                showEditDialog = true
                selectedRoom = room
            }
        )
    }

    if (showEditDialog && selectedRoom != null) {
        RoomUpdateDialog(
            room = selectedRoom!!,

            onCloseDialog = {
                showEditDialog = false
                selectedRoom = null
            }
        )
    }
}



data class RoomData(val roomName: String, val capacity: String)
@Composable
fun RoomAdd(onAddRoom: (RoomData) -> Unit, onCloseDialog: () -> Unit) {
    var roomName by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onCloseDialog() }, // إغلاق الحوار عند النقر خارجه
        title = { Text(text = "Add Class Room") },
        text = {
            Column {
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Room Name") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Capacity") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roomName.isNotBlank() && capacity.isNotBlank()) {
                        onAddRoom(RoomData(roomName, capacity))
                        roomName = ""
                        capacity = ""
                        onCloseDialog() // إغلاق الحوار بعد إضافة الغرفة بنجاح
                    }
                }
            ) {
                Text("Add")
            }
        }
    )
}




fun addRoomDatabase(roomName: String, capacity: String) {
    val database = Firebase.database
    val roomsRef = database.getReference("classrooms")

    // Generate a unique key for the new room
    val newRoomRef = roomsRef.push()

    // Set the room data
    newRoomRef.setValue(mapOf("name" to roomName, "capacity" to capacity))
}
fun deleteRoomDatabase(context: Context, room: Room) {
    val database = Firebase.database
    val roomsRef = database.getReference("classrooms").child(room.id)
    roomsRef.removeValue()
    Toast.makeText(context, "Room deleted", Toast.LENGTH_SHORT).show()
}

fun updateRoomDatabase( id: String,name: String,capacity: String) {
    val database = Firebase.database
    val roomsRef = database.getReference("classrooms").child(id)

    // Update the room data
    val updates = mapOf<String, Any>(
        "name" to name,
        "capacity" to capacity
    )
    roomsRef.updateChildren(updates)
}

@Composable
fun RoomUpdateDialog(
    room: Room,
    onCloseDialog: () -> Unit
) {
    var roomName by remember { mutableStateOf(room.name) }
    var capacity by remember { mutableStateOf(room.capacity) }

    AlertDialog(
        onDismissRequest = { onCloseDialog() },
        title = { Text(text = "Update Class Room") },
        text = {
            Column {
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Room Name") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Capacity") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roomName.isNotBlank() && capacity.isNotBlank()) {
                        // Move the updateRoomDatabase call here
                        updateRoomDatabase(room.id, roomName, capacity)
                        onCloseDialog()
                    }
                }
            ) {
                Text("Update")
            }
        }

    )
}