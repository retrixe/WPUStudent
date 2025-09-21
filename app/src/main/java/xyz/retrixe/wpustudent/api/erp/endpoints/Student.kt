package xyz.retrixe.wpustudent.api.erp.endpoints

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.cookie
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
import xyz.retrixe.wpustudent.api.erp.entities.CourseAttendanceSummary
import xyz.retrixe.wpustudent.api.erp.entities.ExamHallTicket
import xyz.retrixe.wpustudent.api.erp.entities.Holiday
import xyz.retrixe.wpustudent.api.erp.entities.StudentBasicInfo

suspend fun retrieveStudentBasicInfo(
    client: HttpClient,
    token: String? = null // This function typically expects such a token where it is used
): StudentBasicInfo {
    /*  curl 'https://erp.mitwpu.edu.in/ERP_Main.aspx' \
          -H 'accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*SLASH*;q=0.8,application/signed-exchange;v=b3;q=0.7' \
          -H 'accept-language: en-US,en;q=0.9' \
          -b 'ASP.NET_SessionId=CENSORED; AuthToken=CENSORED' \
          -H 'priority: u=0, i' \
          -H 'referer: https://erp.mitwpu.edu.in/login.aspx' \
          -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36'
    */
    val response = client.get("ERP_Main.aspx") {
        if (token != null) {
            cookie("AuthToken", token.split("|")[0])
            cookie("ASP.NET_SessionId", token.split("|")[1])
        }
    }

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

suspend fun getHolidays(): List<Holiday> {
    delay(500L)
    return listOf(
        Holiday("Republic Day", "National Holiday", "2025-01-26T00:00:00"),
        Holiday("Shivjayanti", "State Holiday", "2025-02-19T00:00:00"),
        Holiday("Mahashivratri", "State Holiday", "2025-02-26T00:00:00"),
        Holiday("Dhulivandan (Holi 2nd day)", "State Holiday", "2025-03-14T00:00:00"),
        Holiday("Gudhi Padva", "State Holiday", "2025-03-30T00:00:00"),
        Holiday("Ramzan Eid (Eid-Al-Fitr)", "State Holiday", "2025-03-31T00:00:00"),
        Holiday("Dr. Babasaheb Ambedkar Jayanti", "State Holiday", "2025-04-14T00:00:00"),
        Holiday("Maharashtra Day", "State Holiday", "2025-05-01T00:00:00"),
        Holiday("Bakri Id (Eid-Al-Adha)", "State Holiday", "2025-06-07T00:00:00"),
        Holiday("Ashadhi Ekadashi", "State Holiday", "2025-07-06T00:00:00"),
        Holiday("Rakshabandhan", "State Holiday", "2025-08-09T00:00:00"),
        Holiday("Independence Day", "National Holiday", "2025-08-15T00:00:00"),
        Holiday("Ganesh Chaturthi", "State Holiday", "2025-08-27T00:00:00"),
        Holiday("Gouri Poojan", "State Holiday", "2025-09-01T00:00:00"),
        Holiday("Anant Chaturdashi", "State Holiday", "2025-09-06T00:00:00"),
        Holiday("Gandhi Jayanti", "National Holiday", "2025-10-02T00:00:00"),
        Holiday("Vijaya Dashmi (Dasara)", "State Holiday", "2025-10-02T00:00:00"),
        Holiday("Diwali Holidays", "State Holiday", "2025-10-18T00:00:00", "2025-10-25T00:00:00"),
        Holiday("Christmas", "State Holiday", "2025-12-25T00:00:00"),
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
