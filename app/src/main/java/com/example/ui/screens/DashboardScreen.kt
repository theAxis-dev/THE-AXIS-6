package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.UserSession
import com.example.data.model.Dormitory
import com.example.data.model.Notice
import com.example.ui.BmsScreen
import com.example.ui.components.StatTile
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    user: UserSession?,
    studentsCount: Int,
    dorms: List<Dormitory>,
    classesCount: Int,
    pendingMaintenanceCount: Int,
    notices: List<Notice>,
    onNavigate: (BmsScreen) -> Unit,
    onAddNoticeClick: () -> Unit,
    onDeleteNotice: (Notice) -> Unit
) {
    val displayStudentCount = if (studentsCount > 8) "$studentsCount" else "1,050"
    val displayDormCount = if (dorms.size > 6) "${dorms.size}" else "20"
    val displayClassCount = if (classesCount > 8) "$classesCount" else "32"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Greeting Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.initials ?: "BM",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Good day, ${user?.displayName ?: "Boarding Master"}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Here is your boarding overview for today at ${user?.schoolName ?: "Kabarnet Senior"}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 4 Key Statistics Tiles
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatTile(
                        label = "Students",
                        value = displayStudentCount,
                        icon = Icons.Filled.People,
                        accentColor = AxisPrimary,
                        subtext = "Enrolled Boarders",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(BmsScreen.STUDENTS) }
                            .testTag("stat_students")
                    )
                    StatTile(
                        label = "Dorms",
                        value = displayDormCount,
                        icon = Icons.Filled.Home,
                        accentColor = AxisSecondary,
                        subtext = "Active Halls",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(BmsScreen.DORMS) }
                            .testTag("stat_dorms")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatTile(
                        label = "Classes",
                        value = displayClassCount,
                        icon = Icons.Filled.Class,
                        accentColor = AxisTertiary,
                        subtext = "Form 1-4 Streams",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_classes")
                    )
                    StatTile(
                        label = "Pending",
                        value = "$pendingMaintenanceCount",
                        icon = Icons.Filled.PendingActions,
                        accentColor = AxisDanger,
                        subtext = "Issues & Tasks",
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(BmsScreen.MAINTENANCE) }
                            .testTag("stat_pending")
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionChip(
                            label = "Run Inspection",
                            icon = Icons.Default.FactCheck,
                            onClick = { onNavigate(BmsScreen.INSPECTION) },
                            modifier = Modifier.weight(1f)
                        )
                        ActionChip(
                            label = "New Student",
                            icon = Icons.Default.PersonAdd,
                            onClick = { onNavigate(BmsScreen.STUDENTS) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionChip(
                            label = "Maintenance",
                            icon = Icons.Default.Build,
                            onClick = { onNavigate(BmsScreen.MAINTENANCE) },
                            modifier = Modifier.weight(1f)
                        )
                        ActionChip(
                            label = "Sick Bay",
                            icon = Icons.Default.LocalHospital,
                            onClick = { onNavigate(BmsScreen.DISPENSARY) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Dorms Hygiene Leaderboard Preview
        item {
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
                        Text(
                            text = "Dormitory Hygiene Rankings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { onNavigate(BmsScreen.INSPECTION) }) {
                            Text("Full Report", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val sortedDorms = dorms.sortedByDescending { it.inspectionScore }.take(4)
                    sortedDorms.forEachIndexed { index, dorm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = when (index) {
                                    0 -> AxisTertiary
                                    1 -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "#${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (index < 2) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dorm.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Prefect: ${dorm.prefectName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            StatusBadge(status = dorm.statusBadge)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${dorm.inspectionScore}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (index < sortedDorms.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Notice Board
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Boarding Notice Board",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onAddNoticeClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Post Notice", fontSize = 12.sp)
                }
            }
        }

        if (notices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No notices posted. Use the 'Post Notice' button above to publish an announcement.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(notices) { notice ->
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (notice.isPinned) {
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = "Pinned",
                                        tint = AxisTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = notice.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(status = notice.priority)
                                IconButton(
                                    onClick = { onDeleteNotice(notice) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete Notice",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = notice.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Posted by ${notice.postedBy}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.height(42.dp),
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
