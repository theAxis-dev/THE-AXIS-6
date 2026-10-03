package com.example.data.repository

import com.example.data.dao.BmsDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class BmsRepository(private val dao: BmsDao) {
    val allStudents: Flow<List<Student>> = dao.getAllStudents()
    val studentCount: Flow<Int> = dao.getStudentCount()
    val allDormitories: Flow<List<Dormitory>> = dao.getAllDormitories()
    val allClasses: Flow<List<ClassStream>> = dao.getAllClasses()
    val allInspections: Flow<List<InspectionRecord>> = dao.getAllInspections()
    val allMaintenanceTickets: Flow<List<MaintenanceTicket>> = dao.getAllMaintenanceTickets()
    val allNotices: Flow<List<Notice>> = dao.getAllNotices()
    val allCleaners: Flow<List<CleanerAssignment>> = dao.getAllCleaners()
    val allDisciplinaryCases: Flow<List<DisciplinaryCase>> = dao.getAllDisciplinaryCases()
    val allDispensaryRecords: Flow<List<DispensaryRecord>> = dao.getAllDispensaryRecords()
    val allMealSchedules: Flow<List<MealSchedule>> = dao.getAllMealSchedules()

    suspend fun insertStudent(student: Student) = dao.insertStudent(student)
    suspend fun updateStudent(student: Student) = dao.updateStudent(student)
    suspend fun deleteStudent(student: Student) = dao.deleteStudent(student)

    suspend fun insertInspection(record: InspectionRecord) = dao.insertInspection(record)

    suspend fun insertMaintenanceTicket(ticket: MaintenanceTicket) = dao.insertMaintenanceTicket(ticket)
    suspend fun updateMaintenanceTicket(ticket: MaintenanceTicket) = dao.updateMaintenanceTicket(ticket)

    suspend fun insertNotice(notice: Notice) = dao.insertNotice(notice)
    suspend fun deleteNotice(notice: Notice) = dao.deleteNotice(notice)

    suspend fun insertCleanerAssignment(cleaner: CleanerAssignment) = dao.insertCleanerAssignment(cleaner)
    suspend fun updateCleanerAssignment(cleaner: CleanerAssignment) = dao.updateCleanerAssignment(cleaner)

    suspend fun insertDisciplinaryCase(case: DisciplinaryCase) = dao.insertDisciplinaryCase(case)
    suspend fun updateDisciplinaryCase(case: DisciplinaryCase) = dao.updateDisciplinaryCase(case)

    suspend fun insertDispensaryRecord(record: DispensaryRecord) = dao.insertDispensaryRecord(record)
}
