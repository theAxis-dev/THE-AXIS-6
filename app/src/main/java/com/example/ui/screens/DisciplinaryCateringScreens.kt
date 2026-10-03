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
import com.example.data.model.DisciplinaryCase
import com.example.data.model.MealSchedule
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AxisDanger
import com.example.ui.theme.AxisPrimary
import com.example.ui.theme.AxisTertiary

@Composable
fun DisciplinaryScreen(
    cases: List<DisciplinaryCase>,
    onAddCase: (DisciplinaryCase) -> Unit,
    onUpdateStatus: (DisciplinaryCase, String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Gavel, contentDescription = null) },
                text = { Text("Log Incident", fontWeight = FontWeight.Bold) },
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
                        Icon(Icons.Default.Balance, contentDescription = null, tint = AxisTertiary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Boarding Disciplinary Committee", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Maintain dorm rules, prep attendance, and fair behavioral corrective measures.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            items(cases) { c ->
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
                            Text("${c.studentName} (${c.admNo})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            StatusBadge(status = c.status)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Offense: ${c.offense}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Sanction: ${c.sanction}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Hearing Officer: ${c.hearingOfficer}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            val next = if (c.status == "Active") "Completed" else "Active"
                            TextButton(onClick = { onUpdateStatus(c, next) }) {
                                Text("Mark $next", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var studentName by remember { mutableStateOf("") }
        var admNo by remember { mutableStateOf("") }
        var offense by remember { mutableStateOf("") }
        var sanction by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Log Disciplinary Case", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = studentName, onValueChange = { studentName = it }, label = { Text("Student Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = admNo, onValueChange = { admNo = it }, label = { Text("Admission Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = offense, onValueChange = { offense = it }, label = { Text("Nature of Infraction") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sanction, onValueChange = { sanction = it }, label = { Text("Corrective Duty / Penalty") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (studentName.isNotBlank() && offense.isNotBlank()) {
                            onAddCase(DisciplinaryCase(studentName = studentName.trim(), admNo = admNo.trim(), offense = offense.trim(), sanction = sanction.trim()))
                            showAddDialog = false
                        }
                    },
                    enabled = studentName.isNotBlank() && offense.isNotBlank()
                ) {
                    Text("Record Case")
                }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun CateringScreen(
    meals: List<MealSchedule>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = AxisPrimary, modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Dining Hall & Catering Timetable", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Balanced boarding nutrition menu, kitchen stores, and allergy safeguards.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        items(meals) { meal ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = meal.dayOfWeek.uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    MealRow(icon = "☕", mealTime = "Breakfast (06:30 AM)", menu = meal.breakfast)
                    Spacer(modifier = Modifier.height(8.dp))
                    MealRow(icon = "🍲", mealTime = "Lunch (12:45 PM)", menu = meal.lunch)
                    Spacer(modifier = Modifier.height(8.dp))
                    MealRow(icon = "🍛", mealTime = "Supper (07:00 PM)", menu = meal.dinner)

                    if (meal.specialDietsNote.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AxisTertiary.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WarningAmber, contentDescription = null, tint = AxisTertiary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dietary Alert: ${meal.specialDietsNote}", fontSize = 11.sp, color = AxisTertiary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MealRow(icon: String, mealTime: String, menu: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(mealTime, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(menu, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
