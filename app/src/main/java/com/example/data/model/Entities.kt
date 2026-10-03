package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val admNo: String,
    val fullName: String,
    val classStream: String, // e.g. "Form 3 East"
    val dormName: String,    // e.g. "Mount Kenya"
    val bedNumber: String,   // e.g. "B-14 Top"
    val guardianName: String,
    val guardianPhone: String,
    val medicalNotes: String = "None",
    val status: String = "Active", // "Active", "Dispensary", "On Leave", "Suspended"
    val specialDiet: String = "Standard",
    val lastRollCall: Long = System.currentTimeMillis()
)

@Entity(tableName = "dormitories")
data class Dormitory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val wing: String, // "Boys" or "Girls"
    val capacity: Int,
    val occupiedBeds: Int,
    val prefectName: String,
    val houseMaster: String,
    val inspectionScore: Int = 85, // 0-100
    val statusBadge: String = "Compliant" // "Compliant", "Needs Cleaning", "Inspection Due"
)

@Entity(tableName = "class_streams")
data class ClassStream(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // "Form 1 North", etc.
    val formLevel: Int, // 1, 2, 3, 4
    val studentCount: Int,
    val classTeacher: String,
    val roomNumber: String
)

@Entity(tableName = "inspection_records")
data class InspectionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dormName: String,
    val inspectionDate: Long = System.currentTimeMillis(),
    val bedsScore: Int,      // /20
    val lockersScore: Int,   // /20
    val floorScore: Int,     // /20
    val compoundScore: Int,  // /20
    val ablutionScore: Int,  // /20
    val totalScore: Int,     // /100
    val inspectorName: String,
    val remarks: String
)

@Entity(tableName = "maintenance_tickets")
data class MaintenanceTicket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val location: String,
    val category: String, // "Plumbing", "Electrical", "Carpentry", "Masonry"
    val priority: String, // "High", "Medium", "Low"
    val status: String = "Pending", // "Pending", "In Progress", "Fixed"
    val reportedBy: String,
    val reportedDate: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "notices")
data class Notice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val priority: String = "General", // "Urgent", "General", "Event"
    val postedBy: String = "Boarding Master",
    val datePosted: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Entity(tableName = "cleaner_assignments")
data class CleanerAssignment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val staffName: String,
    val zone: String, // "Dining Hall", "Ablution Blocks", "Dorm Quadrangle", "Classroom Wing"
    val shift: String, // "Morning", "Afternoon", "Full Day"
    val status: String = "Ongoing", // "Ongoing", "Completed", "Pending Review"
    val supervisor: String = "Housekeeper"
)

@Entity(tableName = "disciplinary_cases")
data class DisciplinaryCase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentName: String,
    val admNo: String,
    val offense: String,
    val sanction: String, // e.g. "Compound leveling 3 hrs", "Parent conference"
    val reportedDate: Long = System.currentTimeMillis(),
    val status: String = "Active", // "Active", "Completed", "Appealed"
    val hearingOfficer: String = "Discipline Master"
)

@Entity(tableName = "dispensary_records")
data class DispensaryRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentName: String,
    val admNo: String,
    val classStream: String,
    val symptoms: String,
    val treatmentGiven: String,
    val admittedToSickBay: Boolean = false,
    val visitDate: Long = System.currentTimeMillis(),
    val attendingNurse: String = "Nurse Jane"
)

@Entity(tableName = "meal_schedules")
data class MealSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: String, // "Monday", etc.
    val breakfast: String,
    val lunch: String,
    val dinner: String,
    val specialDietsNote: String
)
