package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.UserSession
import com.example.ui.BmsScreen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmsTopBar(
    currentScreen: BmsScreen,
    user: UserSession?,
    pendingAlertsCount: Int = 4,
    onMenuClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "A6",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Column {
                    Text(
                        text = currentScreen.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = user?.schoolName ?: "Kabarnet Senior School",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.testTag("topbar_menu_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Navigation Menu"
                )
            }
        },
        actions = {
            // Notifications badge
            Box {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("topbar_notification_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications"
                    )
                }
                if (pendingAlertsCount > 0) {
                    Badge(
                        containerColor = AxisDanger,
                        contentColor = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp)
                    ) {
                        Text(text = "$pendingAlertsCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // User Avatar & Role Chip
            if (user != null) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable { onAvatarClick() }
                        .testTag("user_avatar_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.initials,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = user.role,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Sign out
            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier.testTag("topbar_logout_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sign out",
                    tint = AxisDanger
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun BmsBottomNavigation(
    currentScreen: BmsScreen,
    onNavigate: (BmsScreen) -> Unit,
    onOpenMoreMenu: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val primaryTabs = listOf(
            Triple(BmsScreen.DASHBOARD, Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
            Triple(BmsScreen.STUDENTS, Icons.Filled.People, Icons.Outlined.People),
            Triple(BmsScreen.DORMS, Icons.Filled.Home, Icons.Outlined.Home),
            Triple(BmsScreen.INSPECTION, Icons.Filled.FactCheck, Icons.Outlined.FactCheck)
        )

        primaryTabs.forEach { (screen, selectedIcon, unselectedIcon) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) selectedIcon else unselectedIcon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
            )
        }

        // More Tab
        val isMoreActive = currentScreen !in listOf(
            BmsScreen.DASHBOARD, BmsScreen.STUDENTS, BmsScreen.DORMS, BmsScreen.INSPECTION
        )
        NavigationBarItem(
            selected = isMoreActive,
            onClick = onOpenMoreMenu,
            icon = {
                Icon(
                    imageVector = if (isMoreActive) Icons.Filled.Apps else Icons.Outlined.Apps,
                    contentDescription = "More Modules"
                )
            },
            label = { Text("More", fontSize = 11.sp, fontWeight = if (isMoreActive) FontWeight.Bold else FontWeight.Normal) },
            modifier = Modifier.testTag("nav_item_more")
        )
    }
}

@Composable
fun StatTile(
    label: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    subtext: String? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtext != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "active", "compliant", "completed", "fixed" -> Pair(Color(0xFFDCFCE7), AxisSuccess)
        "dispensary", "needs cleaning", "urgent", "high", "in progress", "ongoing" -> Pair(Color(0xFFFEF3C7), AxisWarning)
        "on leave", "suspended", "inspection due", "pending" -> Pair(Color(0xFFFEE2E2), AxisDanger)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
