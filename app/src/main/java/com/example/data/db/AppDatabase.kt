package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.BmsDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Student::class,
        Dormitory::class,
        ClassStream::class,
        InspectionRecord::class,
        MaintenanceTicket::class,
        Notice::class,
        CleanerAssignment::class,
        DisciplinaryCase::class,
        DispensaryRecord::class,
        MealSchedule::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bmsDao(): BmsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "the_axis_6_bms_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.bmsDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: BmsDao) {
            // Initial Dormitories
            val dorms = listOf(
                Dormitory(name = "Mount Kenya", wing = "Boys", capacity = 160, occupiedBeds = 152, prefectName = "Brian Kipchumba", houseMaster = "Mr. Kiprop", inspectionScore = 92, statusBadge = "Compliant"),
                Dormitory(name = "Kilimanjaro", wing = "Boys", capacity = 180, occupiedBeds = 176, prefectName = "David Cheruiyot", houseMaster = "Mr. Kabutiei", inspectionScore = 88, statusBadge = "Compliant"),
                Dormitory(name = "Mount Elgon", wing = "Boys", capacity = 170, occupiedBeds = 165, prefectName = "Emmanuel Rutto", houseMaster = "Mr. Kiplagat", inspectionScore = 74, statusBadge = "Needs Cleaning"),
                Dormitory(name = "Aberdare", wing = "Boys", capacity = 180, occupiedBeds = 178, prefectName = "Collins Tanui", houseMaster = "Mr. Chebii", inspectionScore = 95, statusBadge = "Compliant"),
                Dormitory(name = "Ruwenzori", wing = "Boys", capacity = 180, occupiedBeds = 174, prefectName = "Kevin Korir", houseMaster = "Mr. Kemboi", inspectionScore = 82, statusBadge = "Compliant"),
                Dormitory(name = "Masai Mara", wing = "Boys", capacity = 180, occupiedBeds = 175, prefectName = "Samson Bett", houseMaster = "Mr. Toroitich", inspectionScore = 80, statusBadge = "Inspection Due")
            )
            dao.insertDormitories(dorms)

            // Initial Classes
            val classes = listOf(
                ClassStream(name = "Form 1 North", formLevel = 1, studentCount = 45, classTeacher = "Mrs. Chemutai", roomNumber = "R101"),
                ClassStream(name = "Form 1 South", formLevel = 1, studentCount = 44, classTeacher = "Mr. Koech", roomNumber = "R102"),
                ClassStream(name = "Form 1 East", formLevel = 1, studentCount = 46, classTeacher = "Mr. Sang", roomNumber = "R103"),
                ClassStream(name = "Form 2 North", formLevel = 2, studentCount = 43, classTeacher = "Mrs. Bii", roomNumber = "R201"),
                ClassStream(name = "Form 2 East", formLevel = 2, studentCount = 45, classTeacher = "Mr. Kiptoo", roomNumber = "R202"),
                ClassStream(name = "Form 3 Science A", formLevel = 3, studentCount = 42, classTeacher = "Mr. Langat", roomNumber = "Lab 1"),
                ClassStream(name = "Form 3 Science B", formLevel = 3, studentCount = 41, classTeacher = "Mrs. Jepkorir", roomNumber = "Lab 2"),
                ClassStream(name = "Form 4 STEM", formLevel = 4, studentCount = 40, classTeacher = "Mr. Kabutiei", roomNumber = "R401")
            )
            dao.insertClasses(classes)

            // Sample Students
            val students = listOf(
                Student(admNo = "KB-4201", fullName = "Brian Kipchumba", classStream = "Form 4 STEM", dormName = "Mount Kenya", bedNumber = "A-01 Low", guardianName = "Paul Kipchumba", guardianPhone = "+254 712 345 678", medicalNotes = "Asthma inhaler in matron office", status = "Active"),
                Student(admNo = "KB-4202", fullName = "Allan Komen", classStream = "Form 3 Science A", dormName = "Kilimanjaro", bedNumber = "B-04 Up", guardianName = "Mercy Komen", guardianPhone = "+254 722 888 111", status = "Active"),
                Student(admNo = "KB-4203", fullName = "Dennis Kiprono", classStream = "Form 2 East", dormName = "Mount Elgon", bedNumber = "C-12 Low", guardianName = "James Kiprono", guardianPhone = "+254 733 999 222", status = "Dispensary", medicalNotes = "Mild malaria treatment"),
                Student(admNo = "KB-4204", fullName = "Felix Kiprotich", classStream = "Form 1 North", dormName = "Aberdare", bedNumber = "D-02 Up", guardianName = "Grace Kiprotich", guardianPhone = "+254 700 123 456", status = "Active"),
                Student(admNo = "KB-4205", fullName = "Geoffrey Rutto", classStream = "Form 4 STEM", dormName = "Ruwenzori", bedNumber = "A-15 Low", guardianName = "David Rutto", guardianPhone = "+254 711 555 444", status = "Active"),
                Student(admNo = "KB-4206", fullName = "Hillary Cheruiyot", classStream = "Form 3 Science B", dormName = "Masai Mara", bedNumber = "B-08 Low", guardianName = "Rose Cheruiyot", guardianPhone = "+254 720 777 666", status = "On Leave", medicalNotes = "Dental appointment permission granted"),
                Student(admNo = "KB-4207", fullName = "Isaac Kiplagat", classStream = "Form 2 North", dormName = "Mount Kenya", bedNumber = "A-09 Up", guardianName = "Simon Kiplagat", guardianPhone = "+254 789 222 333", status = "Active"),
                Student(admNo = "KB-4208", fullName = "Kelvin Toroitich", classStream = "Form 1 East", dormName = "Aberdare", bedNumber = "D-14 Low", guardianName = "Esther Toroitich", guardianPhone = "+254 714 666 999", status = "Active")
            )
            dao.insertStudents(students)

            // Initial Notices
            val notices = listOf(
                Notice(title = "Weekly General Dorm Inspection", message = "All dormitories must be ready for Sunday Morning Inspection by 09:30 AM. Housemasters and prefects to ensure full adherence.", priority = "Urgent", isPinned = true),
                Notice(title = "Dining Hall Schedule Adjustment", message = "Supper will be served at 6:45 PM on Thursday due to intra-school athletics briefing in the main hall.", priority = "General", isPinned = false),
                Notice(title = "Dispensary Medical Check-up", message = "All Form 1 students to report to the sanatorium for routine bi-term vitals and health screening.", priority = "Event", isPinned = false)
            )
            dao.insertNotices(notices)

            // Initial Maintenance Tickets
            val tickets = listOf(
                Notice(title = "Elgon Dorm Tap Leakage", message = "Ablution block tap 4 leaking continuously.", priority = "High"),
                Notice(title = "Kilimanjaro Room 3 Light Bulb", message = "Overhead tube light flickering.", priority = "Medium")
            )
            val initialTickets = listOf(
                MaintenanceTicket(title = "Elgon Ablution Tap Leak", location = "Mount Elgon Block B", category = "Plumbing", priority = "High", status = "Pending", reportedBy = "Emmanuel Rutto (Prefect)", notes = "Requires replacement washer and pipe sealant."),
                MaintenanceTicket(title = "Kilimanjaro Overhead Lighting", location = "Kilimanjaro Room 3", category = "Electrical", priority = "Medium", status = "In Progress", reportedBy = "David Cheruiyot", notes = "Electrician dispatched with 40W tubes."),
                MaintenanceTicket(title = "Aberdare Window Latch Broken", location = "Aberdare Wing 2", category = "Carpentry", priority = "Low", status = "Fixed", reportedBy = "Collins Tanui", notes = "Replaced with brass barrel bolt latch."),
                MaintenanceTicket(title = "Dining Hall Boiler Valve", location = "Main Kitchen", category = "Plumbing", priority = "High", status = "Pending", reportedBy = "Chef Kiprotich", notes = "Pressure valve requires urgent inspection.")
            )
            dao.insertMaintenanceTickets(initialTickets)

            // Initial Inspections
            val inspections = listOf(
                InspectionRecord(dormName = "Aberdare", bedsScore = 20, lockersScore = 19, floorScore = 19, compoundScore = 18, ablutionScore = 19, totalScore = 95, inspectorName = "John Mwangi (Boarding Master)", remarks = "Impeccable order. Bed linen taut and lockers uniformly arranged."),
                InspectionRecord(dormName = "Mount Kenya", bedsScore = 19, lockersScore = 18, floorScore = 18, compoundScore = 19, ablutionScore = 18, totalScore = 92, inspectorName = "John Mwangi", remarks = "Clean verandah. Dustbins emptied early."),
                InspectionRecord(dormName = "Kilimanjaro", bedsScore = 18, lockersScore = 17, floorScore = 18, compoundScore = 18, ablutionScore = 17, totalScore = 88, inspectorName = "Mr. Kabutiei", remarks = "Satisfactory. Two lockers had unironed shirts."),
                InspectionRecord(dormName = "Mount Elgon", bedsScore = 15, lockersScore = 15, floorScore = 14, compoundScore = 16, ablutionScore = 14, totalScore = 74, inspectorName = "John Mwangi", remarks = "Ablution block needs scrubbing. Floor polish uneven.")
            )
            dao.insertInspections(inspections)

            // Cleaners
            val cleaners = listOf(
                CleanerAssignment(staffName = "Samuel Kemboi", zone = "Dorm Quadrangle & Pathways", shift = "Morning", status = "Completed"),
                CleanerAssignment(staffName = "Sarah Chebet", zone = "Dining Hall & Kitchen Annex", shift = "Full Day", status = "Ongoing"),
                CleanerAssignment(staffName = "Joseph Kipng'etich", zone = "Ablution Blocks 1 to 4", shift = "Morning", status = "Completed"),
                CleanerAssignment(staffName = "Lilian Jepkosgei", zone = "Sanatorium & Admin Corridors", shift = "Afternoon", status = "Ongoing")
            )
            dao.insertCleaners(cleaners)

            // Disciplinary cases
            val cases = listOf(
                DisciplinaryCase(studentName = "Allan Komen", admNo = "KB-4202", offense = "Late return from field games past 6:15 PM bell", sanction = "Compound sweeping during prep break (2 days)", status = "Active"),
                DisciplinaryCase(studentName = "Hillary Cheruiyot", admNo = "KB-4206", offense = "Improper school uniform footwear in dining hall", sanction = "Verbal warning and confiscated non-regulation shoes", status = "Completed")
            )
            dao.insertDisciplinaryCases(cases)

            // Dispensary records
            val sickBay = listOf(
                DispensaryRecord(studentName = "Dennis Kiprono", admNo = "KB-4203", classStream = "Form 2 East", symptoms = "High fever (38.5C), chills, headache", treatmentGiven = "Paracetamol 500mg, Artemether tabs, bed rest", admittedToSickBay = true, attendingNurse = "Nurse Jane"),
                DispensaryRecord(studentName = "Felix Kiprotich", admNo = "KB-4204", classStream = "Form 1 North", symptoms = "Minor ankle sprain during football practice", treatmentGiven = "Elastic crepe bandage, Ice compression, Ibuprofen", admittedToSickBay = false, attendingNurse = "Nurse Jane")
            )
            dao.insertDispensaryRecords(sickBay)

            // Meal schedules
            val meals = listOf(
                MealSchedule(dayOfWeek = "Monday", breakfast = "Millet Porridge, Boiled Eggs, Tea & Bread", lunch = "Githeri (Maize & Beans) with Cabbage", dinner = "Beef Stew, Ugali & Sukuma Wiki", specialDietsNote = "3 students diabetic (no sugar tea)"),
                MealSchedule(dayOfWeek = "Tuesday", breakfast = "Tea, Fresh Mandazi, Ripe Bananas", lunch = "Rice with Yellow Lentils & Greens", dinner = "Chicken Curry, Chapati & Steamed Spinach", specialDietsNote = "2 vegetarian students"),
                MealSchedule(dayOfWeek = "Wednesday", breakfast = "Oatmeal Porridge, Peanut Butter Toast & Milk", lunch = "Bean Stew with Ugali & Traditional Greens", dinner = "Pilau Beef with Kachumbari", specialDietsNote = "Standard"),
                MealSchedule(dayOfWeek = "Thursday", breakfast = "Tea, Sweet Potatoes & Boiled Eggs", lunch = "Maize & Pigeon Peas with Spinach", dinner = "Minced Meat Sauce with Pasta / Rice", specialDietsNote = "1 gluten-free student"),
                MealSchedule(dayOfWeek = "Friday", breakfast = "Tea, Bread & Sausage", lunch = "Ndengu (Green grams) with Rice", dinner = "Fried Fish, Ugali & Kales", specialDietsNote = "1 fish allergy (substitute beef)")
            )
            dao.insertMealSchedules(meals)
        }
    }
}
