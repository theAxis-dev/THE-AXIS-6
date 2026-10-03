package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.MaintenanceTicket
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AxisDanger
import com.example.ui.theme.AxisPrimary
import com.example.ui.theme.AxisSuccess
import com.example.ui.theme.AxisWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceScreen(
    tickets: List<MaintenanceTicket>,
    activeFilter: String,
    onFilterChange: (String) -> Unit,
    onAddTicket: (MaintenanceTicket) -> Unit,
    onUpdateStatus: (MaintenanceTicket, String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val filtered = remember(tickets, activeFilter) {
        if (activeFilter == "All") tickets else tickets.filter { it.status == activeFilter }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Build, contentDescription = null) },
                text = { Text("Report Issue", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Status Tabs
            val tabs = listOf("All", "Pending", "In Progress", "Fixed")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs) { tab ->
                    FilterChip(
                        selected = activeFilter == tab,
                        onClick = { onFilterChange(tab) },
                        label = { Text(tab, fontSize = 12.sp) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { ticket ->
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
                                    text = ticket.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                StatusBadge(status = ticket.status)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Location: ${ticket.location} • Category: ${ticket.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (ticket.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = ticket.notes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Reported by ${ticket.reportedBy}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    fontSize = 11.sp
                                )

                                // Status cycler button
                                val nextStatus = when (ticket.status) {
                                    "Pending" -> "In Progress"
                                    "In Progress" -> "Fixed"
                                    else -> "Pending"
                                }
                                TextButton(
                                    onClick = { onUpdateStatus(ticket, nextStatus) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Mark as $nextStatus", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTicketDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = {
                onAddTicket(it)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddTicketDialog(
    onDismiss: () -> Unit,
    onConfirm: (MaintenanceTicket) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Plumbing") }
    var priority by remember { mutableStateOf("Medium") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Maintenance Request", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Issue Title (e.g. Leaking pipe, broken lock)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Exact Location (e.g. Kilimanjaro Room 2)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("Plumbing", "Electrical", "Carpentry", "Masonry")) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
                Text("Priority:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(listOf("Low", "Medium", "High")) { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p, fontSize = 11.sp) }
                        )
                    }
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Detailed Description / Material Needed") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && location.isNotBlank()) {
                        onConfirm(
                            MaintenanceTicket(
                                title = title.trim(),
                                location = location.trim(),
                                category = category,
                                priority = priority,
                                notes = notes,
                                reportedBy = "Boarding Office"
                            )
                        )
                    }
                },
                enabled = title.isNotBlank() && location.isNotBlank()
            ) {
                Text("Submit Ticket")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
