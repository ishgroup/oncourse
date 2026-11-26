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

import java.time.LocalDate

/**
 * A clash found by {@link TimetableClashService}.
 */
@CompileStatic
@ToString(includeNames = true, ignoreNulls = true)
class TimetableClash {

    TimetableClashType type

    /**
     * Day of the clash: the start date of {@link #session} in its site time zone.
     */
    LocalDate date

    /**
     * The session that clashes. For a clash between two sessions it is the one that is listed first.
     */
    TimetableClashSession session

    /**
     * The session it clashes with for SESSION, ROOM and TUTOR clashes; null otherwise.
     */
    TimetableClashSession otherSession

    /**
     * What clashes: the class (SESSION), room (ROOM, ROOM_UNAVAILABLE), tutor's contact (TUTOR, TUTOR_UNAVAILABLE),
     * site (SITE_UNAVAILABLE), course (COURSE_UNAVAILABLE) or holiday (HOLIDAY).
     */
    Long referenceId

    /**
     * Class code, room name, tutor name, site name, course name or holiday description.
     */
    String referenceName

    /**
     * The holiday or unavailability rule the session falls on; null for SESSION, ROOM and TUTOR clashes.
     */
    Long ruleId
    String ruleDescription
}
