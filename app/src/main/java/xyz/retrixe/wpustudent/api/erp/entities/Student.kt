package xyz.retrixe.wpustudent.api.erp.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
