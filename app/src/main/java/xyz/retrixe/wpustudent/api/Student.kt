package xyz.retrixe.wpustudent.api

/* FIXME
curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/connect-portal/api/studentloginbasicinfo' \
  -H 'authorization: Bearer BEARER' \
  -H 'x-applicationname: connectportal' \
  -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
  -H 'x-requestfrom: web'

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
