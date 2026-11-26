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
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.cayenne.TutorAttendance
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect

/**
 * Session clash rules shared by the class timetable validation ({@link ish.oncourse.server.api.v1.function.SessionValidator})
 * and the date range clash check ({@link TimetableClashService}).
 */
@CompileStatic
class SessionClashRules {

    /**
     * Periods clash when they overlap. A period that starts when the other ends is not a clash.
     */
    static <T extends Comparable<? super T>> boolean isClashed(T selfStart, T selfEnd, T start, T end) {
        return (selfStart >= start && selfStart < end) ||
                (selfEnd > start && selfEnd <= end) ||
                (selfStart <= start && selfEnd >= end) ||
                (selfStart >= start && selfEnd <= end)
    }

    /**
     * Tutor rosters in classes that aren't cancelled which clash with the given period.
     *
     * @param exceptClassId rosters of this class are skipped; null checks every class
     * @param limit maximum number of rosters to return; null returns all of them
     */
    static List<TutorAttendance> getTutorRosterClashes(ObjectContext context, Date start, Date end, Long contactId, Long exceptClassId, Integer limit) {

        //tutor qualifier
        Expression filter = TutorAttendance.COURSE_CLASS_TUTOR.dot(CourseClassTutor.TUTOR).dot(Tutor.CONTACT).dot(Contact.ID).eq(contactId)
        //only active classes
        filter = filter.andExp(TutorAttendance.SESSION.dot(Session.COURSE_CLASS).dot(CourseClass.IS_CANCELLED).isFalse())
        if (exceptClassId != null) {
            filter = filter.andExp(TutorAttendance.SESSION.dot(Session.COURSE_CLASS).dot(CourseClass.ID).ne(exceptClassId))
        }
        filter = filter.andExp(overlaps(TutorAttendance.START_DATETIME, TutorAttendance.END_DATETIME, start, end))

        ObjectSelect<TutorAttendance> query = ObjectSelect.query(TutorAttendance)
                .where(filter)
                .prefetch(TutorAttendance.SESSION.dot(Session.COURSE_CLASS).dot(CourseClass.COURSE).joint())
        if (limit != null) {
            query = query.limit(limit)
        }
        return query.select(context)
    }

    /**
     * Sessions of classes that aren't cancelled which are booked in the room for a period that clashes with the given one.
     *
     * @param exceptClassId sessions of this class are skipped; null checks every class
     * @param limit maximum number of sessions to return; null returns all of them
     */
    static List<Session> getRoomBookingClashes(ObjectContext context, Date start, Date end, Long roomId, Long exceptClassId, Integer limit) {

        Expression filter = Session.ROOM.dot(Room.ID).eq(roomId)
        filter = filter.andExp(Session.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).isFalse())
        if (exceptClassId != null) {
            filter = filter.andExp(Session.COURSE_CLASS.dot(CourseClass.ID).ne(exceptClassId))
        }
        filter = filter.andExp(overlaps(Session.START_DATETIME, Session.END_DATETIME, start, end))

        ObjectSelect<Session> query = ObjectSelect.query(Session)
                .where(filter)
                .prefetch(Session.COURSE_CLASS.dot(CourseClass.COURSE).joint())
        if (limit != null) {
            query = query.limit(limit)
        }
        return query.select(context)
    }

    private static Expression overlaps(Property<Date> startProperty, Property<Date> endProperty, Date start, Date end) {
        return startProperty.gt(start).andExp(startProperty.lt(end))
                .orExp(endProperty.gt(start).andExp(endProperty.lt(end)))
                .orExp(startProperty.lte(start).andExp(endProperty.gte(end)))
                .orExp(startProperty.gte(start).andExp(endProperty.lte(end)))
    }
}
