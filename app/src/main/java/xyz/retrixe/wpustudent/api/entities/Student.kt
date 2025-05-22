package xyz.retrixe.wpustudent.api.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentBasicInfo(
    @SerialName("StudentId") val studentId: String,
    @SerialName("FirstName") val firstName: String,
    @SerialName("MiddleName") val middleName: String,
    @SerialName("LastName") val lastName: String,
    @SerialName("GlobalId") val globalId: String,
    @SerialName("TermName") val termName: String,
    @SerialName("CourseFamilyName") val courseFamilyName: String,
    @SerialName("ProfilePictureInfo") val profilePictureInfo: ProfilePictureInfo,
)

@Serializable
data class ProfilePictureInfo(@SerialName("FilePath") val filePath: String)

@Serializable
data class CourseAttendanceSummary(
    @SerialName("TotalSessions") val totalSessions: Double,
    @SerialName("PresentCount") val presentCount: Double,
    @SerialName("AbsentCount") val absentCount: Int,
    @SerialName("CondonedAttendanceCount") val condonedAttendanceCount: Double,
    @SerialName("StudentCount") val studentCount: Double,
    @SerialName("AttendancePercentage") val attendancePercentage: Double,
    @SerialName("ModuleId") val moduleId: Int,
    @SerialName("ModuleName") val moduleName: String,
    @SerialName("ThresholdPercentage") val thresholdPercentage: Double,
    @SerialName("SelectedStartDate") val selectedStartDate: String,
    @SerialName("SelectedEndDate") val selectedEndDate: String,
)

@Serializable
data class AttendedTerm(
    @SerialName("IsCurrentTerm") val isCurrentTerm: Boolean,
    @SerialName("TermCodeId") val termCodeId: Int,
    @SerialName("TermCode") val termCode: String,
    @SerialName("TermStartDate") val termStartDate: String,
    @SerialName("TermEndDate") val termEndDate: String,
    @SerialName("ModuleDropdownDetailsList") val attendedCourses: List<AttendedCourse>,
)

@Serializable
data class AttendedCourse(
    @SerialName("ModuleId") val moduleId: Int,
    @SerialName("ModuleName") val moduleName: String,
)
