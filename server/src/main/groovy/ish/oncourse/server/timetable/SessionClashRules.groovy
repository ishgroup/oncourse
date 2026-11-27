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
        filter = filter.andExp(overlapCondition(TutorAttendance.START_DATETIME, TutorAttendance.END_DATETIME, start, end))

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
        filter = filter.andExp(overlapCondition(Session.START_DATETIME, Session.END_DATETIME, start, end))

        ObjectSelect<Session> query = ObjectSelect.query(Session)
                .where(filter)
                .prefetch(Session.COURSE_CLASS.dot(CourseClass.COURSE).joint())
        if (limit != null) {
            query = query.limit(limit)
        }
        return query.select(context)
    }

    /**
     * Tutor rosters in classes that aren't cancelled which may clash with any period within the window.
     * Use {@link #overlaps(Date, Date, Date, Date)} to find the ones which clash with a period.
     */
    static List<TutorAttendance> getTutorRosters(ObjectContext context, Collection<Long> contactIds, Date windowStart, Date windowEnd) {
        if (contactIds.empty) {
            return []
        }
        Expression filter = TutorAttendance.COURSE_CLASS_TUTOR.dot(CourseClassTutor.TUTOR).dot(Tutor.CONTACT).dot(Contact.ID).in(contactIds)
        filter = filter.andExp(TutorAttendance.SESSION.dot(Session.COURSE_CLASS).dot(CourseClass.IS_CANCELLED).isFalse())
        filter = filter.andExp(mayOverlapCondition(TutorAttendance.START_DATETIME, TutorAttendance.END_DATETIME, windowStart, windowEnd))

        return ObjectSelect.query(TutorAttendance)
                .where(filter)
                // every step is prefetched, as relationships which are not are fetched one by one
                .prefetch(TutorAttendance.COURSE_CLASS_TUTOR.disjoint())
                .prefetch(TutorAttendance.COURSE_CLASS_TUTOR.dot(CourseClassTutor.TUTOR).disjoint())
                .prefetch(TutorAttendance.COURSE_CLASS_TUTOR.dot(CourseClassTutor.TUTOR).dot(Tutor.CONTACT).disjoint())
                .prefetch(TutorAttendance.SESSION.disjoint())
                .prefetch(TutorAttendance.SESSION.dot(Session.COURSE_CLASS).disjoint())
                .prefetch(TutorAttendance.SESSION.dot(Session.COURSE_CLASS).dot(CourseClass.COURSE).disjoint())
                .prefetch(TutorAttendance.SESSION.dot(Session.ROOM).disjoint())
                .prefetch(TutorAttendance.SESSION.dot(Session.ROOM).dot(Room.SITE).disjoint())
                .select(context)
    }

    /**
     * Sessions of classes that aren't cancelled which are booked in the rooms for a period that may clash with any
     * period within the window. Use {@link #overlaps(Date, Date, Date, Date)} to find the ones which clash with a period.
     */
    static List<Session> getRoomBookings(ObjectContext context, Collection<Long> roomIds, Date windowStart, Date windowEnd) {
        if (roomIds.empty) {
            return []
        }
        Expression filter = Session.ROOM.dot(Room.ID).in(roomIds)
        filter = filter.andExp(Session.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).isFalse())
        filter = filter.andExp(mayOverlapCondition(Session.START_DATETIME, Session.END_DATETIME, windowStart, windowEnd))

        return ObjectSelect.query(Session)
                .where(filter)
                // every step is prefetched, as relationships which are not are fetched one by one
                .prefetch(Session.COURSE_CLASS.disjoint())
                .prefetch(Session.COURSE_CLASS.dot(CourseClass.COURSE).disjoint())
                .prefetch(Session.ROOM.disjoint())
                .prefetch(Session.ROOM.dot(Room.SITE).disjoint())
                .select(context)
    }

    /**
     * The clash condition of the queries above, checked in memory. Like in the database, a missing date matches nothing.
     */
    static boolean overlaps(Date selfStart, Date selfEnd, Date start, Date end) {
        return (after(selfStart, start) && after(end, selfStart)) ||
                (after(selfEnd, start) && after(end, selfEnd)) ||
                (notAfter(selfStart, start) && notAfter(end, selfEnd)) ||
                (notAfter(start, selfStart) && notAfter(selfEnd, end))
    }

    private static boolean after(Date date, Date other) {
        return date != null && other != null && date.after(other)
    }

    private static boolean notAfter(Date date, Date other) {
        return date != null && other != null && !date.after(other)
    }

    private static Expression overlapCondition(Property<Date> startProperty, Property<Date> endProperty, Date start, Date end) {
        return startProperty.gt(start).andExp(startProperty.lt(end))
                .orExp(endProperty.gt(start).andExp(endProperty.lt(end)))
                .orExp(startProperty.lte(start).andExp(endProperty.gte(end)))
                .orExp(startProperty.gte(start).andExp(endProperty.lte(end)))
    }

    /**
     * Matches every record which {@link #overlapCondition} matches for some period within the window, that is a period
     * whose start and end are both between {@code windowStart} and {@code windowEnd}. Each term matches at least what the
     * same term of {@link #overlapCondition} matches.
     */
    private static Expression mayOverlapCondition(Property<Date> startProperty, Property<Date> endProperty, Date windowStart, Date windowEnd) {
        return startProperty.gt(windowStart).andExp(startProperty.lt(windowEnd))
                .orExp(endProperty.gt(windowStart).andExp(endProperty.lt(windowEnd)))
                .orExp(startProperty.lte(windowEnd).andExp(endProperty.gte(windowStart)))
                .orExp(startProperty.gte(windowStart).andExp(endProperty.lte(windowEnd)))
    }
}
