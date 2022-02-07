/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.CourseDao
import ish.oncourse.server.api.dao.RoomDao
import ish.oncourse.server.api.dao.UnavailableRuleDao
import ish.oncourse.server.api.traits.SessionDTOTrait
import ish.oncourse.server.api.v1.model.TutorAttendanceDTO
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.TutorAttendance

import static ish.oncourse.server.api.v1.model.ClashTypeDTO.*
import ish.oncourse.server.api.v1.model.SessionDTO
import ish.oncourse.server.api.v1.model.SessionWarningDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.cayenne.UnavailableRule
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime

@CompileStatic
class SessionValidator {

    @Inject
    CourseDao courseDao

    @Inject
    CayenneService cayenneService

    @Inject
    ContactDao contactDao

    @Inject
    RoomDao roomDao

    @Inject
    UnavailableRuleDao unavailableRuleDao

    private DateFormat format = new SimpleDateFormat("E d MMM H:mm")


    List<SessionWarningDTO> validate(List<SessionDTO> sessions, Long classId) {
        List<SessionWarningDTO> result = []
        ObjectContext context = cayenneService.newContext
        sessions = sessions.findAll {it.end.isAfter(LocalDateTime.now()) }
        if (sessions.empty) {
            return result
        }
        Course course = courseDao.getById(context, sessions[0].courseId)
        List<UnavailableRule> courseUnavailableRules = course.unavailableRuleRelations*.rule
        List<UnavailableRule> holidays = unavailableRuleDao.getHolidays(context)

        sessions.each { s ->

            Date start = LocalDateUtils.timeValueToDate(s.start)
            Date end = LocalDateUtils.timeValueToDate(s.end)

            //validate persisted objects 
            result += validateSession(s, start, end, context, classId)
            
            //looking for clashes among other class sessions
            if (sessions.minus(s).any {isClashed(s.start, s.end, it.start, it.end)}) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = s.id
                warning.temporaryId = s.temporaryId
                warning.type = SESSION
                warning.message = "Class already has session for that time"
                result << warning
            }

            result += s.tutorAttendances.findAll { it ->
                (sessions.minus(s)*.tutorAttendances
                    .flatten() as List<TutorAttendanceDTO>)
                    .findAll {  a -> a.contactId == it.contactId }
                    .any { a -> isClashed(it.start, it.end, a.start, a.end) }
                }.collect {
                    SessionWarningDTO warning = new SessionWarningDTO()
                    warning.sessionId = s.id
                    warning.temporaryId = s.temporaryId
                    warning.referenceId = it.contactId
                    warning.type = TUTOR
                    warning.message = "Tutor already has roster for that time"
                    warning
                }

            List<UnavailableRule> clashes = courseUnavailableRules.findAll { it.isClashed(start, end) }
            if (!clashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = s.id
                warning.temporaryId = s.temporaryId
                warning.label = course.name
                warning.type = UNAVAILABLERULE
                warning.message = "Course $warning.label is busy: "

                clashes.each {
                    warning.message += "${it.explanation} \n"
                }

                result << warning
            }

            clashes = holidays.findAll { it.isClashed(start, end) }
            if (!clashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = s.id
                warning.temporaryId = s.temporaryId
                warning.type = UNAVAILABLERULE
                warning.message = "Whole of business busy: "

                clashes.each {
                    warning.message += "${it.explanation} \n"
                }

                result << warning
            }

        }

        return result
    }
    

    private List<SessionWarningDTO> validateSession(SessionDTO sessionDto, Date sessionStart, Date  sessionEnd, ObjectContext context, Long classId) {
        List<SessionWarningDTO> sessionWarning = []
        

        sessionDto.tutorAttendances.each { ta ->
            Contact contact = contactDao.getById(context, ta.contactId)
            Date rosterStart = LocalDateUtils.timeValueToDate(ta.start)
            Date rosterEnd = LocalDateUtils.timeValueToDate(ta.end)
            
            List<TutorAttendance> rosterClashes = getTutorSessionClashes(rosterStart,rosterEnd, ta.contactId, classId)
            if (!rosterClashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = sessionDto.id
                warning.temporaryId = sessionDto.temporaryId
                warning.referenceId = contact.id
                warning.label = contact.fullName
                warning.type = TUTOR
                warning.message = "Clash for $warning.label with class "
                rosterClashes.each {
                    format.setTimeZone(it.session.timeZone)
                    warning.message += "$it.session.courseClass.uniqueCode \n"
                }

                sessionWarning << warning
            }

            List<UnavailableRule> unavailableRuleClashes = contact.unavailableRuleRelations*.rule.findAll { it.isClashed(rosterStart, rosterEnd) }
            if (!unavailableRuleClashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = sessionDto.id
                warning.temporaryId = sessionDto.temporaryId
                warning.referenceId = contact.id
                warning.label = contact.fullName
                warning.type = TUTOR
                warning.message = "$warning.label is busy: "

                unavailableRuleClashes.each {
                    warning.message += "${it.explanation} \n"
                }

                sessionWarning << warning
            }
        }

        if (sessionDto.roomId != null) {
            Room room = roomDao.getById(context, sessionDto.roomId)
            List<Session> roomClashes = getRoomSessionClashes(sessionStart, sessionEnd, sessionDto.roomId, classId)
            if (!roomClashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = sessionDto.id
                warning.temporaryId = sessionDto.temporaryId
                warning.referenceId = room.id
                warning.label = room.name
                warning.type = ROOM
                warning.message = "Clash for Room $warning.label with class "

                roomClashes.each {
                    format.setTimeZone(it.timeZone)
                    warning.message += "$it.courseClass.uniqueCode \n"
                }

                sessionWarning << warning
            }

            List<UnavailableRule> unavailableRuleClashes = room.unavailableRuleRelations*.rule.findAll { it.isClashed(sessionStart, sessionEnd) }
            if (!unavailableRuleClashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = sessionDto.id
                warning.temporaryId = sessionDto.temporaryId
                warning.referenceId = room.id
                warning.label = room.name
                warning.type = ROOM
                warning.message = "Room $warning.label is busy: "

                unavailableRuleClashes.each {
                    warning.message += "${it.explanation} \n"
                }

                sessionWarning << warning
            }

            unavailableRuleClashes = room.site.unavailableRuleRelations*.rule.findAll { it.isClashed(sessionStart, sessionEnd) }
            if (!unavailableRuleClashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = sessionDto.id
                warning.temporaryId = sessionDto.temporaryId
                warning.referenceId = room.site.id
                warning.label = room.site.name
                warning.type = SITE
                warning.message = "Site $warning.label is busy: "

                unavailableRuleClashes.each {
                    warning.message += "${it.explanation} \n"
                }

                sessionWarning << warning
            }
        }

        return sessionWarning
    }

    private static boolean isClashed(LocalDateTime selfStart, LocalDateTime selfEnd, LocalDateTime start, LocalDateTime end) {
        return (selfStart >= start && selfStart < end) ||
                (selfEnd > start && selfEnd <= end) ||
                (selfStart <= start && selfEnd >= end) ||
                (selfStart >= start && selfEnd <= end)
    }
    

    private List<TutorAttendance> getTutorSessionClashes(Date start, Date end, Long contactId, Long classId) {

        //tutor qualifier
        Expression filter = TutorAttendance.COURSE_CLASS_TUTOR.dot(CourseClassTutor.TUTOR).dot(Tutor.CONTACT).dot(Contact.ID).eq(contactId)
        //only active classes
        filter = filter.andExp(TutorAttendance.SESSION.dot(Session.COURSE_CLASS).dot(CourseClass.IS_CANCELLED).isFalse())
        //if classId is null - that means class is not saved to db yet
        //othervice filter out same class attendances, they will be chacked on other step
        if (classId) {
            filter = filter.andExp(TutorAttendance.SESSION.dot(Session.COURSE_CLASS).dot(CourseClass.ID).ne(classId))
        }
        //time clash quilifier 
        filter = filter.andExp(
                TutorAttendance.START_DATETIME.gt(start).andExp(TutorAttendance.START_DATETIME.lt(end))
                        .orExp(TutorAttendance.END_DATETIME.gt(start).andExp(TutorAttendance.END_DATETIME.lt(end)))
                        .orExp(TutorAttendance.START_DATETIME.lte(start).andExp(TutorAttendance.END_DATETIME.gte(end)))
                        .orExp(TutorAttendance.START_DATETIME.gte(start).andExp(TutorAttendance.END_DATETIME.lte(end)))
        )

        return ObjectSelect.query(TutorAttendance)
                .where(filter)
                .prefetch(TutorAttendance.SESSION.dot(Session.COURSE_CLASS).dot(CourseClass.COURSE).joint())
                .limit(3)
                .select(cayenneService.newContext)
    }
    
    private List<Session> getRoomSessionClashes(Date start, Date end, Long roomId, Long classId) {

        Expression filter = Session.ROOM.dot(Room.ID).eq(roomId)
        filter = filter.andExp(Session.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).isFalse())
        if (classId != null) {
            filter = filter.andExp(Session.COURSE_CLASS.dot(CourseClass.ID).ne(classId))
        }
        filter = filter.andExp(
                Session.START_DATETIME.gt(start).andExp(Session.START_DATETIME.lt(end))
                        .orExp(Session.END_DATETIME.gt(start).andExp(Session.END_DATETIME.lt(end)))
                        .orExp(Session.START_DATETIME.lte(start).andExp(Session.END_DATETIME.gte(end)))
                        .orExp(Session.START_DATETIME.gte(start).andExp(Session.END_DATETIME.lte(end)))
        )
        
        return ObjectSelect.query(Session)
                .where(filter)
                .prefetch(Session.COURSE_CLASS.dot(CourseClass.COURSE).joint())
                .limit(3)
                .select(cayenneService.newContext)
    }

}

