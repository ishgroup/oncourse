records = query {
    entity "TutorAttendance"
    query "startDatetime next week"
}

records.each { r ->
    kronosCreateEdit {
        scheduleName scheduleNameValue
        tutorAttendance r
    }
}