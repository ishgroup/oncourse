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
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.CourseDao
import ish.oncourse.server.api.dao.RoomDao
import ish.oncourse.server.api.dao.UnavailableRuleDao
import ish.oncourse.server.api.traits.SessionDTOTrait
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

        Course course = courseDao.getById(context, sessions[0].courseId)
        List<UnavailableRule> courseUnavailableRules = course.unavailableRuleRelations*.rule
        List<UnavailableRule> holidays = unavailableRuleDao.getHolidays(context)

        sessions.each { s ->

            Date start = LocalDateUtils.timeValueToDate(s.start)
            Date end = LocalDateUtils.timeValueToDate(s.end)

            result += validateSession(s, start, end, context, classId)

            if (isClashed(s, sessions)) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = s.id
                warning.temporaryId = s.temporaryId
                warning.type = SESSION
                warning.message = "Class already has session for that time"
                result << warning
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

    List<Session> getTutorSessionClashes(Contact contact, Expression sessionFilter) {
        ObjectSelect.query(Session)
                .where(Session.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).isFalse())
                .and(Session.TUTORS.dot(Tutor.CONTACT).eq(contact))
                .and(sessionFilter)
                .prefetch(Session.COURSE_CLASS.dot(CourseClass.COURSE).joint())
                .select(cayenneService.newContext)
    }

    List<Session> getRoomSessionClashes(Room room, Expression sessionFilter) {
        ObjectSelect.query(Session)
                .where(Session.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).isFalse())
                .and(Session.ROOM.eq(room))
                .and(sessionFilter)
                .prefetch(Session.COURSE_CLASS.dot(CourseClass.COURSE).joint())
                .select(cayenneService.newContext)
    }


    private List<SessionWarningDTO> validateSession(SessionDTO dto, Date start, Date end, ObjectContext context, Long classId) {
        List<SessionWarningDTO> sessionWarning = []

        if (dto.end.isBefore(LocalDateTime.now())) {
            return sessionWarning
        }

        Expression sessionFilter = Session.START_DATETIME.gt(start).andExp(Session.START_DATETIME.lt(end))
                .orExp(Session.END_DATETIME.gt(start).andExp(Session.END_DATETIME.lt(end)))
                .orExp(Session.START_DATETIME.lte(start).andExp(Session.END_DATETIME.gte(end)))
                .orExp(Session.START_DATETIME.gte(start).andExp(Session.END_DATETIME.lte(end)))
        if (classId != null) {
            sessionFilter = sessionFilter.andExp(Session.COURSE_CLASS.dot(CourseClass.ID).ne(classId))
        }

        dto.contactIds.each { id ->
            Contact contact = contactDao.getById(context, id)
            List<Session> sessionClashes = getTutorSessionClashes(contact, sessionFilter)
            if (!sessionClashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = dto.id
                warning.temporaryId = dto.temporaryId
                warning.referenceId = contact.id
                warning.label = contact.fullName
                warning.type = TUTOR
                warning.message = "$warning.label is already booked for "
                sessionClashes.each {
                    format.setTimeZone(it.timeZone)
                    warning.message += "$it.courseClass.uniqueCode at ${format.format(it.startDatetime)}(${it.timeZone.ID}) \n"
                }

                sessionWarning << warning
            }

            List<UnavailableRule> unavailableRuleClashes = contact.unavailableRuleRelations*.rule.findAll { it.isClashed(start, end) }
            if (!unavailableRuleClashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = dto.id
                warning.temporaryId = dto.temporaryId
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

        if (dto.roomId != null) {
            Room room = roomDao.getById(context, dto.roomId)
            List<Session> sessionClashes = getRoomSessionClashes(room, sessionFilter)
            if (!sessionClashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = dto.id
                warning.temporaryId = dto.temporaryId
                warning.referenceId = room.id
                warning.label = room.name
                warning.type = ROOM
                warning.message = "Room $warning.label is already booked for "

                sessionClashes.each {
                    format.setTimeZone(it.timeZone)
                    warning.message += "$it.courseClass.uniqueCode at ${format.format(it.startDatetime)}(${it.timeZone.ID}) \n"
                }

                sessionWarning << warning
            }

            List<UnavailableRule> unavailableRuleClashes = room.unavailableRuleRelations*.rule.findAll { it.isClashed(start, end) }
            if (!unavailableRuleClashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = dto.id
                warning.temporaryId = dto.temporaryId
                warning.referenceId = room.id
                warning.label = room.name
                warning.type = ROOM
                warning.message = "Room $warning.label is busy: "

                unavailableRuleClashes.each {
                    warning.message += "${it.explanation} \n"
                }

                sessionWarning << warning
            }

            unavailableRuleClashes = room.site.unavailableRuleRelations*.rule.findAll { it.isClashed(start, end) }
            if (!unavailableRuleClashes.empty) {
                SessionWarningDTO warning = new SessionWarningDTO()
                warning.sessionId = dto.id
                warning.temporaryId = dto.temporaryId
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

    boolean isClashed(SessionDTO self, List<SessionDTO> classSessions) {
        return classSessions.findAll { it ->
            ((it.getId() != null && it.getId() != self.getId()) || (it.getTemporaryId() != null && it.getTemporaryId() != self.getTemporaryId())) &&
                    (
                            (self.getStart() >= it.getStart() && self.getStart() < it.getEnd()) ||
                                    (self.getEnd() > it.getStart()   && self.getEnd() <= it.getEnd()) ||
                                    (self.getStart() <= it.getStart() && self.getEnd() >= it.getEnd()) ||
                                    (self.getStart() >= it.getStart() && self.getEnd() <= it.getEnd())
                    )
        }.size() > 0

    }


}

