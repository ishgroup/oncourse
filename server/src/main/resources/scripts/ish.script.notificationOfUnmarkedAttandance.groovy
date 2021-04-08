def startOfDate = Calendar.getInstance().getTime().clearTime() - 1
def sessions = query {
    entity "Session"
    query "startDatetime is yesterday and courseClass.isCancelled is false and courseClass.isActive is true"
}

if (tagName != null && tagName != "") {
    sessions = sessions.findAll { s ->
        s.courseClass.course.hasTag(tagName)
    }
}


def sessionsWithUnmarkedAttendance = sessions.findAll { s ->
    s.attendance.findAll { a ->
        AttendanceType.UNMARKED.equals(a.attendanceType)
    }.size() > 0
}

if (!sessionsWithUnmarkedAttendance.empty) {
    def bodyContent = [
            'Dear Admin,',
            '',
            "The following sessions ran on ${startOfDate.format("d/M/yy")} and had unmarked attendance:"
    ]


    sessionsWithUnmarkedAttendance.each { s ->
        def tutors = s.tutors*.contact*.fullName.flatten()
        def tutorString = tutors.size() > 0 ? "Tutor${tutors.size() > 1 ? 's' : ''}: ${tutors.join(", ")}" : ''

        bodyContent << ''
        bodyContent << "${s.courseClass.course.name} (${s.courseClass.uniqueCode}) ${tutorString}"
        bodyContent << "Enrolled: ${s.courseClass.successAndQueuedEnrolments.size()}"
        bodyContent << "Unmarked attendance: ${s.attendance.findAll { a -> AttendanceType.UNMARKED.equals(a.attendanceType) }.size()}"
        bodyContent << "Absent: ${s.attendance.findAll { a -> [AttendanceType.DID_NOT_ATTEND_WITH_REASON, AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON].contains(a.attendanceType) }.size()}"
        bodyContent << "Attended: ${s.attendance.findAll { a -> [AttendanceType.ATTENDED, AttendanceType.PARTIAL].contains(a.attendanceType) }.size()}"

    }

    message {
        from preference.email.from
        subject 'Notification of unmarked attendance'
        to preference.email.admin
        content bodyContent.join(System.getProperty('line.separator'))
    }
}