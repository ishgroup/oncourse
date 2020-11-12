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

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.oncourse.server.api.dao.CourseClassDao
import ish.oncourse.server.api.dao.CourseClassTutorDao
import ish.oncourse.server.api.dao.RoomDao
import ish.oncourse.server.api.dao.SessionDao
import ish.oncourse.server.api.dao.TutorAttendanceDao
import static ish.oncourse.server.api.servlet.ApiFilter.validateOnly
import ish.oncourse.server.api.v1.function.SessionValidator
import ish.oncourse.server.api.v1.model.SessionDTO
import ish.oncourse.server.api.v1.model.SessionWarningDTO
import static ish.oncourse.server.api.v1.service.impl.SessionApiImpl.CLASS_TEMP_ID
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.TutorAttendance
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.validation.ValidationException

class SessionApiService extends EntityApiService<SessionDTO, Session, SessionDao> {

    @Inject
    private CourseClassDao classDao

    @Inject
    private TutorAttendanceDao tutorAttendanceDao

    @Inject
    private CourseClassTutorDao classTutorDao

    @Inject
    private RoomDao roomDao

    @Inject
    private SessionValidator sessionValidator

    @Override
    Class<Session> getPersistentClass() {
        return Session
    }

    @Override
    SessionDTO toRestModel(Session session) {
        SessionDTO dto = new SessionDTO()
        dto.id = session.id
        dto.start = LocalDateUtils.dateToTimeValue(session.startDatetime)
        dto.end = LocalDateUtils.dateToTimeValue(session.endDatetime)
        dto.roomId = session.room?.id
        dto.room = session.room?.name
        dto.siteId = session.room?.site?.id
        dto.site = session.room?.site?.name
        dto.courseClassTutorIds = session.sessionTutors*.courseClassTutor*.id
        dto.contactIds = session.tutors*.contact*.id
        dto.courseId = session.courseClass.course.id
        dto.privateNotes = session.privateNotes
        dto.publicNotes = session.publicNotes
        dto.payAdjustment = session.payAdjustment
        dto.tutors = session.tutors*.contact*.fullName
        dto.name = session.courseClass.course.name
        if (session.room && !session.room.site.isVirtual) {
            dto.siteTimezone = session.room.site.localTimezone
        }
        dto.hasPaylines = session.payLines != null && !session.payLines.empty
        return dto
    }

    @Override
    Session toCayenneModel(SessionDTO dto, Session session) {
        ObjectContext context = session.context
        if (dto.roomId != null) {
            Room room = roomDao.getById(context, dto.roomId)
            session.room = room
        } else {
            session.room = null
        }

        session.startDatetime = LocalDateUtils.timeValueToDate(dto.start)
        session.endDatetime = LocalDateUtils.timeValueToDate(dto.end)
        session.publicNotes = dto.publicNotes
        session.privateNotes = dto.privateNotes
        session.payAdjustment = dto.payAdjustment
        List<TutorAttendance> tutorsToDelete = session.sessionTutors.findAll { !(it.courseClassTutor.id in dto.courseClassTutorIds) }
        session.context.deleteObjects(tutorsToDelete)
        dto.courseClassTutorIds.findAll { !(it in session.sessionTutors*.courseClassTutor.id) }.each { id ->
            CourseClassTutor classTutor = classTutorDao.getById(context, id)
            tutorAttendanceDao.newObject(session.context, session, classTutor)
        }
        session
    }

    List<SessionWarningDTO> update(Long classId, List<SessionDTO> dtoList) {

        List<SessionWarningDTO> responce = []
        ObjectContext context = cayenneService.newContext
        CourseClass courseClass = null
        if (!(validateOnly.get() && classId == CLASS_TEMP_ID)) {
            courseClass = classDao.getById(context, classId)
            courseClass.modifiedOn = new Date()
            if (dtoList.empty) {
                courseClass.startDateTime = null
                courseClass.endDateTime = null
                if (!courseClass.isDistantLearningCourse) {
                    courseClass.room = null
                }
                courseClass.sessionsCount = 0
            }
        }

        if (courseClass) {
            //delete
            List<Session> toDelte = courseClass.sessions.findAll { s -> !(s.id in dtoList*.id) }
            toDelte.each { s ->
                validateModelBeforeRemove(s)
                remove(s, context)
            }
        }

        //update
        dtoList.findAll { it.id != null }.each { dto ->
            Session session = validateModelBeforeSave(dto, context, dto.id, courseClass)
            toCayenneModel(dto, session)
        }

        //create
        dtoList.findAll { it.id == null }.each { dto ->
            Session session = entityDao.newObject(context, courseClass)
            validateModelBeforeSave(dto, context, null)
            toCayenneModel(dto, session)
        }

        try {
            save(context)
        } catch (ValidationException e) {
            if (validateOnly.get() && courseClass == null && e.validationResult?.failures?.find {it.description == "\"$Session.COURSE_CLASS.name\"  is required.".toString() }) {
                //Ignore class/tutorRole FK for validation of new record
            } else {
                throw new RuntimeException("Can not save sessions: $dtoList", e)
            }
        }
        //        collect all Clash warnings
        if (validateOnly.get() && !dtoList.empty && ((courseClass != null && !courseClass.isCancelled) || courseClass == null)) {
            responce = sessionValidator.validate(dtoList, classId)
        }

        return responce
    }

    Session validateModelBeforeSave(SessionDTO dto, ObjectContext context, Long id, CourseClass courseClass) {
        Session session = null

        if (id != null) {
            session = getEntityAndValidateExistence(context, id)
            if (!session.courseClass.equalsIgnoreContext(courseClass)) {
                    validator.throwClientErrorException(id, 'name',  "Session id:$id doesn't belong to Class: $courseClass.uniqueCode" )
            }
            List<TutorAttendance> tutorsToDelete = session.sessionTutors.findAll { !(it.courseClassTutor.id in dto.courseClassTutorIds) }
            TutorAttendance attendance = tutorsToDelete.find { it.hasPayslips() }
            if (attendance) {
                validator.throwClientErrorException(id, 'courseClassTutorIds', "Unable to unlink tutor: $attendance.courseClassTutor.tutor.contact.fullName, payslip already generated for this session" )
            }
        }
        if (courseClass != null && courseClass.course.isTraineeship &&
                (dto.courseClassTutorIds == null || (dto.courseClassTutorIds.isEmpty() && dto.temporaryTutorIds.isEmpty()))) {
            validator.throwClientErrorException(id, 'courseClassTutorIds', 'At least one tutor required for traineeship session.')
        }
        validateModelBeforeSave(dto, context, id)
        return session
    }



    @Override
    void validateModelBeforeSave(SessionDTO dto, ObjectContext context, Long id) {


        if (!dto.start) {
            validator.throwClientErrorException(id?:dto.temporaryId, 'start', 'Start date required.' )
        }
        if (!dto.end) {
            validator.throwClientErrorException(id?:dto.temporaryId, 'end', 'End date required.' )
        }
        if (dto.start > dto.end) {
            validator.throwClientErrorException(id?:dto.temporaryId, 'end', 'End date should be after session start date.' )
        }

        if (dto.payAdjustment == null) {
            validator.throwClientErrorException(id?:dto.temporaryId, 'payAdjustment', 'session duration .' )
        }

        dto.courseClassTutorIds.each { roleId ->
             if (classTutorDao.getById(context, roleId) == null) {
                 validator.throwClientErrorException(id?:dto.temporaryId, 'courseClassTutorIds', "Tutor role doesn't exist")
             }
        }

        if (dto.roomId != null && roomDao.getById(context, dto.roomId) == null) {
            validator.throwClientErrorException(id?:dto.temporaryId, 'roomId', "Room doesn't exist")
        }
    }

    @Override
    void validateModelBeforeRemove(Session session) {
        if (!session.payLines.empty) {
            validator.throwClientErrorException(session.id, null, "Unable to remove this session because it linked to paylines.")
        }
    }

    List<SessionDTO> getList(Long classId) {
        entityDao.getByClassId(cayenneService.newContext, classId)
                .collect {
                    toRestModel(it)
                }
    }

    void remove (Session session, ObjectContext context) {
        context.deleteObjects(session.sessionModules)
        context.deleteObjects(session.sessionTutors)
        context.deleteObjects(session.attendance)
        context.deleteObject(session)
    }

}
