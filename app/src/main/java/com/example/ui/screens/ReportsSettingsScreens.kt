package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.auth.UserSession
import com.example.data.model.Dormitory
import com.example.ui.components.StatTile
import com.example.ui.theme.AxisDanger
import com.example.ui.theme.AxisPrimary
import com.example.ui.theme.AxisSecondary
import com.example.ui.theme.AxisTertiary

@Composable
fun ReportsScreen(
    dorms: List<Dormitory>,
    studentCount: Int,
    maintenanceCount: Int,
    onExportReport: () -> Unit
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
                    Icon(Icons.Default.Assessment, contentDescription = null, tint = AxisPrimary, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Executive Boarding Analytics", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Term II Composite Boarding Performance & Compliance Summary", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(
                    label = "Occupancy",
                    value = "96.8%",
                    icon = Icons.Default.PieChart,
                    accentColor = AxisSecondary,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "Hygiene",
                    value = "88.2%",
                    icon = Icons.Default.CheckCircle,
                    accentColor = AxisTertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dormitory Hygiene Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    dorms.forEach { dorm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(dorm.name, fontSize = 13.sp)
                            Text("${dorm.inspectionScore}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onExportReport,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Boarding Summary Report", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SettingsScreen(
    user: UserSession?,
    onSwitchRole: (String) -> Unit,
    onResetDemoData: () -> Unit
) {
    var schoolName by remember { mutableStateOf(user?.schoolName ?: "Kabarnet Senior School") }
    var motto by remember { mutableStateOf(user?.schoolMotto ?: "Knowledge is Power") }
    var showResetConfirm by remember { mutableStateOf(false) }

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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("School Branding & Institutional Info", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = schoolName,
                        onValueChange = { schoolName = it },
                        label = { Text("School Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = motto,
                        onValueChange = { motto = it },
                        label = { Text("School Motto") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Active Staff Profile & Permissions", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Current User: ${user?.displayName} (${user?.username})", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Switch Role (Demo Preview):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Boarding Master", "Master Admin", "Housemaster").forEach { role ->
                            FilterChip(
                                selected = user?.role == role,
                                onClick = { onSwitchRole(role) },
                                label = { Text(role, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Security & Portal Parameters", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Lockout Policy: 3 failed attempts triggers 15-minute freeze", fontSize = 12.sp)
                    Text("• Idle Timeout: 60 minutes automatic session expiration", fontSize = 12.sp)
                    Text("• Local Persistence: Encrypted Room SQLite Database", fontSize = 12.sp)
                }
            }
        }

        item {
            OutlinedButton(
                onClick = { showResetConfirm = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AxisDanger),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(46.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restore Demo Data (Seed Database)")
            }
        }

        item {
            Text(
                text = "The Axis 6 — Boarding Management System (BMS)\nVersion 2.4.0 (Build 2026.10)\nEngineered with Kotlin & Jetpack Compose",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Boarding Database?") },
            text = { Text("This will reload the initial Kabarnet Senior School roster, dorms, inspections, notices, and tickets.") },
            confirmButton = {
                Button(onClick = {
                    onResetDemoData()
                    showResetConfirm = false
                }) {
                    Text("Reset Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
