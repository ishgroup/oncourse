/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.timetable

import groovy.transform.CompileStatic
import groovy.transform.ToString
import ish.oncourse.server.cayenne.Session
import ish.util.LocalDateUtils

import java.time.LocalDateTime

/**
 * A session involved in a {@link TimetableClash}.
 */
@CompileStatic
@ToString(includeNames = true, ignoreNulls = true)
class TimetableClashSession {

    Long sessionId
    Long classId
    /**
     * Class code with the course code, for example ABC-1.
     */
    String classCode
    String courseName

    Long roomId
    String roomName
    Long siteId
    String siteName

    /**
     * Time zone of the session's site (the server time zone for a session without a room).
     */
    String timeZone

    /**
     * Start and end of the session in {@link #timeZone}.
     */
    LocalDateTime start
    LocalDateTime end

    static TimetableClashSession of(Session session) {
        TimeZone timeZone = session.timeZone
        return new TimetableClashSession(
                sessionId: session.id,
                classId: session.courseClass.id,
                classCode: session.courseClass.uniqueCode,
                courseName: session.courseClass.course.name,
                roomId: session.room?.id,
                roomName: session.room?.name,
                siteId: session.room?.site?.id,
                siteName: session.room?.site?.name,
                timeZone: timeZone.ID,
                start: LocalDateUtils.dateToTimeValue(session.startDatetime, timeZone.toZoneId()),
                end: LocalDateUtils.dateToTimeValue(session.endDatetime, timeZone.toZoneId())
        )
    }
}
