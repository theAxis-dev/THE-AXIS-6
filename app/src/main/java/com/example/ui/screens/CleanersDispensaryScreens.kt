package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CleanerAssignment
import com.example.data.model.DispensaryRecord
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AxisDanger
import com.example.ui.theme.AxisPrimary
import com.example.ui.theme.AxisSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CleanersScreen(
    cleaners: List<CleanerAssignment>,
    onAddCleaner: (CleanerAssignment) -> Unit,
    onUpdateStatus: (CleanerAssignment, String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.CleaningServices, contentDescription = null) },
                text = { Text("Assign Duty", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, tint = AxisPrimary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Sanitation & Cleaning Staff Roster", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Ensure dormitories, dining halls and ablution blocks meet high hygiene standards.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            items(cleaners) { cleaner ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cleaner.staffName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            StatusBadge(status = cleaner.status)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Assigned Zone: ${cleaner.zone}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Shift: ${cleaner.shift} • Supervisor: ${cleaner.supervisor}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            val next = if (cleaner.status == "Completed") "Ongoing" else "Completed"
                            TextButton(onClick = { onUpdateStatus(cleaner, next) }) {
                                Text("Toggle Status ($next)", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var zone by remember { mutableStateOf("Dormitory Block A") }
        var shift by remember { mutableStateOf("Morning") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Assign Cleaner Duty", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Staff Member Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = zone,
                        onValueChange = { zone = it },
                        label = { Text("Designated Zone / Block") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = shift,
                        onValueChange = { shift = it },
                        label = { Text("Shift (Morning / Afternoon / Full Day)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onAddCleaner(CleanerAssignment(staffName = name.trim(), zone = zone.trim(), shift = shift.trim()))
                            showAddDialog = false
                        }
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("Assign")
                }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun DispensaryScreen(
    records: List<DispensaryRecord>,
    onAddRecord: (DispensaryRecord) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                text = { Text("Log Patient", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                val admittedCount = records.count { it.admittedToSickBay }
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, tint = AxisDanger, modifier = Modifier.size(30.dp))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Sanatorium & Dispensary Portal", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("$admittedCount Students currently admitted to Sick Bay beds", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            items(records) { record ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(record.studentName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${record.admNo} • ${record.classStream}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (record.admittedToSickBay) {
                                StatusBadge(status = "Admitted to Bed")
                            } else {
                                StatusBadge(status = "Outpatient")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Symptoms: ${record.symptoms}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("Treatment: ${record.treatmentGiven}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(8.dp))
                        val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(Date(record.visitDate))
                        Text("Attending: ${record.attendingNurse} • $formattedDate", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var studentName by remember { mutableStateOf("") }
        var admNo by remember { mutableStateOf("") }
        var classStream by remember { mutableStateOf("Form 2 East") }
        var symptoms by remember { mutableStateOf("") }
        var treatment by remember { mutableStateOf("") }
        var admitToSickBay by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Log Sanatorium Visit", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = studentName, onValueChange = { studentName = it }, label = { Text("Student Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = admNo, onValueChange = { admNo = it }, label = { Text("Admission No (e.g. KB-4203)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = classStream, onValueChange = { classStream = it }, label = { Text("Class Stream") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = symptoms, onValueChange = { symptoms = it }, label = { Text("Symptoms / Complaint") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = treatment, onValueChange = { treatment = it }, label = { Text("Treatment & Medication") }, modifier = Modifier.fillMaxWidth())
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = admitToSickBay, onCheckedChange = { admitToSickBay = it })
                        Text("Admit to Sick Bay (Bed Rest)", fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (studentName.isNotBlank() && symptoms.isNotBlank()) {
                            onAddRecord(
                                DispensaryRecord(
                                    studentName = studentName.trim(),
                                    admNo = admNo.trim(),
                                    classStream = classStream,
                                    symptoms = symptoms.trim(),
                                    treatmentGiven = treatment.trim().ifBlank { "Observation" },
                                    admittedToSickBay = admitToSickBay
                                )
                            )
                            showAddDialog = false
                        }
                    },
                    enabled = studentName.isNotBlank() && symptoms.isNotBlank()
                ) {
                    Text("Save Record")
                }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
        )
    }
}
