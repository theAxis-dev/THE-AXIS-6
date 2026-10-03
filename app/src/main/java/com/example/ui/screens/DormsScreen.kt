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
import com.example.data.model.Dormitory
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AxisPrimary
import com.example.ui.theme.AxisWarning

@Composable
fun DormsScreen(
    dorms: List<Dormitory>,
    onRunInspection: (String) -> Unit
) {
    var selectedDormForTool by remember { mutableStateOf<Dormitory?>(null) }

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
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bed,
                        contentDescription = null,
                        tint = AxisPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Dormitory & Bunk Management",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        val totalBeds = dorms.sumOf { it.capacity }
                        val occupied = dorms.sumOf { it.occupiedBeds }
                        Text(
                            text = "$occupied / $totalBeds Total Boarding Beds Occupied",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(dorms) { dorm ->
            Card(
                shape = RoundedCornerShape(16.dp),
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
                            Text(
                                text = dorm.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Wing: ${dorm.wing} • Housemaster: ${dorm.houseMaster}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        StatusBadge(status = dorm.statusBadge)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Occupancy Progress
                    val progress = if (dorm.capacity > 0) dorm.occupiedBeds.toFloat() / dorm.capacity else 0f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Occupancy: ${dorm.occupiedBeds} / ${dorm.capacity} Beds",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (progress >= 0.95f) AxisWarning else AxisPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (progress >= 0.95f) AxisWarning else AxisPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prefect: ${dorm.prefectName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Hygiene Rating: ${dorm.inspectionScore}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { selectedDormForTool = dorm },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bunk Tool", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onRunInspection(dorm.name) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Inspect Dorm", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Bunk Tool Modal
    selectedDormForTool?.let { dorm ->
        AlertDialog(
            onDismissRequest = { selectedDormForTool = null },
            title = {
                Text("${dorm.name} — Bunk Allocation & Inventory", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Bunk Bed Structure: Two tiers (Lower deck & Upper deck) per cubicle. Total rooms: 15 cubicles.",
                        fontSize = 13.sp
                    )

                    HorizontalDivider()

                    Text("Cubicle Allocation Sample:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• Cubicle 1 (Beds 1-4): 2 Low, 2 Up [Full - Allan Komen, Dennis Kiprono]", fontSize = 12.sp)
                    Text("• Cubicle 2 (Beds 5-8): 2 Low, 2 Up [1 Bed Vacant - B-06 Up]", fontSize = 12.sp)
                    Text("• Cubicle 3 (Beds 9-12): 2 Low, 2 Up [Full - Form 4 STEM Prefects]", fontSize = 12.sp)

                    HorizontalDivider()

                    Text("Equipment Checklist:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("✓ Fire Extinguisher inspected: Pressure normal", fontSize = 12.sp, color = AxisPrimary)
                    Text("✓ Emergency Exit clear of luggage", fontSize = 12.sp, color = AxisPrimary)
                    Text("✓ Lockers numbered 1 through ${dorm.capacity}", fontSize = 12.sp, color = AxisPrimary)
                }
            },
            confirmButton = {
                Button(onClick = { selectedDormForTool = null }) {
                    Text("Close")
                }
            }
        )
    }
}
