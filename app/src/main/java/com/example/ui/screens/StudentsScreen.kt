package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassStream
import com.example.data.model.Dormitory
import com.example.data.model.Student
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AxisDanger
import com.example.ui.theme.AxisPrimary
import com.example.ui.theme.AxisWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(
    students: List<Student>,
    dorms: List<Dormitory>,
    classes: List<ClassStream>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedClassFilter: String,
    onClassFilterChange: (String) -> Unit,
    onAddStudent: (Student) -> Unit,
    onUpdateStudent: (Student) -> Unit,
    onDeleteStudent: (Student) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedStudentForDetail by remember { mutableStateOf<Student?>(null) }

    // Filter students
    val filteredStudents = remember(students, searchQuery, selectedClassFilter) {
        students.filter { student ->
            val matchQuery = searchQuery.isBlank() ||
                    student.fullName.contains(searchQuery, ignoreCase = true) ||
                    student.admNo.contains(searchQuery, ignoreCase = true) ||
                    student.dormName.contains(searchQuery, ignoreCase = true)

            val matchClass = selectedClassFilter == "All" ||
                    student.classStream.startsWith(selectedClassFilter, ignoreCase = true)

            matchQuery && matchClass
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Add Student", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_student_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search by name, admission no, or dorm…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("student_search_input")
            )

            // Class Filter Chips
            val classFilters = listOf("All", "Form 1", "Form 2", "Form 3", "Form 4")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(classFilters) { filter ->
                    FilterChip(
                        selected = selectedClassFilter == filter,
                        onClick = { onClassFilterChange(filter) },
                        label = { Text(filter, fontSize = 12.sp) }
                    )
                }
            }

            // Results count banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredStudents.size} Enrolled Boarders",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total System: 1,050",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Student Cards List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredStudents, key = { it.id }) { student ->
                    StudentCard(
                        student = student,
                        onClick = { selectedStudentForDetail = student }
                    )
                }
            }
        }
    }

    // Add Student Dialog
    if (showAddDialog) {
        AddStudentDialog(
            dorms = dorms,
            classes = classes,
            onDismiss = { showAddDialog = false },
            onConfirm = { newStudent ->
                onAddStudent(newStudent)
                showAddDialog = false
            }
        )
    }

    // Student Detail / Edit Modal
    selectedStudentForDetail?.let { student ->
        StudentDetailDialog(
            student = student,
            dorms = dorms,
            onDismiss = { selectedStudentForDetail = null },
            onSave = { updated ->
                onUpdateStudent(updated)
                selectedStudentForDetail = null
            },
            onDelete = {
                onDeleteStudent(student)
                selectedStudentForDetail = null
            }
        )
    }
}

@Composable
private fun StudentCard(
    student: Student,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initials = student.fullName.split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2).joinToString("")

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AxisPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = AxisPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = student.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    StatusBadge(status = student.status)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${student.admNo} • ${student.classStream}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${student.dormName} (${student.bedNumber})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (student.medicalNotes != "None" && student.medicalNotes.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = AxisDanger,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Medical alert",
                                fontSize = 11.sp,
                                color = AxisDanger,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddStudentDialog(
    dorms: List<Dormitory>,
    classes: List<ClassStream>,
    onDismiss: () -> Unit,
    onConfirm: (Student) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var admNo by remember { mutableStateOf("KB-${(4210..4999).random()}") }
    var selectedClass by remember { mutableStateOf(classes.firstOrNull()?.name ?: "Form 1 North") }
    var selectedDorm by remember { mutableStateOf(dorms.firstOrNull()?.name ?: "Mount Kenya") }
    var bedNumber by remember { mutableStateOf("B-12 Low") }
    var guardianName by remember { mutableStateOf("") }
    var guardianPhone by remember { mutableStateOf("+254 7") }
    var medicalNotes by remember { mutableStateOf("None") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Boarding Student", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = admNo,
                    onValueChange = { admNo = it },
                    label = { Text("Admission Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = selectedClass,
                    onValueChange = { selectedClass = it },
                    label = { Text("Class Stream") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = selectedDorm,
                    onValueChange = { selectedDorm = it },
                    label = { Text("Dormitory") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bedNumber,
                    onValueChange = { bedNumber = it },
                    label = { Text("Bed & Bunk Position") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = guardianPhone,
                    onValueChange = { guardianPhone = it },
                    label = { Text("Parent / Guardian Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = medicalNotes,
                    onValueChange = { medicalNotes = it },
                    label = { Text("Medical Notes / Allergies") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank()) {
                        onConfirm(
                            Student(
                                admNo = admNo,
                                fullName = fullName.trim(),
                                classStream = selectedClass,
                                dormName = selectedDorm,
                                bedNumber = bedNumber,
                                guardianName = guardianName.ifBlank { "Parent" },
                                guardianPhone = guardianPhone,
                                medicalNotes = medicalNotes
                            )
                        )
                    }
                },
                enabled = fullName.isNotBlank()
            ) {
                Text("Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun StudentDetailDialog(
    student: Student,
    dorms: List<Dormitory>,
    onDismiss: () -> Unit,
    onSave: (Student) -> Unit,
    onDelete: () -> Unit
) {
    var status by remember { mutableStateOf(student.status) }
    var dormName by remember { mutableStateOf(student.dormName) }
    var bedNumber by remember { mutableStateOf(student.bedNumber) }
    var medicalNotes by remember { mutableStateOf(student.medicalNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(student.fullName, fontWeight = FontWeight.Bold)
                Text(
                    "${student.admNo} • ${student.classStream}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Guardian: ${student.guardianName} (${student.guardianPhone})", fontSize = 13.sp)

                OutlinedTextField(
                    value = dormName,
                    onValueChange = { dormName = it },
                    label = { Text("Dormitory Assignment") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bedNumber,
                    onValueChange = { bedNumber = it },
                    label = { Text("Bed Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = medicalNotes,
                    onValueChange = { medicalNotes = it },
                    label = { Text("Medical Record") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Boarding Status:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Active", "Dispensary", "On Leave").forEach { opt ->
                        FilterChip(
                            selected = status == opt,
                            onClick = { status = opt },
                            label = { Text(opt, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    student.copy(
                        dormName = dormName,
                        bedNumber = bedNumber,
                        medicalNotes = medicalNotes,
                        status = status
                    )
                )
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) {
                    Text("Delete", color = AxisDanger)
                }
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    )
}
