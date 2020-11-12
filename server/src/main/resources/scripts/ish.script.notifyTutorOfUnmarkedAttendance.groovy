def sessionsWithUnmarkedAttendance = query {
    entity "Session"
    query "startDatetime after today - 7 days and startDatetime before today + 1 days and courseClass.isCancelled is false and courseClass.isActive is true and attendance.attendanceType is UNMARKED"
}

sessionsWithUnmarkedAttendance.each { s ->
    message {
        template tutorNoticeTemplate
        records s.tutorRoles*.tutor.flatten().unique()
        session s
        attendanceType AttendanceType
    }
}
