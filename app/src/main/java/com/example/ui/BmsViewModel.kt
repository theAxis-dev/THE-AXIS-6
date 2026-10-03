package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthManager
import com.example.auth.LockoutState
import com.example.auth.UserSession
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.BmsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class BmsScreen(val title: String) {
    DASHBOARD("Dashboard"),
    STUDENTS("Students"),
    CLASSES("Classes"),
    DORMS("Dorms"),
    INSPECTION("Inspection"),
    CLEANERS("Cleaners"),
    MAINTENANCE("Maintenance"),
    DISPENSARY("Dispensary"),
    DISCIPLINARY("Disciplinary"),
    CATERING("Catering"),
    REPORTS("Reports"),
    SETTINGS("Settings")
}

class BmsViewModel(application: Application) : AndroidViewModel(application) {
    private val authManager = AuthManager(application)
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = BmsRepository(database.bmsDao())

    val currentUser: StateFlow<UserSession?> = authManager.currentUser
    val lockoutState: StateFlow<LockoutState> = authManager.lockout

    // Navigation State
    private val _currentScreen = MutableStateFlow(BmsScreen.DASHBOARD)
    val currentScreen: StateFlow<BmsScreen> = _currentScreen.asStateFlow()

    // Data streams from Room
    val students: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dormitories: StateFlow<List<Dormitory>> = repository.allDormitories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val classes: StateFlow<List<ClassStream>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inspections: StateFlow<List<InspectionRecord>> = repository.allInspections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val maintenanceTickets: StateFlow<List<MaintenanceTicket>> = repository.allMaintenanceTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notices: StateFlow<List<Notice>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cleaners: StateFlow<List<CleanerAssignment>> = repository.allCleaners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val disciplinaryCases: StateFlow<List<DisciplinaryCase>> = repository.allDisciplinaryCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dispensaryRecords: StateFlow<List<DispensaryRecord>> = repository.allDispensaryRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mealSchedules: StateFlow<List<MealSchedule>> = repository.allMealSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Search & Filter States
    val studentSearchQuery = MutableStateFlow("")
    val studentClassFilter = MutableStateFlow("All")
    val studentDormFilter = MutableStateFlow("All")

    val maintenanceFilter = MutableStateFlow("All") // "All", "Pending", "In Progress", "Fixed"

    // Toast / Feedback message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        // Ticking timer for lockout countdown
        viewModelScope.launch {
            while (isActive) {
                authManager.updateLockoutStatus()
                delay(1000)
            }
        }
    }

    fun navigateTo(screen: BmsScreen) {
        _currentScreen.value = screen
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // --- Auth actions ---
    fun login(user: String, pass: String, rememberMe: Boolean, onError: (String) -> Unit) {
        val result = authManager.login(user, pass, rememberMe)
        result.onSuccess {
            _currentScreen.value = BmsScreen.DASHBOARD
            showToast("Welcome back, ${it.displayName}")
        }.onFailure {
            onError(it.message ?: "Authentication failed")
        }
    }

    fun quickDemoLogin(asMaster: Boolean) {
        authManager.quickDemoLogin(asMaster)
        _currentScreen.value = BmsScreen.DASHBOARD
        showToast("Logged in as ${if (asMaster) "Master Admin" else "Boarding Master"}")
    }

    fun switchRole(role: String) {
        authManager.switchRole(role)
        showToast("Switched role to $role")
    }

    fun logout() {
        authManager.logout()
        _currentScreen.value = BmsScreen.DASHBOARD
    }

    fun resetLockout() {
        authManager.resetLockout()
        showToast("Lockout restrictions cleared")
    }

    // --- Entity Mutations ---
    fun addStudent(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertStudent(student)
            showToast("Student ${student.fullName} registered successfully")
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStudent(student)
            showToast("Student details updated")
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudent(student)
            showToast("Student record removed")
        }
    }

    fun saveInspection(
        dormName: String,
        beds: Int,
        lockers: Int,
        floors: Int,
        compound: Int,
        ablution: Int,
        remarks: String
    ) {
        val total = beds + lockers + floors + compound + ablution
        val inspector = currentUser.value?.displayName ?: "Boarding Master"
        val record = InspectionRecord(
            dormName = dormName,
            bedsScore = beds,
            lockersScore = lockers,
            floorScore = floors,
            compoundScore = compound,
            ablutionScore = ablution,
            totalScore = total,
            inspectorName = inspector,
            remarks = remarks
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertInspection(record)
            // Also update the dorm's inspectionScore
            val currentDorms = dormitories.value
            val target = currentDorms.find { it.name.equals(dormName, ignoreCase = true) }
            if (target != null) {
                val updated = target.copy(
                    inspectionScore = total,
                    statusBadge = if (total >= 80) "Compliant" else "Needs Cleaning"
                )
                database.bmsDao().updateDormitory(updated)
            }
            showToast("Inspection saved: $dormName scored $total/100")
        }
    }

    fun addMaintenanceTicket(ticket: MaintenanceTicket) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMaintenanceTicket(ticket)
            showToast("Maintenance ticket reported: ${ticket.title}")
        }
    }

    fun updateTicketStatus(ticket: MaintenanceTicket, nextStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = ticket.copy(status = nextStatus)
            repository.updateMaintenanceTicket(updated)
            showToast("Ticket #${ticket.id} marked as $nextStatus")
        }
    }

    fun addNotice(notice: Notice) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNotice(notice)
            showToast("Notice posted to board")
        }
    }

    fun deleteNotice(notice: Notice) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNotice(notice)
            showToast("Notice removed")
        }
    }

    fun addCleanerAssignment(cleaner: CleanerAssignment) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCleanerAssignment(cleaner)
            showToast("Duty assigned to ${cleaner.staffName}")
        }
    }

    fun updateCleanerStatus(cleaner: CleanerAssignment, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = cleaner.copy(status = newStatus)
            repository.updateCleanerAssignment(updated)
            showToast("Cleaner duty status updated to $newStatus")
        }
    }

    fun addDisciplinaryCase(case: DisciplinaryCase) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDisciplinaryCase(case)
            showToast("Disciplinary incident logged for ${case.studentName}")
        }
    }

    fun updateDisciplinaryStatus(case: DisciplinaryCase, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = case.copy(status = newStatus)
            repository.updateDisciplinaryCase(updated)
            showToast("Case status updated to $newStatus")
        }
    }

    fun addDispensaryRecord(record: DispensaryRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertDispensaryRecord(record)
            // If admitted, update student status to Dispensary
            val studentList = students.value
            val match = studentList.find { it.admNo == record.admNo }
            if (match != null && record.admittedToSickBay) {
                repository.updateStudent(match.copy(status = "Dispensary"))
            }
            showToast("Sick bay record logged for ${record.studentName}")
        }
    }

    fun resetDemoData() {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.populateInitialData(database.bmsDao())
            showToast("Demo data reloaded successfully")
        }
    }
}
