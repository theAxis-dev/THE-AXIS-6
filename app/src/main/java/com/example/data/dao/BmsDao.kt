package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BmsDao {
    // --- Students ---
    @Query("SELECT * FROM students ORDER BY fullName ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE admNo = :admNo LIMIT 1")
    suspend fun getStudentByAdmNo(admNo: String): Student?

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    // --- Dormitories ---
    @Query("SELECT * FROM dormitories ORDER BY name ASC")
    fun getAllDormitories(): Flow<List<Dormitory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDormitories(dorms: List<Dormitory>)

    @Update
    suspend fun updateDormitory(dormitory: Dormitory)

    // --- Classes ---
    @Query("SELECT * FROM class_streams ORDER BY formLevel ASC, name ASC")
    fun getAllClasses(): Flow<List<ClassStream>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<ClassStream>)

    // --- Inspections ---
    @Query("SELECT * FROM inspection_records ORDER BY inspectionDate DESC")
    fun getAllInspections(): Flow<List<InspectionRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(record: InspectionRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspections(records: List<InspectionRecord>)

    // --- Maintenance Tickets ---
    @Query("SELECT * FROM maintenance_tickets ORDER BY reportedDate DESC")
    fun getAllMaintenanceTickets(): Flow<List<MaintenanceTicket>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceTicket(ticket: MaintenanceTicket): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceTickets(tickets: List<MaintenanceTicket>)

    @Update
    suspend fun updateMaintenanceTicket(ticket: MaintenanceTicket)

    // --- Notices ---
    @Query("SELECT * FROM notices ORDER BY isPinned DESC, datePosted DESC")
    fun getAllNotices(): Flow<List<Notice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: Notice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotices(notices: List<Notice>)

    @Delete
    suspend fun deleteNotice(notice: Notice)

    // --- Cleaners ---
    @Query("SELECT * FROM cleaner_assignments ORDER BY zone ASC")
    fun getAllCleaners(): Flow<List<CleanerAssignment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCleanerAssignment(assignment: CleanerAssignment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCleaners(assignments: List<CleanerAssignment>)

    @Update
    suspend fun updateCleanerAssignment(assignment: CleanerAssignment)

    // --- Disciplinary ---
    @Query("SELECT * FROM disciplinary_cases ORDER BY reportedDate DESC")
    fun getAllDisciplinaryCases(): Flow<List<DisciplinaryCase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisciplinaryCase(case: DisciplinaryCase): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisciplinaryCases(cases: List<DisciplinaryCase>)

    @Update
    suspend fun updateDisciplinaryCase(case: DisciplinaryCase)

    // --- Dispensary ---
    @Query("SELECT * FROM dispensary_records ORDER BY visitDate DESC")
    fun getAllDispensaryRecords(): Flow<List<DispensaryRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispensaryRecord(record: DispensaryRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispensaryRecords(records: List<DispensaryRecord>)

    // --- Meal Schedules ---
    @Query("SELECT * FROM meal_schedules ORDER BY id ASC")
    fun getAllMealSchedules(): Flow<List<MealSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealSchedules(schedules: List<MealSchedule>)
}
