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

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.UnavailableRuleDao
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactUnavailableRuleRelation
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.CourseUnavailableRuleRelation
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.RoomUnavailableRuleRelation
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.SiteUnavailableRuleRelation
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.cayenne.TutorAttendance
import ish.oncourse.server.cayenne.UnavailableRule
import ish.oncourse.server.cayenne.UnavailableRuleRelation
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.Persistent
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect

import java.time.LocalDate

import static ish.oncourse.server.timetable.TimetableClashType.*

/**
 * Checks the timetable of every class over a date range.
 */
@CompileStatic
class TimetableClashService {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private UnavailableRuleDao unavailableRuleDao

    /**
     * Finds the clashes of sessions that start between {@code from} and {@code to}, both included.
     * A session's day is taken in its site time zone.
     *
     * The rules are the class timetable ones (see {@link ish.oncourse.server.api.v1.function.SessionValidator}):
     * sessions of the same class that overlap, rooms booked by two classes, tutors rostered twice,
     * holidays the college has entered, and tutor, room, site and course unavailability.
     * Sessions of cancelled classes and sessions that have already finished are not checked.
     * Unlike the timetable, there is no limit on the number of clashes.
     *
     * @param siteId check only sessions in rooms of this site; null checks every session
     * @return the clashes sorted by date and time; a clash between two sessions is listed once
     */
    List<TimetableClash> check(LocalDate from, LocalDate to, Long siteId) {
        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException("Date range ${from} - ${to} is not valid.")
        }

        ObjectContext context = cayenneService.newContext
        List<Session> sessions = getSessions(context, from, to, siteId)
        Timetable timetable = new Timetable(context, sessions, unavailableRuleDao.getHolidays(context))

        Map<List<Object>, TimetableClash> clashes = new LinkedHashMap<>()
        sessions.each { session ->
            checkSession(session, timetable).each { clash ->
                clashes.putIfAbsent(key(clash), clash)
            }
        }
        return withoutSameClassTutorClashes(clashes.values())
                .sort(false, { TimetableClash a, TimetableClash b -> compare(a, b) } as Comparator<TimetableClash>)
    }

    private static List<Session> getSessions(ObjectContext context, LocalDate from, LocalDate to, Long siteId) {
        // sites can be in any time zone, so select a wider period and then check each session's day in its own zone
        Expression filter = Session.START_DATETIME.gte(LocalDateUtils.valueToDate(from.minusDays(2)))
                .andExp(Session.START_DATETIME.lt(LocalDateUtils.valueToDate(to.plusDays(3))))
                .andExp(Session.END_DATETIME.gt(new Date()))
                .andExp(Session.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).isFalse())
        if (siteId != null) {
            filter = filter.andExp(Session.ROOM.dot(Room.SITE).dot(Site.ID).eq(siteId))
        }

        return ObjectSelect.query(Session)
                .where(filter)
                .orderBy(Session.START_DATETIME.asc(), Session.ID.asc())
                // every step is prefetched, as relationships which are not are fetched one by one
                .prefetch(Session.ROOM.disjoint())
                .prefetch(Session.ROOM.dot(Room.SITE).disjoint())
                .prefetch(Session.COURSE_CLASS.disjoint())
                .prefetch(Session.COURSE_CLASS.dot(CourseClass.COURSE).disjoint())
                .prefetch(Session.COURSE_CLASS.dot(CourseClass.SESSIONS).disjoint())
                .prefetch(Session.COURSE_CLASS.dot(CourseClass.SESSIONS).dot(Session.ROOM).disjoint())
                .prefetch(Session.COURSE_CLASS.dot(CourseClass.SESSIONS).dot(Session.ROOM).dot(Room.SITE).disjoint())
                .prefetch(Session.SESSION_TUTORS.disjoint())
                .prefetch(Session.SESSION_TUTORS.dot(TutorAttendance.COURSE_CLASS_TUTOR).disjoint())
                .prefetch(Session.SESSION_TUTORS.dot(TutorAttendance.COURSE_CLASS_TUTOR).dot(CourseClassTutor.TUTOR).disjoint())
                .prefetch(Session.SESSION_TUTORS.dot(TutorAttendance.COURSE_CLASS_TUTOR).dot(CourseClassTutor.TUTOR).dot(Tutor.CONTACT).disjoint())
                .select(context)
                .findAll { startsWithin(it, from, to) }
    }

    private static boolean startsWithin(Session session, LocalDate from, LocalDate to) {
        LocalDate day = startDate(session)
        return !day.isBefore(from) && !day.isAfter(to)
    }

    private static LocalDate startDate(Session session) {
        return LocalDateUtils.dateToTimeValue(session.startDatetime, session.timeZone.toZoneId()).toLocalDate()
    }

    private static List<TimetableClash> checkSession(Session session, Timetable timetable) {
        List<TimetableClash> result = []
        TimeZone timeZone = session.timeZone
        Date start = session.startDatetime
        Date end = session.endDatetime
        CourseClass courseClass = session.courseClass

        courseClass.sessions
                .findAll { it != session && SessionClashRules.isClashed(start, end, it.startDatetime, it.endDatetime) }
                .each { result << timetable.sessionsClash(SESSION, session, it, courseClass.id, courseClass.uniqueCode) }

        session.sessionTutors.each { roster ->
            Contact tutor = roster.courseClassTutor.tutor.contact
            Date rosterStart = roster.startDatetime ?: start
            Date rosterEnd = roster.endDatetime ?: end
            // rosters of the same class count too, as the timetable checks them among the class sessions
            timetable.getRosters(tutor)
                    .findAll { it.session != session && SessionClashRules.overlaps(it.startDatetime, it.endDatetime, rosterStart, rosterEnd) }
                    .each { result << timetable.sessionsClash(TUTOR, session, it.session, tutor.id, tutor.fullName) }
            clashingRules(timetable.getRules(tutor), rosterStart, rosterEnd, timeZone)
                    .each { result << ruleClash(TUTOR_UNAVAILABLE, session, tutor.id, tutor.fullName, it) }
        }

        Room room = session.room
        if (room != null) {
            timetable.getBookings(room)
                    .findAll { it.courseClass.id != courseClass.id && SessionClashRules.overlaps(it.startDatetime, it.endDatetime, start, end) }
                    .each { result << timetable.sessionsClash(ROOM, session, it, room.id, room.name) }
            clashingRules(timetable.getRules(room), start, end, timeZone)
                    .each { result << ruleClash(ROOM_UNAVAILABLE, session, room.id, room.name, it) }
            clashingRules(timetable.getRules(room.site), start, end, timeZone)
                    .each { result << ruleClash(SITE_UNAVAILABLE, session, room.site.id, room.site.name, it) }
        }

        Course course = courseClass.course
        clashingRules(timetable.getRules(course), start, end, timeZone)
                .each { result << ruleClash(COURSE_UNAVAILABLE, session, course.id, course.name, it) }

        clashingRules(timetable.holidays, start, end, timeZone)
                .each { result << ruleClash(HOLIDAY, session, it.id, it.explanation, it) }

        return result
    }

    private static List<UnavailableRule> clashingRules(List<UnavailableRule> rules, Date start, Date end, TimeZone timeZone) {
        return rules.findAll { it.isClashed(start, end, timeZone) }
    }

    private static TimetableClash ruleClash(TimetableClashType type, Session session, Long referenceId, String referenceName, UnavailableRule rule) {
        return new TimetableClash(
                type: type,
                date: startDate(session),
                session: TimetableClashSession.of(session),
                referenceId: referenceId,
                referenceName: referenceName,
                ruleId: rule.id,
                ruleDescription: rule.explanation
        )
    }

    /**
     * Two overlapping sessions of a class are a SESSION clash, so the same tutor rostered on both isn't listed again.
     */
    private static List<TimetableClash> withoutSameClassTutorClashes(Collection<TimetableClash> clashes) {
        Set<List<Long>> sessionClashes = clashes.findAll { it.type == SESSION }.collect { sessionIds(it) }.toSet()
        return clashes.findAll {
            !(it.type == TUTOR && it.session.classId == it.otherSession.classId && sessionIds(it) in sessionClashes)
        }.toList()
    }

    /**
     * By date, time, clash type and then ids, so the order is stable.
     */
    private static int compare(TimetableClash a, TimetableClash b) {
        int result = a.date.compareTo(b.date)
        if (result == 0) {
            result = a.session.start.compareTo(b.session.start)
        }
        if (result == 0) {
            result = a.type.compareTo(b.type)
        }
        if (result == 0) {
            result = a.session.sessionId.compareTo(b.session.sessionId)
        }
        if (result == 0) {
            result = compareNullsFirst(a.otherSession?.sessionId, b.otherSession?.sessionId)
        }
        if (result == 0) {
            result = compareNullsFirst(a.referenceId, b.referenceId)
        }
        if (result == 0) {
            result = compareNullsFirst(a.ruleId, b.ruleId)
        }
        return result
    }

    private static int compareNullsFirst(Long a, Long b) {
        if (a == null || b == null) {
            return a == null ? (b == null ? 0 : -1) : 1
        }
        return a.compareTo(b)
    }

    /**
     * Two sessions clash with each other, so the same clash is found when either of them is checked.
     */
    private static List<Object> key(TimetableClash clash) {
        return [clash.type, clash.referenceId, clash.ruleId, sessionIds(clash)] as List<Object>
    }

    private static List<Long> sessionIds(TimetableClash clash) {
        return [clash.session.sessionId, clash.otherSession?.sessionId].findAll { it != null }.sort()
    }

    /**
     * The checked sessions and what they can clash with, loaded at once for all of them rather than per session:
     * tutor rosters and room bookings over the whole period, and the unavailability rules.
     */
    private static class Timetable {

        final List<UnavailableRule> holidays

        private final Set<Session> checked
        private final Map<Long, List<TutorAttendance>> rostersByTutor
        private final Map<Long, List<Session>> bookingsByRoom
        private final Map<Persistent, List<UnavailableRule>> rules = new HashMap<>()

        Timetable(ObjectContext context, List<Session> sessions, List<UnavailableRule> holidays) {
            this.holidays = holidays
            this.checked = new HashSet<>(sessions)

            Set<Contact> tutors = new LinkedHashSet<>()
            List<Date> rosterDates = []
            sessions.each { session ->
                session.sessionTutors.each { roster ->
                    tutors << roster.courseClassTutor.tutor.contact
                    rosterDates << (roster.startDatetime ?: session.startDatetime) << (roster.endDatetime ?: session.endDatetime)
                }
            }
            List<Session> sessionsInRooms = sessions.findAll { it.room != null }
            Set<Room> rooms = new LinkedHashSet<>(sessionsInRooms*.room)
            List<Date> roomDates = sessionsInRooms.collectMany { [it.startDatetime, it.endDatetime] }

            rostersByTutor = tutors.empty ? new HashMap<Long, List<TutorAttendance>>() :
                    SessionClashRules.getTutorRosters(context, tutors*.id, rosterDates.min(), rosterDates.max())
                            .groupBy { it.courseClassTutor.tutor.contact.id }
            bookingsByRoom = rooms.empty ? new HashMap<Long, List<Session>>() :
                    SessionClashRules.getRoomBookings(context, rooms*.id, roomDates.min(), roomDates.max())
                            .groupBy { it.room.id }

            loadRules(context, ContactUnavailableRuleRelation, ContactUnavailableRuleRelation.CONTACT, tutors)
            loadRules(context, RoomUnavailableRuleRelation, RoomUnavailableRuleRelation.ROOM, rooms)
            loadRules(context, SiteUnavailableRuleRelation, SiteUnavailableRuleRelation.SITE, new LinkedHashSet<Site>(rooms*.site))
            loadRules(context, CourseUnavailableRuleRelation, CourseUnavailableRuleRelation.COURSE, new LinkedHashSet<Course>(sessions*.courseClass*.course))
        }

        /**
         * Rosters of the tutor that may clash with the checked sessions.
         */
        List<TutorAttendance> getRosters(Contact tutor) {
            return rostersByTutor[tutor.id] ?: []
        }

        /**
         * Sessions in the room that may clash with the checked sessions.
         */
        List<Session> getBookings(Room room) {
            return bookingsByRoom[room.id] ?: []
        }

        List<UnavailableRule> getRules(Persistent entity) {
            return rules[entity] ?: []
        }

        /**
         * When both sessions are checked, the clash is listed for the one that starts first (then the one with the lower id),
         * so it is the same whichever of them finds it.
         */
        TimetableClash sessionsClash(TimetableClashType type, Session session, Session otherSession, Long referenceId, String referenceName) {
            boolean otherFirst = otherSession in checked &&
                    (otherSession.startDatetime < session.startDatetime ||
                            (otherSession.startDatetime == session.startDatetime && otherSession.id < session.id))
            Session first = otherFirst ? otherSession : session
            Session second = otherFirst ? session : otherSession
            return new TimetableClash(
                    type: type,
                    date: startDate(first),
                    session: TimetableClashSession.of(first),
                    otherSession: TimetableClashSession.of(second),
                    referenceId: referenceId,
                    referenceName: referenceName
            )
        }

        private void loadRules(ObjectContext context, Class<? extends UnavailableRuleRelation> type, Property relationOwner, Collection<? extends Persistent> owners) {
            if (owners.empty) {
                return
            }
            ObjectSelect.query(type)
                    .where(relationOwner.in(owners))
                    // a joint prefetch fails on these subclass entities
                    .prefetch(relationOwner.disjoint())
                    .prefetch(UnavailableRuleRelation.RULE.disjoint())
                    .select(context)
                    .each { UnavailableRuleRelation relation ->
                        rules.computeIfAbsent(relationOwner.getFrom(relation) as Persistent, { new ArrayList<UnavailableRule>() }).add(relation.rule)
                    }
        }
    }
}
