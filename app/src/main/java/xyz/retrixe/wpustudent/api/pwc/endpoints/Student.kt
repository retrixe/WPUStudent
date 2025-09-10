package xyz.retrixe.wpustudent.api.pwc.endpoints

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.retrixe.wpustudent.api.pwc.CLIENT_SECRET
import xyz.retrixe.wpustudent.api.pwc.entities.AttendedTerm
import xyz.retrixe.wpustudent.api.pwc.entities.CourseAttendanceSummary
import xyz.retrixe.wpustudent.api.pwc.entities.ExamHallTicket
import xyz.retrixe.wpustudent.api.pwc.entities.Holiday
import xyz.retrixe.wpustudent.api.pwc.entities.StudentBasicInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
private data class StudentBasicInfoResponse(@SerialName("Items") val items: List<StudentBasicInfo>)

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

@Serializable
private data class TermAttendanceSummaryRequest(
    @SerialName("StartDate") val startDate: String? = null,
    @SerialName("EndDate") val endDate: String,
    @SerialName("ModeName") val modeName: String = "term",
    @SerialName("StudentUniqueId") val studentUniqueId: String,
    @SerialName("SelectedModuleId") val selectedModuleId: String? = null,
    @SerialName("SelectedTermId") val selectedTermId: String? = null,
)

/* [
  {
    "TotalSessions": 63.00,
    "PresentCount": 59.00,
    "AbsentCount": 4,
    "CondonedAttendanceCount": 0.00,
    "StudentCount": 1.00,
    "AttendancePercentage": 93.65,
    "ModuleId": 1773,
    "ModuleName": "Probability and Statistics",
    "ThresholdPercentage": 75.00,
    "SelectedStartDate": "2025-01-01",
    "SelectedEndDate": "2025-05-07"
  },
  {
    "TotalSessions": 52.00,
    "PresentCount": 50.00,
    "AbsentCount": 2,
    "CondonedAttendanceCount": 0.00,
    "StudentCount": 1.00,
    "AttendancePercentage": 96.15,
    "ModuleId": 4327,
    "ModuleName": "Computer Networks",
    "ThresholdPercentage": 75.00,
    "SelectedStartDate": "2025-01-01",
    "SelectedEndDate": "2025-05-07"
  },
  {
    "TotalSessions": 52.00,
    "PresentCount": 49.00,
    "AbsentCount": 3,
    "CondonedAttendanceCount": 0.00,
    "StudentCount": 1.00,
    "AttendancePercentage": 94.23,
    "ModuleId": 4328,
    "ModuleName": "Database Management System",
    "ThresholdPercentage": 75.00,
    "SelectedStartDate": "2025-01-01",
    "SelectedEndDate": "2025-05-07"
  },
  {
    "TotalSessions": 18.00,
    "PresentCount": 18.00,
    "AbsentCount": 0,
    "CondonedAttendanceCount": 0.00,
    "StudentCount": 1.00,
    "AttendancePercentage": 100.00,
    "ModuleId": 4329,
    "ModuleName": "Database Management System Laboratory",
    "ThresholdPercentage": 75.00,
    "SelectedStartDate": "2025-01-01",
    "SelectedEndDate": "2025-05-07"
  },
  {
    "TotalSessions": 48.00,
    "PresentCount": 45.00,
    "AbsentCount": 3,
    "CondonedAttendanceCount": 0.00,
    "StudentCount": 1.00,
    "AttendancePercentage": 93.75,
    "ModuleId": 4330,
    "ModuleName": "Design and Analysis of Algorithms",
    "ThresholdPercentage": 75.00,
    "SelectedStartDate": "2025-01-01",
    "SelectedEndDate": "2025-05-07"
  },
  {
    "TotalSessions": 21.00,
    "PresentCount": 21.00,
    "AbsentCount": 0,
    "CondonedAttendanceCount": 0.00,
    "StudentCount": 1.00,
    "AttendancePercentage": 100.00,
    "ModuleId": 4331,
    "ModuleName": "Project Based Learning - II",
    "ThresholdPercentage": 75.00,
    "SelectedStartDate": "2025-01-01",
    "SelectedEndDate": "2025-05-07"
  },
  {
    "TotalSessions": 17.00,
    "PresentCount": 17.00,
    "AbsentCount": 0,
    "CondonedAttendanceCount": 0.00,
    "StudentCount": 1.00,
    "AttendancePercentage": 100.00,
    "ModuleId": 4377,
    "ModuleName": "Computer Networks Laboratory",
    "ThresholdPercentage": 75.00,
    "SelectedStartDate": "2025-01-01",
    "SelectedEndDate": "2025-05-07"
  }
] */

suspend fun getTermAttendanceSummary(
    client: HttpClient,
    studentUniqueId: String,
): List<CourseAttendanceSummary> {
    /*  curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/attendance/api/attendance/summary' \
          -H 'authorization: Bearer TOKEN' \
          -H 'content-type: application/json' \
          -H 'x-applicationname: connectportal' \
          -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
          -H 'x-requestfrom: web' \
          --data-raw '{"StartDate":null,"EndDate":"2025-05-07","ModeName":"term","StudentUniqueId":"c9bef136-396a-441e-9370-876fda382b20","SelectedModuleId":null,"SelectedTermId":null}'
    */
    val response = client.post("apigateway/attendance/api/attendance/summary") {
        contentType(ContentType.Application.Json)
        header("x-applicationname", "connectportal")
        header("x-appsecret", CLIENT_SECRET)
        header("x-requestfrom", "web")
        val endDate = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now())
        setBody(TermAttendanceSummaryRequest(endDate = endDate, studentUniqueId = studentUniqueId))
    }
    val body: List<CourseAttendanceSummary> = response.body()
    return body
}

/* [
  {
    "CourseFamilyId": 35,
    "CourseFamilyName": "B.Tech Computer Science and Engineering",
    "TermDropdownDetailsList": [
      {
        "IsCurrentTerm": false,
        "TermCodeId": 3,
        "TermCode": "Semester 3",
        "TermStartDate": "2024-07-01T00:00:00",
        "TermEndDate": "2024-12-31T00:00:00",
        "ModuleDropdownDetailsList": [
          {
            "ModuleId": 1297,
            "ModuleName": "Spiritual and Cultural Heritage: Indian Experience"
          },
          {
            "ModuleId": 1764,
            "ModuleName": "Differential Equations and Transform Techniques"
          },
          {
            "ModuleId": 1909,
            "ModuleName": "Research Innovation Design Entrepreneurship"
          },
          {
            "ModuleId": 2070,
            "ModuleName": "Object Oriented Programming using C++"
          },
          {
            "ModuleId": 2071,
            "ModuleName": "Data Structure"
          },
          {
            "ModuleId": 2072,
            "ModuleName": "Data Structure Laboratory"
          },
          {
            "ModuleId": 2073,
            "ModuleName": "Project Based Learning - I"
          },
          {
            "ModuleId": 2074,
            "ModuleName": "Microprocessor, Microcontroller and Applications"
          },
          {
            "ModuleId": 2900,
            "ModuleName": "Organizational Leadership and Change"
          }
        ]
      },
      {
        "IsCurrentTerm": true,
        "TermCodeId": 4,
        "TermCode": "Semester 4",
        "TermStartDate": "2025-01-01T00:00:00",
        "TermEndDate": "2025-07-06T00:00:00",
        "ModuleDropdownDetailsList": [
          {
            "ModuleId": 1289,
            "ModuleName": "Indian Constitution"
          },
          {
            "ModuleId": 1773,
            "ModuleName": "Probability and Statistics"
          },
          {
            "ModuleId": 1908,
            "ModuleName": "Rural Immersion"
          },
          {
            "ModuleId": 2931,
            "ModuleName": "Business Intelligence and Data Visualisation with Tableau"
          },
          {
            "ModuleId": 4327,
            "ModuleName": "Computer Networks"
          },
          {
            "ModuleId": 4328,
            "ModuleName": "Database Management System"
          },
          {
            "ModuleId": 4329,
            "ModuleName": "Database Management System Laboratory"
          },
          {
            "ModuleId": 4330,
            "ModuleName": "Design and Analysis of Algorithms"
          },
          {
            "ModuleId": 4331,
            "ModuleName": "Project Based Learning - II"
          },
          {
            "ModuleId": 4377,
            "ModuleName": "Computer Networks Laboratory"
          }
        ]
      }
    ]
  }
] */
@Serializable
private data class AttendedCoursesResponse(
    @SerialName("TermDropdownDetailsList") val attendedTerms: List<AttendedTerm>
)

suspend fun getAttendedCourses(
    client: HttpClient,
    studentUniqueId: String,
): List<AttendedTerm> {
    /*  curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/student-attendance/attendancedropdown' \
          -H 'authorization: Bearer BEARER' \
          -H 'content-type: application/json' \
          -H 'x-applicationname: connectportal' \
          -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
          -H 'x-requestfrom: web' \
          --data-raw '{"StudentUniqueID":"c9bef136-396a-441e-9370-876fda382b20"}'
    */
    val response = client.post("apigateway/student-attendance/attendancedropdown") {
        contentType(ContentType.Application.Json)
        header("x-applicationname", "connectportal")
        header("x-appsecret", CLIENT_SECRET)
        header("x-requestfrom", "web")
        setBody(mapOf("StudentUniqueID" to studentUniqueId))
    }
    val body: List<AttendedCoursesResponse> = response.body()
    return body.first().attendedTerms
}

/* FIXME: Detailed attendance info per subject
curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/student-attendance/studentattendancesummary' \
  -H 'authorization: Bearer TOKEN' \
  -H 'content-type: application/json' \
  -H 'x-applicationname: connectportal' \
  -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
  -H 'x-requestfrom: web' \
  --data-raw '{"StudentUniqueID":"c9bef136-396a-441e-9370-876fda382b20","CourseFamilyId":35,"TermCodeId":4,"CourseList":[{"ID":1773,"Name":"Probability and Statistics"}],"StartDate":"2025-05-01","EndDate":"2025-05-07","TermStartDate":"2025-01-01","TermEndDate":"2025-07-06"}'
{
  "AttendanceSummary": {
    "TotalAttendance": 3,
    "TotalSessionCount": 3,
    "TotalCondonedAttendanceCount": 0,
    "TotalPresentPercentage": 100.00,
    "TotalPresentPercentageTermWise": 93.65
  },
  "AttendanceInfo": [
    {
      "CourseId": 1773,
      "CourseName": "Probability and Statistics",
      "CohortCode": "",
      "AttendanceDetails": [
        {
          "AttendanceDate": "2025-05-05T16:30:00",
          "SessionId": 370116,
          "SessionDate": "2025-05-05T00:00:00",
          "SessionTime": "10:45 - 11:45",
          "FacultyNames": "Vaishali Sayankar",
          "StudentPunchInTime": "",
          "AttendanceID": 10913901,
          "AttendanceStatus": "PRESENT",
          "AttendanceSubTypeCode": "CRA",
          "AdditionalDetails": {
            "Mode": "Class Room",
            "ClassRoom": "KS-107",
            "VirtualRoom": "",
            "SessionStatus": "Class Taken",
            "Reason": []
          }
        },
        {
          "AttendanceDate": "2025-05-05T16:30:00",
          "SessionId": 370432,
          "SessionDate": "2025-05-05T00:00:00",
          "SessionTime": "09:30 - 10:30",
          "FacultyNames": "Vaishali Sayankar",
          "StudentPunchInTime": "",
          "AttendanceID": 10913840,
          "AttendanceStatus": "PRESENT",
          "AttendanceSubTypeCode": "CRA",
          "AdditionalDetails": {
            "Mode": "Class Room",
            "ClassRoom": "KS-107",
            "VirtualRoom": "",
            "SessionStatus": "Class Taken",
            "Reason": []
          }
        },
        {
          "AttendanceDate": "2025-05-02T16:30:00",
          "SessionId": 370219,
          "SessionDate": "2025-05-02T00:00:00",
          "SessionTime": "08:30 - 09:30",
          "FacultyNames": "Vaishali Sayankar",
          "StudentPunchInTime": "",
          "AttendanceID": 10641947,
          "AttendanceStatus": "PRESENT",
          "AttendanceSubTypeCode": "CRA",
          "AdditionalDetails": {
            "Mode": "Class Room",
            "ClassRoom": "KS-107",
            "VirtualRoom": "",
            "SessionStatus": "Class Taken",
            "Reason": []
          }
        }
      ]
    }
  ]
} */

/* [
  {
    "Name": "Republic Day",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "National Holiday",
    "StartDate": "2024-01-26T00:00:00",
    "EndDate": "2024-01-26T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Independence Day",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "National Holiday",
    "StartDate": "2024-08-15T00:00:00",
    "EndDate": "2024-08-15T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Rakshabandhan",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2024-08-19T00:00:00",
    "EndDate": "2024-08-19T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Krishan Janmashtami",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2024-08-26T00:00:00",
    "EndDate": "2024-08-26T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Ganesh Chaturthi",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2024-09-07T00:00:00",
    "EndDate": "2024-09-07T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Gauri Poojan",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2024-09-11T00:00:00",
    "EndDate": "2024-09-11T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Anant Chaturdashi",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2024-09-18T00:00:00",
    "EndDate": "2024-09-18T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Gandhi Jayanti",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "National Holiday",
    "StartDate": "2024-10-02T00:00:00",
    "EndDate": "2024-10-02T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Vijaya Dashami (Dasera)",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2024-10-12T00:00:00",
    "EndDate": "2024-10-12T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Diwali Vacation",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2024-10-28T00:00:00",
    "EndDate": "2024-11-04T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Christmas",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2024-12-25T00:00:00",
    "EndDate": "2024-12-25T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Shivjayanti",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2025-02-19T00:00:00",
    "EndDate": "2025-02-19T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Mahashivratri",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2025-02-26T00:00:00",
    "EndDate": "2025-02-26T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Dhulivandan",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2025-03-14T00:00:00",
    "EndDate": "2025-03-14T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Gudi Padva",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2025-03-30T00:00:00",
    "EndDate": "2025-03-30T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Ramzan Eid",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2025-03-31T00:00:00",
    "EndDate": "2025-03-31T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Dr. Babasaheb Ambedkar Jayanti",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2025-04-14T00:00:00",
    "EndDate": "2025-04-14T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Maharashtra Day",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2025-05-01T00:00:00",
    "EndDate": "2025-05-01T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  },
  {
    "Name": "Bakari Id",
    "BatchName": "",
    "Type": "MANHOL",
    "SubType": "State Holiday",
    "StartDate": "2025-06-07T00:00:00",
    "EndDate": "2025-06-07T00:00:00",
    "GradeId": 0,
    "BoardId": 0,
    "FacultyId": 2,
    "CourseId": 0,
    "BatchId": 0
  }
] */
suspend fun getHolidays(client: HttpClient, studentUniqueId: String): List<Holiday> {
    /*  curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/api/calendareventlist' \
          -H 'authorization: Bearer TOKEN' \
          -H 'content-type: application/json' \
          -H 'x-applicationname: connectportal' \
          -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
          -H 'x-requestfrom: web' \
          --data-raw '{"StudentUniqueId":"c9bef136-396a-441e-9370-876fda382b20"}'
    */
    val response = client.post("apigateway/api/calendareventlist") {
        contentType(ContentType.Application.Json)
        header("x-applicationname", "connectportal")
        header("x-appsecret", CLIENT_SECRET)
        header("x-requestfrom", "web")
        setBody(mapOf("StudentUniqueID" to studentUniqueId))
    }
    val body: List<Holiday> = response.body()
    return body
}

@Serializable
private data class ExamHallTicketRequest(
    @SerialName("StudentUniqueId") val studentUniqueId: String,
    @SerialName("ActivityCode") val activityCode: String = "hallticket",
    @SerialName("TermCode") val termCode: String,
)

/* {
  "StatusCode": 200,
  "Message": "Success",
  "Item": {
    "GlobalId": "1032233145",
    "IntegrationData": {
      "code": 200,
      "status": "OK",
      "message": "",
      "body": {
        "globalId": "1032233145",
        "enrollmentNo": "1032233145",
        "sessionName": "May-June 2025",
        "ticket": [
          [
            {
              "srNo": 1,
              "brandName": "Dr. Vishwanath Karad MIT World Peace University",
              "academicYearName": "2024-25",
              "schoolName": "School of Computer Science and Engineering",
              "programName": "B.Tech Computer Science and Engineering",
              "programCode": "BTECH_CSE",
              "examTypeCode": "SUPPL",
              "assessmentGroupCode": "AG34",
              "examId": 38414,
              "examName": "External Backlog II",
              "termId": 10787,
              "termName": "Semester 1",
              "termCode": "SEM_1",
              "examDate": "2025-06-05",
              "courseName": "Linear Algebra and Differential Calculus",
              "courseCode": "MST1PF01A",
              "day": "Thursday",
              "date": "05-06-2025",
              "time": "04:00 pm - 05:30 pm",
              "room_seatno": "",
              "remarks": null,
              "eligibility": "Yes"
            },
            {
              "srNo": 2,
              "brandName": "Dr. Vishwanath Karad MIT World Peace University",
              "academicYearName": "2024-25",
              "schoolName": "School of Computer Science and Engineering",
              "programName": "B.Tech Computer Science and Engineering",
              "programCode": "BTECH_CSE",
              "examTypeCode": "SUPPL",
              "assessmentGroupCode": "AG34",
              "examId": 37755,
              "examName": "External Backlog II",
              "termId": 10458,
              "termName": "Semester 3",
              "termCode": "SEM_3",
              "examDate": "2025-05-19",
              "courseName": "Differential Equations and Transform Techniques",
              "courseCode": "MST2PF01A",
              "day": "Monday",
              "date": "19-05-2025",
              "time": "02:00 pm - 03:30 pm",
              "room_seatno": "",
              "remarks": null,
              "eligibility": "Yes"
            }
          ],
          [
            {
              "srNo": 3,
              "brandName": "Dr. Vishwanath Karad MIT World Peace University",
              "academicYearName": "2024-25",
              "schoolName": "School of Computer Science and Engineering",
              "programName": "B.Tech Computer Science and Engineering",
              "programCode": "BTECH_CSE",
              "examTypeCode": "REG",
              "assessmentGroupCode": "AG02",
              "examId": 37775,
              "examName": "End Term",
              "termId": 64843,
              "termName": "Semester 4",
              "termCode": "SEM_4",
              "examDate": "2025-05-26",
              "courseName": "Probability and Statistics",
              "courseCode": "MST2PF03A",
              "day": "Monday",
              "date": "26-05-2025",
              "time": "10:30 am - 12:00 pm",
              "room_seatno": "",
              "remarks": null,
              "eligibility": "Yes"
            },
            {
              "srNo": 4,
              "brandName": "Dr. Vishwanath Karad MIT World Peace University",
              "academicYearName": "2024-25",
              "schoolName": "School of Computer Science and Engineering",
              "programName": "B.Tech Computer Science and Engineering",
              "programCode": "BTECH_CSE",
              "examTypeCode": "REG",
              "assessmentGroupCode": "AG02",
              "examId": 37779,
              "examName": "End Term",
              "termId": 64843,
              "termName": "Semester 4",
              "termCode": "SEM_4",
              "examDate": "2025-05-30",
              "courseName": "Database Management System",
              "courseCode": "CSE2PM04A",
              "day": "Friday",
              "date": "30-05-2025",
              "time": "10:30 am - 12:00 pm",
              "room_seatno": "",
              "remarks": null,
              "eligibility": "Yes"
            },
            {
              "srNo": 5,
              "brandName": "Dr. Vishwanath Karad MIT World Peace University",
              "academicYearName": "2024-25",
              "schoolName": "School of Computer Science and Engineering",
              "programName": "B.Tech Computer Science and Engineering",
              "programCode": "BTECH_CSE",
              "examTypeCode": "REG",
              "assessmentGroupCode": "AG02",
              "examId": 37786,
              "examName": "End Term",
              "termId": 64843,
              "termName": "Semester 4",
              "termCode": "SEM_4",
              "examDate": "2025-06-02",
              "courseName": "Design and Analysis of Algorithms",
              "courseCode": "CSE2PM09A",
              "day": "Monday",
              "date": "02-06-2025",
              "time": "10:30 am - 12:00 pm",
              "room_seatno": "",
              "remarks": null,
              "eligibility": "Yes"
            },
            {
              "srNo": 6,
              "brandName": "Dr. Vishwanath Karad MIT World Peace University",
              "academicYearName": "2024-25",
              "schoolName": "School of Computer Science and Engineering",
              "programName": "B.Tech Computer Science and Engineering",
              "programCode": "BTECH_CSE",
              "examTypeCode": "REG",
              "assessmentGroupCode": "AG02",
              "examId": 37788,
              "examName": "End Term",
              "termId": 64843,
              "termName": "Semester 4",
              "termCode": "SEM_4",
              "examDate": "2025-06-04",
              "courseName": "Computer Networks",
              "courseCode": "CSE2PM03A",
              "day": "Wednesday",
              "date": "04-06-2025",
              "time": "10:30 am - 12:00 pm",
              "room_seatno": "",
              "remarks": null,
              "eligibility": "Yes"
            }
          ]
        ]
      }
    },
    "AttendanceData": [],
    "IsDisciplineIncidentPresent": false,
    "IsTQMQFeedbackCompleted": false
  }
} */
@Serializable
private data class ExamHallTicketResponse(@SerialName("Item") val item: ExamHallTicketItem)

@Serializable
private data class ExamHallTicketItem(
    @SerialName("IntegrationData") val integrationData: ExamHallTicketIntegrationData)

@Serializable
private data class ExamHallTicketIntegrationData(@SerialName("body") val body: ExamHallTicket)

suspend fun getExams(
    client: HttpClient,
    studentUniqueId: String,
    termCode: String
): ExamHallTicket {
    /*  curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/integratons/api/data/exam-pro' \
          -H 'authorization: Bearer TOKEN' \
          -H 'content-type: application/json' \
          -H 'x-applicationname: connectportal' \
          -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
          -H 'x-requestfrom: web' \
          --data-raw '{"StudentUniqueId":"c9bef136-396a-441e-9370-876fda382b20","ActivityCode":"hallticket","TermCode":"SEM_4"}'
    */
    val response = client.post("apigateway/integratons/api/data/exam-pro") {
        contentType(ContentType.Application.Json)
        header("x-applicationname", "connectportal")
        header("x-appsecret", CLIENT_SECRET)
        header("x-requestfrom", "web")
        setBody(ExamHallTicketRequest(studentUniqueId, termCode = termCode))
    }
    val body: ExamHallTicketResponse = response.body()
    return body.item.integrationData.body
}
