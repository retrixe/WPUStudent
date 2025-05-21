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
