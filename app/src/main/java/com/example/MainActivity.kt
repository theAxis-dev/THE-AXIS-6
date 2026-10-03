package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Notice
import com.example.ui.BmsScreen
import com.example.ui.BmsViewModel
import com.example.ui.components.BmsBottomNavigation
import com.example.ui.components.BmsTopBar
import com.example.ui.screens.*
import com.example.ui.theme.AxisDanger
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BmsAppRoot()
            }
        }
    }
}

@Composable
fun BmsAppRoot(viewModel: BmsViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val lockoutState by viewModel.lockoutState.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    val students by viewModel.students.collectAsStateWithLifecycle()
    val dorms by viewModel.dormitories.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val inspections by viewModel.inspections.collectAsStateWithLifecycle()
    val tickets by viewModel.maintenanceTickets.collectAsStateWithLifecycle()
    val notices by viewModel.notices.collectAsStateWithLifecycle()
    val cleaners by viewModel.cleaners.collectAsStateWithLifecycle()
    val cases by viewModel.disciplinaryCases.collectAsStateWithLifecycle()
    val dispensary by viewModel.dispensaryRecords.collectAsStateWithLifecycle()
    val meals by viewModel.mealSchedules.collectAsStateWithLifecycle()

    val studentSearch by viewModel.studentSearchQuery.collectAsStateWithLifecycle()
    val studentClassFilter by viewModel.studentClassFilter.collectAsStateWithLifecycle()
    val maintenanceFilter by viewModel.maintenanceFilter.collectAsStateWithLifecycle()

    val toastMsg by viewModel.toastMessage.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showPostNoticeDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var preselectedDormForInspection by remember { mutableStateOf<String?>(null) }

    // Display feedback toast
    LaunchedEffect(toastMsg) {
        toastMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    if (currentUser == null) {
        LoginScreen(
            lockoutState = lockoutState,
            onLoginSubmit = { u, p, r, onError ->
                viewModel.login(u, p, r, onError)
            },
            onQuickDemoLogin = { asMaster ->
                viewModel.quickDemoLogin(asMaster)
            },
            onResetLockout = {
                viewModel.resetLockout()
            }
        )
    } else {
        // Back handler to navigate back to dashboard when on sub-screens
        BackHandler(enabled = currentScreen != BmsScreen.DASHBOARD) {
            viewModel.navigateTo(BmsScreen.DASHBOARD)
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp)
                ) {
                    // Drawer Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(20.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentUser?.initials ?: "A6",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentUser?.displayName ?: "User",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${currentUser?.role} • ${currentUser?.schoolName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    HorizontalDivider()

                    // Drawer Items
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 8.dp)
                    ) {
                        DrawerNavEntry("Dashboard", Icons.Default.Dashboard, currentScreen == BmsScreen.DASHBOARD) {
                            viewModel.navigateTo(BmsScreen.DASHBOARD)
                            scope.launch { drawerState.close() }
                        }
                        DrawerNavEntry("Students", Icons.Default.People, currentScreen == BmsScreen.STUDENTS) {
                            viewModel.navigateTo(BmsScreen.STUDENTS)
                            scope.launch { drawerState.close() }
                        }
                        DrawerNavEntry("Classes", Icons.Default.Class, currentScreen == BmsScreen.CLASSES) {
                            viewModel.navigateTo(BmsScreen.CLASSES)
                            scope.launch { drawerState.close() }
                        }
                        DrawerNavEntry("Dorms & Bunk Tool", Icons.Default.Home, currentScreen == BmsScreen.DORMS) {
                            viewModel.navigateTo(BmsScreen.DORMS)
                            scope.launch { drawerState.close() }
                        }
                        DrawerNavEntry("Cleaners Roster", Icons.Default.CleaningServices, currentScreen == BmsScreen.CLEANERS) {
                            viewModel.navigateTo(BmsScreen.CLEANERS)
                            scope.launch { drawerState.close() }
                        }
                        DrawerNavEntry("Inspection", Icons.Default.FactCheck, currentScreen == BmsScreen.INSPECTION) {
                            preselectedDormForInspection = null
                            viewModel.navigateTo(BmsScreen.INSPECTION)
                            scope.launch { drawerState.close() }
                        }
                        DrawerNavEntry("Reports & Analytics", Icons.Default.Assessment, currentScreen == BmsScreen.REPORTS) {
                            viewModel.navigateTo(BmsScreen.REPORTS)
                            scope.launch { drawerState.close() }
                        }
                        DrawerNavEntry("Catering & Meals", Icons.Default.Restaurant, currentScreen == BmsScreen.CATERING) {
                            viewModel.navigateTo(BmsScreen.CATERING)
                            scope.launch { drawerState.close() }
                        }
                        DrawerNavEntry("Dispensary (Sick Bay)", Icons.Default.LocalHospital, currentScreen == BmsScreen.DISPENSARY) {
                            viewModel.navigateTo(BmsScreen.DISPENSARY)
                            scope.launch { drawerState.close() }
                        }
                        DrawerNavEntry("Maintenance Requests", Icons.Default.Build, currentScreen == BmsScreen.MAINTENANCE) {
                            viewModel.navigateTo(BmsScreen.MAINTENANCE)
                            scope.launch { drawerState.close() }
                        }
                        DrawerNavEntry("Disciplinary Cases", Icons.Default.Gavel, currentScreen == BmsScreen.DISCIPLINARY) {
                            viewModel.navigateTo(BmsScreen.DISCIPLINARY)
                            scope.launch { drawerState.close() }
                        }
                        DrawerNavEntry("Settings", Icons.Default.Settings, currentScreen == BmsScreen.SETTINGS) {
                            viewModel.navigateTo(BmsScreen.SETTINGS)
                            scope.launch { drawerState.close() }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        NavigationDrawerItem(
                            label = { Text("Sign out", color = AxisDanger, fontWeight = FontWeight.Bold) },
                            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = AxisDanger) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.logout()
                            }
                        )
                    }
                }
            }
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    BmsTopBar(
                        currentScreen = currentScreen,
                        user = currentUser,
                        pendingAlertsCount = tickets.count { it.status == "Pending" },
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onNotificationsClick = { showNotificationsDialog = true },
                        onLogoutClick = { viewModel.logout() },
                        onAvatarClick = { viewModel.navigateTo(BmsScreen.SETTINGS) }
                    )
                },
                bottomBar = {
                    BmsBottomNavigation(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) },
                        onOpenMoreMenu = { scope.launch { drawerState.open() } }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        BmsScreen.DASHBOARD -> DashboardScreen(
                            user = currentUser,
                            studentsCount = students.size,
                            dorms = dorms,
                            classesCount = classes.size,
                            pendingMaintenanceCount = tickets.count { it.status == "Pending" },
                            notices = notices,
                            onNavigate = { viewModel.navigateTo(it) },
                            onAddNoticeClick = { showPostNoticeDialog = true },
                            onDeleteNotice = { viewModel.deleteNotice(it) }
                        )
                        BmsScreen.STUDENTS -> StudentsScreen(
                            students = students,
                            dorms = dorms,
                            classes = classes,
                            searchQuery = studentSearch,
                            onSearchChange = { viewModel.studentSearchQuery.value = it },
                            selectedClassFilter = studentClassFilter,
                            onClassFilterChange = { viewModel.studentClassFilter.value = it },
                            onAddStudent = { viewModel.addStudent(it) },
                            onUpdateStudent = { viewModel.updateStudent(it) },
                            onDeleteStudent = { viewModel.deleteStudent(it) }
                        )
                        BmsScreen.CLASSES -> ClassesScreen(classes = classes)
                        BmsScreen.DORMS -> DormsScreen(
                            dorms = dorms,
                            onRunInspection = { dormName ->
                                preselectedDormForInspection = dormName
                                viewModel.navigateTo(BmsScreen.INSPECTION)
                            }
                        )
                        BmsScreen.INSPECTION -> InspectionScreen(
                            inspections = inspections,
                            dorms = dorms,
                            preselectedDorm = preselectedDormForInspection,
                            onSaveInspection = { dorm, b, l, f, c, a, remarks ->
                                viewModel.saveInspection(dorm, b, l, f, c, a, remarks)
                                preselectedDormForInspection = null
                            }
                        )
                        BmsScreen.CLEANERS -> CleanersScreen(
                            cleaners = cleaners,
                            onAddCleaner = { viewModel.addCleanerAssignment(it) },
                            onUpdateStatus = { cleaner, next -> viewModel.updateCleanerStatus(cleaner, next) }
                        )
                        BmsScreen.MAINTENANCE -> MaintenanceScreen(
                            tickets = tickets,
                            activeFilter = maintenanceFilter,
                            onFilterChange = { viewModel.maintenanceFilter.value = it },
                            onAddTicket = { viewModel.addMaintenanceTicket(it) },
                            onUpdateStatus = { ticket, next -> viewModel.updateTicketStatus(ticket, next) }
                        )
                        BmsScreen.DISPENSARY -> DispensaryScreen(
                            records = dispensary,
                            onAddRecord = { viewModel.addDispensaryRecord(it) }
                        )
                        BmsScreen.DISCIPLINARY -> DisciplinaryScreen(
                            cases = cases,
                            onAddCase = { viewModel.addDisciplinaryCase(it) },
                            onUpdateStatus = { c, next -> viewModel.updateDisciplinaryStatus(c, next) }
                        )
                        BmsScreen.CATERING -> CateringScreen(meals = meals)
                        BmsScreen.REPORTS -> ReportsScreen(
                            dorms = dorms,
                            studentCount = students.size,
                            maintenanceCount = tickets.size,
                            onExportReport = {
                                viewModel.showToast("Report generated & downloaded to device storage")
                            }
                        )
                        BmsScreen.SETTINGS -> SettingsScreen(
                            user = currentUser,
                            onSwitchRole = { viewModel.switchRole(it) },
                            onResetDemoData = { viewModel.resetDemoData() }
                        )
                    }
                }
            }
        }
    }

    // Post Notice Dialog
    if (showPostNoticeDialog) {
        var noticeTitle by remember { mutableStateOf("") }
        var noticeMsg by remember { mutableStateOf("") }
        var isPinned by remember { mutableStateOf(false) }
        var priority by remember { mutableStateOf("General") }

        AlertDialog(
            onDismissRequest = { showPostNoticeDialog = false },
            title = { Text("Publish Notice to Board", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = noticeTitle,
                        onValueChange = { noticeTitle = it },
                        label = { Text("Notice Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = noticeMsg,
                        onValueChange = { noticeMsg = it },
                        label = { Text("Announcement Details") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isPinned, onCheckedChange = { isPinned = it })
                            Text("Pin to top", fontSize = 13.sp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("General", "Urgent", "Event").forEach { p ->
                                FilterChip(
                                    selected = priority == p,
                                    onClick = { priority = p },
                                    label = { Text(p, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noticeTitle.isNotBlank()) {
                            viewModel.addNotice(
                                Notice(
                                    title = noticeTitle.trim(),
                                    message = noticeMsg.trim(),
                                    priority = priority,
                                    isPinned = isPinned,
                                    postedBy = currentUser?.displayName ?: "Boarding Master"
                                )
                            )
                            showPostNoticeDialog = false
                        }
                    },
                    enabled = noticeTitle.isNotBlank()
                ) {
                    Text("Post")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPostNoticeDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        val pendingTickets = tickets.filter { it.status == "Pending" }
        val admittedPatients = dispensary.filter { it.admittedToSickBay }
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Active Boarding Alerts", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Urgent Maintenance (${pendingTickets.size}):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    pendingTickets.take(3).forEach {
                        Text("• [${it.category}] ${it.title} at ${it.location}", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Sick Bay Inpatients (${admittedPatients.size}):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    admittedPatients.take(2).forEach {
                        Text("• ${it.studentName} (${it.classStream}) - ${it.symptoms}", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showNotificationsDialog = false }) {
                    Text("Understood")
                }
            }
        )
    }
}

@Composable
private fun DrawerNavEntry(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp) },
        icon = { Icon(icon, contentDescription = null, tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
    )
}
