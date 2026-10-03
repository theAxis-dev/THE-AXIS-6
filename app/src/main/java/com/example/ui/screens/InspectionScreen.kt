package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.Dormitory
import com.example.data.model.InspectionRecord
import com.example.ui.theme.AxisDanger
import com.example.ui.theme.AxisPrimary
import com.example.ui.theme.AxisSuccess
import com.example.ui.theme.AxisTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionScreen(
    inspections: List<InspectionRecord>,
    dorms: List<Dormitory>,
    preselectedDorm: String? = null,
    onSaveInspection: (String, Int, Int, Int, Int, Int, String) -> Unit
) {
    var showNewInspectionDialog by remember { mutableStateOf(preselectedDorm != null) }
    var activeDormToInspect by remember { mutableStateOf(preselectedDorm ?: dorms.firstOrNull()?.name ?: "Mount Kenya") }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewInspectionDialog = true },
                icon = { Icon(Icons.Default.Check, contentDescription = null) },
                text = { Text("New Inspection", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("new_inspection_fab")
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Leaderboard Award Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AxisTertiary)
                            Text(
                                text = "Weekly Dormitory Trophy Leaderboard",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        val ranked = dorms.sortedByDescending { it.inspectionScore }
                        ranked.take(3).forEachIndexed { idx, dorm ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val (trophy, color) = when (idx) {
                                        0 -> Pair("🥇 1st", AxisTertiary)
                                        1 -> Pair("🥈 2nd", Color(0xFF94A3B8))
                                        else -> Pair("🥉 3rd", Color(0xFFB45309))
                                    }
                                    Text(trophy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(dorm.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                }
                                Text(
                                    "${dorm.inspectionScore}/100",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Historical inspection records
            item {
                Text(
                    text = "Recent Inspection Logs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            items(inspections) { record ->
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
                            Text(
                                text = record.dormName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            val scoreColor = when {
                                record.totalScore >= 85 -> AxisSuccess
                                record.totalScore >= 70 -> AxisTertiary
                                else -> AxisDanger
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = scoreColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${record.totalScore} / 100",
                                    color = scoreColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category breakdown chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ScoreItem("Beds", record.bedsScore)
                            ScoreItem("Lockers", record.lockersScore)
                            ScoreItem("Floors", record.floorScore)
                            ScoreItem("Compound", record.compoundScore)
                            ScoreItem("Ablution", record.ablutionScore)
                        }

                        if (record.remarks.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Remarks: ${record.remarks}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(Date(record.inspectionDate))
                        Text(
                            text = "Inspected by ${record.inspectorName} on $formattedDate",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }

    // New Inspection Rubric Modal
    if (showNewInspectionDialog) {
        NewInspectionDialog(
            dorms = dorms,
            initialDorm = activeDormToInspect,
            onDismiss = { showNewInspectionDialog = false },
            onConfirm = { dorm, beds, lockers, floors, compound, ablution, remarks ->
                onSaveInspection(dorm, beds, lockers, floors, compound, ablution, remarks)
                showNewInspectionDialog = false
            }
        )
    }
}

@Composable
private fun ScoreItem(label: String, score: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("$score/20", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun NewInspectionDialog(
    dorms: List<Dormitory>,
    initialDorm: String,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Int, Int, Int, Int, String) -> Unit
) {
    var selectedDorm by remember { mutableStateOf(initialDorm) }
    var bedsScore by remember { mutableIntStateOf(18) }
    var lockersScore by remember { mutableIntStateOf(18) }
    var floorScore by remember { mutableIntStateOf(18) }
    var compoundScore by remember { mutableIntStateOf(18) }
    var ablutionScore by remember { mutableIntStateOf(17) }
    var remarks by remember { mutableStateOf("") }

    val total = bedsScore + lockersScore + floorScore + compoundScore + ablutionScore

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dormitory Hygiene Inspection", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "$total / 100",
                    fontWeight = FontWeight.Bold,
                    color = AxisPrimary,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Select Hall to Inspect:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(dorms) { d ->
                        FilterChip(
                            selected = selectedDorm == d.name,
                            onClick = { selectedDorm = d.name },
                            label = { Text(d.name, fontSize = 11.sp) }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                RubricSlider("1. Beds & Linen (0-20)", bedsScore) { bedsScore = it }
                RubricSlider("2. Lockers & Shoe Racks (0-20)", lockersScore) { lockersScore = it }
                RubricSlider("3. Floors & Dusting (0-20)", floorScore) { floorScore = it }
                RubricSlider("4. Compound & Verandah (0-20)", compoundScore) { compoundScore = it }
                RubricSlider("5. Ablution Block & Toilets (0-20)", ablutionScore) { ablutionScore = it }

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Inspector Notes & Directives") },
                    placeholder = { Text("e.g. Excellent hospital corners. Dust bins emptied.") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        selectedDorm,
                        bedsScore,
                        lockersScore,
                        floorScore,
                        compoundScore,
                        ablutionScore,
                        remarks.ifBlank { "Routine weekly boarding inspection" }
                    )
                }
            ) {
                Text("Submit Score ($total/100)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun RubricSlider(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("$value / 20", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AxisPrimary)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..20f,
            steps = 19,
            modifier = Modifier.height(26.dp)
        )
    }
}
