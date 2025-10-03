package xyz.retrixe.wpustudent.api.erp.mocks

val MOCKS = mapOf(
    "/ERP_Main.aspx" to mockRetrieveStudentBasicInfo,
    "/STUDENT/SelfAttendence.aspx" to mockGetAttendanceSummary,
    "" to mockGetAttendanceDetails
)
