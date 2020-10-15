/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.each { CourseClass cc ->
    csv << [
            "courseName"                        : cc.course.name,
            "startDate"                         : cc.startDateTime?.format("dd/MM/yyyy"),
            "startDateTime"                     : cc.startDateTime?.format("h:mm a"),
            "endDate"                           : cc.endDateTime?.format("dd/MM/yyyy"),
            "endDateTime"                       : cc.endDateTime?.format("h:mm a"),
            "sessionsCount"                     : cc.sessionsCount.toString() + " sessions",
            "sessionsDuration"                  : cc.startDateTime?.format("h:mm a").toLowerCase().replace(":00", "") + " - " + cc.endDateTime?.format("h:mm a").toLowerCase().replace(":00", ""),
            "DayOfWeekOfFirstSession"           : cc.startDateTime?.format("EEE"),
            "roomName"                          : cc.room?.name,
            "roomSiteName"                      : cc.room?.site?.name,
            "tutors"                            : cc.tutorRoles?.collect { tr -> tr.tutor.fullName }.join(", "),
    ]
}
