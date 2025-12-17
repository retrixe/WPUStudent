package xyz.retrixe.wpustudent.api.erp.mocks

val MOCKS = mapOf(
    "/ERP_Main.aspx" to mockRetrieveStudentBasicInfo,
    "/STUDENT/SelfAttendence.aspx" to mockGetAttendanceSummary,
    "/STUDENT/SelfAttendence.aspx/GetAttDtls" to mockGetAttendanceDetails
)
