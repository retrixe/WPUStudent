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
data class Holiday(
    val name: String,
    val subType: String,
    val startDate: String,
    val endDate: String = startDate,
) : Parcelable

// TODO: Everything below needs updating
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
