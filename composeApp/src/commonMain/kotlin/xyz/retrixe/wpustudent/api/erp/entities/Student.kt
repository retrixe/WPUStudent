package xyz.retrixe.wpustudent.api.erp.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.retrixe.wpustudent.kmp.Parcelable
import xyz.retrixe.wpustudent.kmp.Parcelize

@Serializable
data class StudentBasicInfo(
    val prn: String,
    val name: String,
    val term: String,
    val section: String,
    val profilePicture: String,
)

const val THRESHOLD_PERCENTAGE = 75.0 // +5'ed everywhere it's used, thus 80%

@Parcelize
@Serializable
data class CourseAttendanceSummary(
    val id: String,
    val subjectName: String,
    val subjectType: String,
    val present: Int,
    val total: Int,
    val percentage: Double,
) : Parcelable

fun readableSubjectType(type: String) = when (type) {
    "PR" -> "Practical"
    "PJ" -> "Project"
    "TH" -> "Theory"
    "TT" -> "Tutorial"
    else -> type
}

@Parcelize
@Serializable
data class CourseAttendanceDetail(
    @SerialName("ATTENDANCE_DATE") val attendanceDate: String, // DD/MM/YYYY
    @SerialName("PERIOD_SEQUENCE_NO") val periodSequenceNo: String, // An integer, which period
    @SerialName("STUDENT_STATUS") val studentStatus: String, // PRESENT or ABSENT
    @SerialName("PERIOD_TYPE") val periodType: String, // REGULAR (no idea what it means)
    @SerialName("SUBJECT_DESCRIPTION") val subjectDescription: String, // Subject Name
    @SerialName("TYPE_DESCRIPTION") val typeDescription: String // Theory/Project/Tutorial/Practical
) : Parcelable

@Parcelize
@Serializable
data class Event(
    val name: String,
    val subType: String,
    val startDate: String,
    val endDate: String = startDate,
) : Parcelable

@Parcelize
@Serializable
data class TimetablePeriod(
    @SerialName("PERIOD_FROM_TIME") val periodFromTime: String, // "10:45",
    @SerialName("PERIOD_UPTO_TIME") val periodUptoTime: String, // "12:45",
    @SerialName("PERIOD_FROM_TIME1") val periodFromTimeAmPmMarker: String, // "AM",
    @SerialName("PERIOD_UPTO_TIME1") val periodUptoTimeAmPmMarker: String, // "PM",
    @SerialName("SUBJECT_DETAIL_ID") val subjectDetailId: String, // "93248",
    @SerialName("SUBJECT_DESCRIPTION") val subjectDescription: String, // "Full Stack Development Laboratory",
    @SerialName("TYPE_SHORT_NAME") val typeShortName: String, // "PR",
    @SerialName("PERIOD_TYPE") val periodType: String, // "",
    @SerialName("EMP_NAME") val empName: String, // "P.N.N.",
    @SerialName("BATCH_SHORT_NAME") val batchShortName: String, // "B1",
    @SerialName("BUILDING_NAME") val buildingName: String, // "",
    @SerialName("ROOM_NUMBER") val roomNumber: String, // "VY-228",
) : Parcelable

@Parcelize
@Serializable
data class TimetableDay(
    @SerialName("WEEK_DAY_ID") val weekDayId: String,
    @SerialName("DAY_NAME") val dayName: String,
    @SerialName("WEEK_DATE") val weekDate: String,
    @SerialName("stud_tt") val periods: List<TimetablePeriod>
) : Parcelable

@Parcelize
@Serializable
data class ExamHallTicket(
    val sessionName: String,
    val ticket: List<List<Exam>>,
) : Parcelable

@Parcelize
@Serializable
data class Exam(
    val examTypeCode: String,
    val examDate: String,
    val courseName: String,
    val courseCode: String,
    val time: String,
    val eligibility: String,
) : Parcelable
