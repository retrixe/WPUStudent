package xyz.retrixe.wpustudent.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* FIXME
curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/attendance/api/attendance/summary' \
  -H 'authorization: Bearer BEARER TOKEN' \
  -H 'content-type: application/json' \
  -H 'x-applicationname: connectportal' \
  -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
  -H 'x-requestfrom: web' \
  --data-raw '{"StartDate":null,"EndDate":"2025-05-07","ModeName":"term","StudentUniqueId":"c9bef136-396a-441e-9370-876fda382b20","SelectedModuleId":null,"SelectedTermId":null}'

curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/student-attendance/attendancedropdown' \
  -H 'authorization: Bearer BEARER' \
  -H 'content-type: application/json' \
  -H 'x-applicationname: connectportal' \
  -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
  -H 'x-requestfrom: web' \
  --data-raw '{"StudentUniqueID":"c9bef136-396a-441e-9370-876fda382b20"}'

curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/student-attendance/studentattendancesummary' \
  -H 'authorization: Bearer BEARER TOKEN' \
  -H 'content-type: application/json' \
  -H 'x-applicationname: connectportal' \
  -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
  -H 'x-requestfrom: web' \
  --data-raw '{"StudentUniqueID":"c9bef136-396a-441e-9370-876fda382b20","CourseFamilyId":35,"TermCodeId":4,"CourseList":[{"ID":1773,"Name":"Probability and Statistics"}],"StartDate":"2025-05-01","EndDate":"2025-05-07","TermStartDate":"2025-01-01","TermEndDate":"2025-07-06"}'
*/

/* {
    "StatusCode": 200,
    "Message": "student found",
    "Item": null,
    "Items": [
        {
            "StudentId": "c9bef136-396a-441e-9370-876fda382b20",
            "FirstName": "Ibrahim",
            "MiddleName": "",
            "LastName": "Ansari",
            "AdmissionDate": null,
            "GlobalId": "1032233145",
            "Date_Of_Birth": "2004-12-02T00:00:00",
            "EmailId": "ibrahim.ansari@mitwpu.edu.in",
            "MobileNo": null,
            "ApplicationNo": null,
            "StreamId": null,
            "StreamCode": "",
            "StreamName": "",
            "TermCodeID": 0,
            "TermCode": "SEM_4",
            "TermName": "Semester 4",
            "CenterId": 0,
            "CenterCode": "KOTHRUD",
            "CenterName": "Kothrud",
            "CourseId": 0,
            "CourseFamilyAcademicYearId": 0,
            "CourseFamilyYearCode": "BTECH_CSE",
            "UserId": 0,
            "CourseCodeId": 0,
            "CourseCode": "SY",
            "CourseCodeName": "Second Year",
            "AdmissionBatchId": null,
            "AdmissionBatchCode": "WPU_2023",
            "AdmissionBatchName": "AB 2023-24",
            "AcademicYearId": 0,
            "AcademicYearCode": "AY2024",
            "AcademicYearName": "2024-25",
            "BrandId": null,
            "BrandCode": "MITWPU",
            "BrandName": "Dr. Vishwanath Karad MIT World Peace University",
            "BrandLogoUrl": "https://mymitwpu.integratededucation.pwc.in/oneportal/assets/site-images/mitwpu/logo.png?v=1.2",
            "BrandTypeCode": "University",
            "CenterLocationId": 0,
            "CampusCode": "PUNE",
            "CampusName": "Pune",
            "DepartmentId": 0,
            "DepartmentCode": "DOCET",
            "DepartmentName": "Department of Computer Engineering and Technology",
            "LevelId": null,
            "ProgramTypeCode": "BTECH",
            "ProgramTypeName": "Bachelor of Technology",
            "FacultyId": null,
            "FacultyCode": "SOCSE",
            "FacultyName": "School of Computer Science and Engineering",
            "ProfilePictureInfo": {
                "FilePath": "MIT-WPU Student Photos/03052024/School of Computer Science and Engineering/1032233145_1032233145/SPH_1032233145.jpg",
                "ContainerName": null
            },
            "GenderCode": "Male",
            "GenderName": "Male",
            "CourseFamilyName": "B.Tech Computer Science and Engineering",
            "CourseFamilyCode": "BTECH_CSE",
            "MajorName": "NA",
            "MajorCode": "NA",
            "CourseFamilyId": 35,
            "DemotionStatus": null
        }
    ],
    "AdditionalData": null
} */
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
data class StudentBasicInfoResponse(@SerialName("Items") val items: List<StudentBasicInfo>)

suspend fun retrieveStudentBasicInfo(
    client: HttpClient,
    bearerToken: String? = null // This function typically expects such a token where it is used
): StudentBasicInfo {
    /*  curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/connect-portal/api/studentloginbasicinfo' \
          -H 'authorization: Bearer BEARER' \
          -H 'x-applicationname: connectportal' \
          -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
          -H 'x-requestfrom: web'
    */
    val response = client.get("apigateway/connect-portal/api/studentloginbasicinfo") {
        header("x-applicationname", "connectportal")
        header("x-appsecret", CLIENT_SECRET)
        header("x-requestfrom", "web")
        if (bearerToken != null) {
            header("authorization", "Bearer $bearerToken")
        }
    }
    val body: StudentBasicInfoResponse = response.body()
    return body.items.first()
}
