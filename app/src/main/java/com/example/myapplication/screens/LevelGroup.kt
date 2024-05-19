package com.example.myapplication

import android.content.Context
import android.util.Log // استيراد مكتبة Log من Android
import android.widget.Toast // استيراد مكتبة Toast من Android
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowForward

// البيانات الأساسية لتعريف المستوى والمجموعة
data class Level(val id: String, val name: String)
data class Group(val id: String, var name: String)

// دالة Composable لعرض قائمة المستويات
@Composable
fun LevelList(
    innerPadding: PaddingValues,
    onClickAdd: () -> Unit,
    onDeleteRoom: (Level) -> Unit,
    onUpdateRoom: (Level) -> Unit,
    onSelectRoom: (Level) -> Unit
) {
    // المتغيرات لحفظ حالة الغرف والبحث وعرض نافذة الإضافة
    var rooms by remember { mutableStateOf(listOf<Level>()) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // تحميل البيانات من Firebase عند إنشاء المكون
    LaunchedEffect(Unit) {
        val database = Firebase.database
        val roomsRef = database.getReference("levels")

        roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedRooms = snapshot.children.mapNotNull { roomSnapshot ->
                    val id = roomSnapshot.key.orEmpty()
                    val name = roomSnapshot.child("name").getValue(String::class.java).orEmpty()
                    Level(id, name)
                }
                rooms = updatedRooms
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomList", "Error loading rooms", error.toException())
            }
        })
    }

    // واجهة المستخدم لعرض المستويات
    Column(modifier = Modifier.padding(innerPadding)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp, top = 16.dp)
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
            LevelAdd(
                onAddRoom = { addLevelDatabase(it.roomName) },
                onCloseDialog = { showAddDialog = false }
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filteredRooms = rooms.filter { room ->
                room.name.contains(searchQuery, ignoreCase = true)
            }
            items(filteredRooms) { room ->
                LevelItem(
                    room = room,
                    onDelete = { onDeleteRoom(room) },
                    onUpdate = { onUpdateRoom(room) },
                    onClick = { onSelectRoom(room) }
                )
            }
        }
    }
}

// مكون لعرض عنصر المستوى
@Composable
fun LevelItem(
    room: Level,
    onDelete: () -> Unit,
    onUpdate: () -> Unit,
    onClick: () -> Unit
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.name,
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            IconButton(onClick = onUpdate, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Room")
            }
            IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Room")
            }
        }
    }
}

// دالة Composable لعرض قائمة المستويات والمجموعات
@Composable
fun LevelGroup(innerPadding: PaddingValues) {
    var selectedAdmin by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedRoom: Level? by remember { mutableStateOf(null) }
    var rooms by remember { mutableStateOf(listOf<Level>()) }
    var groups by remember { mutableStateOf(mapOf<String, List<Group>>()) }
    var selectedGroups by remember { mutableStateOf<List<Group>>(emptyList()) }
    var selectedLevel by remember { mutableStateOf<Level?>(null) }
    var searchGroupQuery by remember { mutableStateOf("") }
    var showAddGroupDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val database = Firebase.database
        val roomsRef = database.getReference("levels")

        roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedRooms = snapshot.children.mapNotNull { roomSnapshot ->
                    val id = roomSnapshot.key.orEmpty()
                    val name = roomSnapshot.child("name").getValue(String::class.java).orEmpty()
                    Level(id, name)
                }
                rooms = updatedRooms

                updatedRooms.forEach { room ->
                    val groupsRef = database.getReference("levels/${room.id}/groups")
                    groupsRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(groupsSnapshot: DataSnapshot) {
                            val groupList = groupsSnapshot.children.mapNotNull { groupSnapshot ->
                                val groupId = groupSnapshot.key.orEmpty()
                                val groupName = groupSnapshot.child("name").getValue(String::class.java).orEmpty()
                                Group(groupId, groupName)
                            }
                            groups += (room.id to groupList)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("GroupList", "Error loading groups for room ${room.id}", error.toException())
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomList", "Error loading rooms", error.toException())
            }
        })
    }

    Column {
        if (selectedLevel == null) {
            LevelList(
                innerPadding = innerPadding,
                onClickAdd = {},
                onDeleteRoom = { room -> deleteLevelDatabase(context, room) },
                onUpdateRoom = { room ->
                    showEditDialog = true
                    selectedRoom = room
                },
                onSelectRoom = { room ->
                    selectedLevel = room
                    selectedGroups = groups[room.id].orEmpty()
                }
            )
        } else {
            Column(modifier = Modifier.padding(innerPadding)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    IconButton(
                        onClick = { /* Perform search action */ }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    OutlinedTextField(
                        value = searchGroupQuery,
                        onValueChange = { searchGroupQuery = it },
                        label = { Text("Search Groups ${selectedLevel!!.name}") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { showAddGroupDialog = true },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Group")
                    }
                }

                if (showAddGroupDialog) {
                    GroupAdd(
                        onAddGroup = { addGroupDatabase(selectedLevel!!.id, it.groupName) },
                        onCloseDialog = { showAddGroupDialog = false }
                    )
                }

                val filteredGroups = selectedGroups.filter { group ->
                    group.name.contains(searchGroupQuery, ignoreCase = true)
                }

                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredGroups) { group ->
                        GroupItem(
                            group = group,
                            onDelete = { deleteGroupDatabase(selectedLevel!!.id, group) },
                            onUpdate = { updateGroupDatabase(selectedLevel!!.id, group) }
                        )
                    }
                }
                IconButton(onClick = { selectedLevel = null }, modifier = Modifier.size(40.dp).align(Alignment.End).padding(top = 16.dp)) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Back to Levels")
                }

            }
        }
    }

    if (showEditDialog && selectedRoom != null) {
        LevelUpdateDialog(
            room = selectedRoom!!,
            onCloseDialog = {
                showEditDialog = false
                selectedRoom = null
            }
        )
    }
}


// مكون لعرض عنصر المجموعة
@Composable

fun GroupItem(
    group: Group,
    onDelete: () -> Unit,
    onUpdate: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var updatedGroupName by remember { mutableStateOf(group.name) } // تخزين اسم المجموعة المحدث

    if (showDeleteDialog) {
        // حوار الحذف
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this group?") },
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

    if (showUpdateDialog) {
        // حوار التحديث
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Update Group Name") },
            text = {
                OutlinedTextField(
                    value = updatedGroupName,
                    onValueChange = { updatedGroupName = it },
                    label = { Text("New Group Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // تحديث اسم المجموعة بعد الضغط على زر التحديث
                        group.name = updatedGroupName
                        onUpdate() // استدعاء دالة التحديث
                        showUpdateDialog = false
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showUpdateDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showUpdateDialog = true },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            IconButton(onClick = { showUpdateDialog = true }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Group")
            }
            IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Group")
            }
        }
    }
}

// مكون لإضافة مجموعة جديدة
@Composable
fun GroupAdd(onAddGroup: (GroupData) -> Unit, onCloseDialog: () -> Unit) {
    var groupName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onCloseDialog() },
        title = { Text("Add Group") },
        text = {
            Column {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (groupName.isNotBlank()) {
                        onAddGroup(GroupData(groupName))
                        groupName = ""
                        onCloseDialog()
                    }
                }
            ) {
                Text("Add")
            }
        }
    )
}

// مكون لتحديث بيانات مجموعة
@Composable
fun GroupUpdateDialog(
    group: Group,
    onUpdateGroup: (String) -> Unit,
    onCloseDialog: () -> Unit
) {
    var groupName by remember { mutableStateOf(group.name) }

    AlertDialog(
        onDismissRequest = { onCloseDialog() },
        title = { Text("Update Group") },
        text = {
            Column {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (groupName.isNotBlank()) {
                        onUpdateGroup(groupName)
                        onCloseDialog()
                    }
                }
            ) {
                Text("Update")
            }
        }
    )
}

// البيانات الأساسية لإضافة مجموعة
data class GroupData(val groupName: String)

// دالة لإضافة مجموعة جديدة في قاعدة البيانات
fun addGroupDatabase(levelId: String, groupName: String) {
    val database = Firebase.database
    val groupsRef = database.getReference("levels/$levelId/groups")

    // إنشاء مرجع جديد لمجموعة جديدة
    val newGroupRef = groupsRef.push()

    // ضبط بيانات المجموعة الجديدة
    newGroupRef.setValue(mapOf("name" to groupName))
}

// دالة لحذف مجموعة من قاعدة البيانات
fun deleteGroupDatabase(levelId: String, group: Group) {
    val database = Firebase.database
    val groupsRef = database.getReference("levels/$levelId/groups").child(group.id)
    groupsRef.removeValue()
}

// دالة لتحديث بيانات مجموعة في قاعدة البيانات
fun updateGroupDatabase(levelId: String, group: Group) {
    val database = Firebase.database
    val groupsRef = database.getReference("levels/$levelId/groups").child(group.id)

    // تحديث بيانات المجموعة
    val updates = mapOf<String, Any>(
        "name" to group.name
    )
    groupsRef.updateChildren(updates)
}

// مكون لتحديث بيانات مستوى
@Composable
fun LevelUpdateDialog(
    room: Level,
    onCloseDialog: () -> Unit
) {
    var roomName by remember { mutableStateOf(room.name) }

    AlertDialog(
        onDismissRequest = { onCloseDialog() },
        title = { Text("Update Class Room") },
        text = {
            Column {
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Room Name") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roomName.isNotBlank()) {
                        updateLevelDatabase(room.id, roomName)
                        onCloseDialog()
                    }
                }
            ) {
                Text("Update")
            }
        }
    )
}

// مكون لإضافة مستوى جديد
@Composable
fun LevelAdd(onAddRoom: (LevelData) -> Unit, onCloseDialog: () -> Unit) {
    var roomName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onCloseDialog() },
        title = { Text("Add Class Room") },
        text = {
            Column {
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Room Name") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roomName.isNotBlank()) {
                        onAddRoom(LevelData(roomName))
                        roomName = ""
                        onCloseDialog()
                    }
                }
            ) {
                Text("Add")
            }
        }
    )
}

// البيانات الأساسية لإضافة مستوى
data class LevelData(val roomName: String)

// دالة لإضافة مستوى جديد في قاعدة البيانات
fun addLevelDatabase(roomName: String) {
    val database = Firebase.database
    val roomsRef = database.getReference("levels")

    val newRoomRef = roomsRef.push()

    newRoomRef.setValue(mapOf("name" to roomName))
}

// دالة لحذف مستوى من قاعدة البيانات
fun deleteLevelDatabase(context: Context, room: Level) {
    val database = Firebase.database
    val roomsRef = database.getReference("levels").child(room.id)
    roomsRef.removeValue()
    Toast.makeText(context, "Room deleted", Toast.LENGTH_SHORT).show()
}

// دالة لتحديث بيانات مستوى في قاعدة البيانات
fun updateLevelDatabase(id: String, name: String) {
    val database = Firebase.database
    val roomsRef = database.getReference("levels").child(id)

    val updates = mapOf<String, Any>("name" to name)
    roomsRef.updateChildren(updates)
}




/*
/*

@Composable
fun LevelGroup(innerPadding: PaddingValues) {
    val (levels, setLevels) = remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(Unit) {
        getLevelsFromDatabase { fetchedLevels ->
            setLevels(fetchedLevels)
        }
    }

    // State for adding a new level
    var showAddLevelDialog by remember { mutableStateOf(false) }
    var newLevelName by remember { mutableStateOf("") }

    // State for adding a new group
    var showAddGroupDialog by remember { mutableStateOf(false) }
    var selectedLevelIndex by remember { mutableStateOf(0) }
    var newGroupName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // Button to add a new level
        Button(
            onClick = { showAddLevelDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add New Level")
        }

        // List of levels
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(levels) { level ->
                Text(
                    text = level,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable {
                            // Handle click on level
                        }
                )
            }
        }

        // Dialog to add a new level
        if (showAddLevelDialog) {
            AlertDialog(
                onDismissRequest = { showAddLevelDialog = false },
                title = { Text("Add New Level") },
                text = {
                    OutlinedTextField(
                        value = newLevelName,
                        onValueChange = { newLevelName = it },
                        label = { Text("Level Name") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Add new level to database
                            if (newLevelName.isNotBlank()) {
                                // Add new level to Firebase
                                addNewLevel(newLevelName)
                                // Reset the state
                                newLevelName = ""
                                showAddLevelDialog = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showAddLevelDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog to add a new group
        if (showAddGroupDialog) {
            AlertDialog(
                onDismissRequest = { showAddGroupDialog = false },
                title = { Text("Add New Group") },
                text = {
                    Column {
                        Text("Select Level:")
                        DropdownMenu(
                            expanded = showAddGroupDialog,
                            onDismissRequest = { showAddGroupDialog = false },
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            levels.forEachIndexed { index, level ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedLevelIndex = index
                                        showAddGroupDialog = false
                                    }
                                , text =  {
                                    Text(level)
                                })
                            }
                        }
                        OutlinedTextField(
                            value = newGroupName,
                            onValueChange = { newGroupName = it },
                            label = { Text("Group Name") }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Add new group to database
                            if (newGroupName.isNotBlank()) {
                                // Add new group to Firebase under selected level
                                addNewGroup(levels[selectedLevelIndex], newGroupName)
                                // Reset the state
                                newGroupName = ""
                                showAddGroupDialog = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showAddGroupDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// Function to add a new level to Firebase
// Function to add a new level to Firebase
private fun addNewLevel(levelName: String) {
    val database = Firebase.database
    val levelsRef = database.getReference("school/levels")

    val newLevel = mapOf(
        levelName to mapOf("name" to levelName, "groups" to emptyMap<String, Any>())
    )

    levelsRef.updateChildren(newLevel)
}

// Function to add a new group to Firebase under a specific level
private fun addNewGroup(levelName: String, groupName: String) {
    val database = Firebase.database
    val levelsRef = database.getReference("school/levels")

    val newGroup = mapOf(
        groupName to mapOf("name" to groupName, "students" to emptyMap<String, Any>())
    )

    levelsRef.child(levelName).child("groups").updateChildren(newGroup)
}

@Composable
fun LevelGrou(innerPadding: PaddingValues) {
    // State to hold the list of levels
    val (levels, setLevels) = remember { mutableStateOf(emptyList<String>()) }

    // Fetch levels from the database when the composable is first composed
    LaunchedEffect(Unit) {
        getLevelsFromDatabase { fetchedLevels ->
            setLevels(fetchedLevels)
        }
    }
    AddLevel(onLevelAdded = {})
    // Pass the levels to the AddLevelGroupPage composable
    AddLevelGroupPage(levels = levels, onLevelGroupAdded = { _, _ -> })

}

@Composable
fun AddLevel(onLevelAdded: (level: String) -> Unit) {
    var levelName by remember { mutableStateOf("") }

    val database = Firebase.database
    val levelsRef = database.getReference("school/levels")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = levelName,
            onValueChange = { levelName = it },
            label = { Text("Level Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (levelName.isNotEmpty()) {
                    val newLevel = mapOf(
                        levelName to mapOf("name" to levelName, "groups" to emptyMap<String, Any>())
                    )

                    levelsRef.updateChildren(newLevel)
                    onLevelAdded(levelName)
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Level")
        }
    }
}
@Composable
fun AddLevelGroupPage(levels: List<String>, onLevelGroupAdded: (level: String, group: String) -> Unit) {
    var selectedLevel by remember { mutableStateOf("") }
    var groupName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val database = Firebase.database
    val levelsRef = database.getReference("school/levels")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top=100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) {
                Text(selectedLevel.takeIf { it.isNotEmpty() } ?: "Select Level")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                levels.forEach { level ->
                    DropdownMenuItem(onClick = {
                        selectedLevel = level
                        expanded = false
                    }, text =  {
                        Text(level)
                    })
                }
            }
        }

        Button(
            onClick = {
                if (selectedLevel.isNotEmpty() && groupName.isNotEmpty()) {
                    val newGroup = mapOf(
                        groupName to mapOf("name" to groupName, "students" to emptyMap<String, Any>())
                    )

                    levelsRef.child(selectedLevel).child("groups").updateChildren(newGroup)
                    onLevelGroupAdded(selectedLevel, groupName)
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Level and Group")
        }
    }
}



// Define a function to fetch levels from the Firebase Realtime Database
fun getLevelsFromDatabase(onLevelsFetched: (List<String>) -> Unit) {
    val database = Firebase.database
    val levelsRef = database.getReference("school/levels")

    // Attach a listener to read the data at our levels reference
    levelsRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val levelsList = mutableListOf<String>()
            for (levelSnapshot in snapshot.children) {
                // Get the level name from each snapshot
                val levelName = levelSnapshot.key
                levelName?.let {
                    levelsList.add(it)
                }
            }
            // Pass the levels list to the callback function
            onLevelsFetched(levelsList)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle errors here
            Log.e("Firebase", "Failed to read levels.", error.toException())
        }
    })
}
*/