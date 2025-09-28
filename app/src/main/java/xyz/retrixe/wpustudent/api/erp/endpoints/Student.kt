package xyz.retrixe.wpustudent.api.erp.endpoints

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import xyz.retrixe.wpustudent.api.erp.entities.CourseAttendanceDetail
import xyz.retrixe.wpustudent.api.erp.entities.CourseAttendanceSummary
import xyz.retrixe.wpustudent.api.erp.entities.ExamHallTicket
import xyz.retrixe.wpustudent.api.erp.entities.Event
import xyz.retrixe.wpustudent.api.erp.entities.StudentBasicInfo

suspend fun retrieveStudentBasicInfo(client: HttpClient): StudentBasicInfo {
    /*  curl 'https://erp.mitwpu.edu.in/ERP_Main.aspx' \
          -H 'accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*SLASH*;q=0.8,application/signed-exchange;v=b3;q=0.7' \
          -H 'accept-language: en-US,en;q=0.9' \
          -b 'ASP.NET_SessionId=CENSORED; AuthToken=CENSORED' \
          -H 'priority: u=0, i' \
          -H 'referer: https://erp.mitwpu.edu.in/login.aspx' \
          -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36'
    */
    val response = client.get("ERP_Main.aspx")

    if (response.request.url.encodedPath == "/Login.aspx")
        throw ResponseException(response, "Logged out")
    else if (response.request.url.encodedPath != "/ERP_Main.aspx")
        throw ResponseException(response, "Unknown redirect")

    val document = Jsoup.parse(response.bodyAsText())
    return StudentBasicInfo(
        document.select("span#span_userid").text().trim(),
        document.select("h6#span_username").text().trim().replace("- ", ""),
        document.select("span#span_regular").text().trim(),
        document.select("span#span_courseyear").text().trim(),
        document.select("img#imgprofile").attr("src"))
}

suspend fun getAttendanceSummary(client: HttpClient): List<CourseAttendanceSummary> {
    /*  curl 'https://erp.mitwpu.edu.in/STUDENT/SelfAttendence.aspx?MENU_CODE=MWEBSTUATTEN_SLF_ATTEN' \
          -H 'accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*SLASH*;q=0.8,application/signed-exchange;v=b3;q=0.7' \
          -H 'accept-language: en-US,en;q=0.9' \
          -b 'ASP.NET_SessionId=CENSORED; AuthToken=CENSORED' \
          -H 'priority: u=0, i' \
          -H 'referer: https://erp.mitwpu.edu.in/ERP_Main.aspx' \
          -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36'
    */
    val response = client.get("STUDENT/SelfAttendence.aspx") {
        url.parameters.append("MENU_CODE", "MWEBSTUATTEN_SLF_ATTEN")
    }

    val document = Jsoup.parse(response.bodyAsText())
    val attendanceSummary = arrayListOf<CourseAttendanceSummary>()
    val rows = document.select("div.infor-table").select("tr")
    for (row in rows) {
        if (row.hasClass("tblAltRowStyle") || row.hasClass("tblRowStyle")) {
            val cells = row.children()
            if (cells.size != 4 && cells.size != 6) {
                continue // We have no idea to handle this situation
            }
            val idxStart = if (cells.size == 4) -2 else 0
            val subjectName =
                if (cells.size == 4) attendanceSummary.lastOrNull()?.subjectName ?: "Unknown"
                else cells[1].text()
            attendanceSummary.add(CourseAttendanceSummary(
                cells[idxStart + 2].select("a").attr("id"),
                subjectName,
                cells[idxStart + 2].select("a").text(),
                cells[idxStart + 3].text().trim().toInt(),
                cells[idxStart + 4].text().trim().toInt(),
                cells[idxStart + 3].text().trim().toDouble()
            ))
        }
    }
    return attendanceSummary
}

@Serializable
private data class AttendanceDetailsRequest(
    val strStudentId: String,
    val strSemId: String,
    val strSubDetId: String,
    val strAppNo: String,
)

suspend fun getAttendanceDetails(client: HttpClient): List<CourseAttendanceDetail> {
    /* curl 'https://erp.mitwpu.edu.in/STUDENT/SelfAttendence.aspx/GetAttDtls' \
         -H 'accept: application/json, text/javascript, *SLASH*; q=0.01' \
         -H 'accept-language: en-US,en;q=0.9' \
         -H 'content-type: application/json; charset=UTF-8' \
         -b 'ASP.NET_SessionId=CENSORED; AuthToken=CENSORED' \
         -H 'origin: https://erp.mitwpu.edu.in' \
         -H 'priority: u=0, i' \
         -H 'referer: https://erp.mitwpu.edu.in/STUDENT/SelfAttendence.aspx?MENU_CODE=MWEBSTUATTEN_SLF_ATTEN' \
         -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36' \
         -H 'x-requested-with: XMLHttpRequest' \
         --data-raw $'{strStudentId: \'1032233145\',strSemId:\'20\',strSubDetId:\'87844\',strAppNo:\'1\'}'
    */
    val response = client.post("STUDENT/SelfAttendence.aspx/GetAttDtls") {
        contentType(ContentType.Application.Json)
        header("x-requested-with", "XMLHttpRequest")
        // FIXME
        setBody(AttendanceDetailsRequest("", "", "", ""))
    }

    @Serializable data class Body(val d: List<CourseAttendanceDetail>)
    val body: Body = response.body()
    return body.d
}

suspend fun getEvents(term: String): List<Event> {
    delay(500L)
    val academicCalendar = if (term.matches(Regex("^SEMESTER-II?\\("))) {
        arrayOf(
            Event("Commencement of Term", "Odd Semester", "2025-07-15T00:00:00"),
            Event("Mid Term Exam", "Odd Semester", "2025-10-06T00:00:00", "2025-10-17T00:00:00"),
            // Event("Diwali Vacation", "Odd Semester", "2025-10-18T00:00:00", "2025-10-25T00:00:00")
            Event("Last Instructional Day", "Odd Semester", "2025-12-12T00:00:00"),
            Event("Term End Exam", "Odd Semester", "2025-12-17T00:00:00", "2025-12-31T00:00:00"),
            Event("Winter Vacation", "Odd Semester", "2026-01-01T00:00:00", "2026-01-04T00:00:00"),
            Event("Commencement of Term", "Even Semester", "2026-01-05T00:00:00"),
            Event("Mid Term Exam", "Even Semester", "2026-03-09T00:00:00", "2026-03-20T00:00:00"),
            Event("Last Instructional Day", "Even Semester", "2026-05-09T00:00:00"),
            Event("Term End Exam", "Even Semester", "2026-05-14T00:00:00", "2026-06-15T00:00:00"),
            Event("Summer Vacation", "Even Semester", "2026-06-16T00:00:00", "2026-07-05T00:00:00"),
            Event("Commencement of Next Academic Year", "Even Semester", "2026-07-06T00:00:00"),
        )
    } else {
        arrayOf(
            Event("Commencement of Term", "Odd Semester", "2025-07-07T00:00:00"),
            Event("Induction", "Odd Semester", "2025-07-07T00:00:00", "2025-07-08T00:00:00"),
            Event("Mid Term Exam", "Odd Semester", "2025-09-29T00:00:00", "2025-10-10T00:00:00"),
            // Event("Diwali Vacation", "Odd Semester", "2025-10-18T00:00:00", "2025-10-25T00:00:00")
            Event("Last Instructional Day", "Odd Semester", "2025-11-18T00:00:00"),
            Event("Term End Exam (Backlog)", "Odd Semester", "2025-11-19T00:00:00", "2025-11-30T00:00:00"),
            Event("Term End Exam (Regular)", "Odd Semester", "2025-12-01T00:00:00", "2025-12-22T00:00:00"),
            Event("Winter Vacation", "Odd Semester", "2025-12-23T00:00:00", "2026-01-04T00:00:00"),
            Event("Commencement of Term", "Even Semester", "2026-01-05T00:00:00"),
            Event("Mid Term Exam", "Even Semester", "2026-03-09T00:00:00", "2026-03-20T00:00:00"),
            Event("Last Instructional Day", "Even Semester", "2026-05-09T00:00:00"),
            Event("Term End Exam (Regular + Backlog)", "Even Semester", "2026-05-14T00:00:00", "2026-06-15T00:00:00"),
            Event("Summer Vacation", "Even Semester", "2026-06-16T00:00:00", "2026-07-05T00:00:00"),
            Event("Commencement of Next Academic Year", "Even Semester", "2026-07-06T00:00:00"),
        )
    }
    return listOf(
        *academicCalendar,
        Event("Republic Day", "National Holiday", "2025-01-26T00:00:00"),
        Event("Shivjayanti", "State Holiday", "2025-02-19T00:00:00"),
        Event("Mahashivratri", "State Holiday", "2025-02-26T00:00:00"),
        Event("Dhulivandan (Holi 2nd day)", "State Holiday", "2025-03-14T00:00:00"),
        Event("Gudhi Padva", "State Holiday", "2025-03-30T00:00:00"),
        Event("Ramzan Eid (Eid-Al-Fitr)", "State Holiday", "2025-03-31T00:00:00"),
        Event("Dr. Babasaheb Ambedkar Jayanti", "State Holiday", "2025-04-14T00:00:00"),
        Event("Maharashtra Day", "State Holiday", "2025-05-01T00:00:00"),
        Event("Bakri Id (Eid-Al-Adha)", "State Holiday", "2025-06-07T00:00:00"),
        Event("Ashadhi Ekadashi", "State Holiday", "2025-07-06T00:00:00"),
        Event("Rakshabandhan", "State Holiday", "2025-08-09T00:00:00"),
        Event("Independence Day", "National Holiday", "2025-08-15T00:00:00"),
        Event("Ganesh Chaturthi", "State Holiday", "2025-08-27T00:00:00"),
        Event("Gouri Poojan", "State Holiday", "2025-09-01T00:00:00"),
        Event("Anant Chaturdashi", "State Holiday", "2025-09-06T00:00:00"),
        Event("Gandhi Jayanti", "National Holiday", "2025-10-02T00:00:00"),
        Event("Vijaya Dashmi (Dasara)", "State Holiday", "2025-10-02T00:00:00"),
        Event("Diwali Holidays", "State Holiday", "2025-10-18T00:00:00", "2025-10-25T00:00:00"),
        Event("Christmas", "State Holiday", "2025-12-25T00:00:00"),
    )
}

// TODO: Exams API is still built for PwC, until hall tickets are released, it can't be ported to ERP

@Serializable
private data class ExamHallTicketRequest(
    @SerialName("StudentUniqueId") val studentUniqueId: String,
    @SerialName("ActivityCode") val activityCode: String = "hallticket",
    @SerialName("TermCode") val termCode: String,
)

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
        // header("x-appsecret", CLIENT_SECRET)
        header("x-requestfrom", "web")
        setBody(ExamHallTicketRequest(studentUniqueId, termCode = termCode))
    }
    val body: ExamHallTicketResponse = response.body()
    return body.item.integrationData.body
}
