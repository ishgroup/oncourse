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

import javax.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.server.api.dao.CourseClassTutorDao
import ish.oncourse.server.api.dao.TutorAttendanceDao
import ish.oncourse.server.api.v1.model.TutorAttendanceDTO
import ish.oncourse.server.api.v1.model.TutorAttendanceTypeDTO
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.TutorAttendance
import ish.oncourse.server.users.SystemUserService
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang.time.DateUtils

import java.util.concurrent.TimeUnit

@CompileStatic
class TutorAttendanceApiService extends EntityApiService<TutorAttendanceDTO, TutorAttendance, TutorAttendanceDao> {

    @Inject
    private SystemUserService userService

    @Inject
    private CourseClassTutorDao classTutorDao

    @Override
    Class<TutorAttendance> getPersistentClass() {
        return TutorAttendance
    }

    @Override
    TutorAttendanceDTO toRestModel(TutorAttendance cayenneModel) {
        TutorAttendanceDTO dto = new TutorAttendanceDTO()
        dto.id = cayenneModel.id
        dto.courseClassTutorId = cayenneModel.courseClassTutor.id
        dto.contactName = cayenneModel.courseClassTutor.tutor.contact.fullName
        dto.attendanceType = TutorAttendanceTypeDTO.values()[0].fromDbType(cayenneModel.attendanceType)
        dto.note = cayenneModel.note
        dto.actualPayableDurationMinutes = cayenneModel.actualPayableDurationMinutes
        dto.hasPayslip = cayenneModel.hasPayslips()
        dto.start = LocalDateUtils.dateToTimeValue(cayenneModel.startDatetime)
        dto.end = LocalDateUtils.dateToTimeValue(cayenneModel.endDatetime)
        dto.contactId = cayenneModel.courseClassTutor.tutor.contact.id
        dto.payslipIds = cayenneModel.payslips*.id
        dto
    }

    @Override
    TutorAttendance toCayenneModel(TutorAttendanceDTO dto, TutorAttendance cayenneModel) {
        cayenneModel.attendanceType = dto.attendanceType.dbType
        cayenneModel.note = dto.note
        cayenneModel.actualPayableDurationMinutes = dto.actualPayableDurationMinutes
        cayenneModel.markedByUser = cayenneModel.context.localObject(userService.currentUser)
        cayenneModel.startDatetime = LocalDateUtils.timeValueToDate(dto.start)
        cayenneModel.endDatetime = LocalDateUtils.timeValueToDate(dto.end)
        cayenneModel
    }

    @Override
    void validateModelBeforeSave(TutorAttendanceDTO dto, ObjectContext context, Long id) {

        if (dto.attendanceType == null) {
            validator.throwClientErrorException(id, 'attendanceType', "Attendance type is required")
        }
        if (dto.start == null) {
            validator.throwClientErrorException(id, 'start', "Roster start is required")
        }
        if (dto.end == null) {
            validator.throwClientErrorException(id, 'end', "Roster end is required")
        }
        if(dto.start != null && dto.end != null && dto.start.isAfter(dto.end)){
            validator.throwClientErrorException(id, 'end', "Roster end date is before roster start date")
        }
        if (dto.actualPayableDurationMinutes == null) {
            validator.throwClientErrorException(id, 'actualPayableDurationMinutes', "Roster end is required")
        }
        
        if (id != null) {
            TutorAttendance attendance = getEntityAndValidateExistence(context, dto.id)

            if (attendance.hasPayslips() && (attendance.attendanceType != dto.attendanceType.dbType)) {
                validator.throwClientErrorException(id, 'attendanceType', "Attendance with linked payslip cannot be changed")
            }
        }
    }

    

    @Override
    void validateModelBeforeRemove(TutorAttendance cayenneModel) {
        if (cayenneModel.hasPayslips()) {
            validator.throwClientErrorException(cayenneModel.session.id, 'tutorAttendances', "Unable to unlink tutor: $cayenneModel.courseClassTutor.tutor.contact.fullName, payslip already generated for this session" )
        }
    }

    @Override
    List<TutorAttendanceDTO> getList(Long sessionId) {
        entityDao.getBySessionId(cayenneService.newContext, sessionId).collect {toRestModel(it)}
    }

    void updateList(Session session, List<TutorAttendanceDTO> attendanceDTOList) {
        ObjectContext context = session.objectContext

        List<TutorAttendance> attendancesToDelete = session.sessionTutors.findAll {!(it.id in attendanceDTOList*.id) }
        attendancesToDelete.each {validateModelBeforeRemove(it)}
        context.deleteObjects(attendancesToDelete)
        
        attendanceDTOList.each { dto ->
            TutorAttendance attendance

            if (!dto.id) {
                attendance = entityDao.newObject(context)
                attendance.session = session

                //handle x-validate request when attendance has no real tutor role id yet
                if (dto.courseClassTutorId) {
                    CourseClassTutor tutorRole = classTutorDao.getById(context, dto.courseClassTutorId)
                    if (!tutorRole) {
                        validator.throwClientErrorException(session.id, 'tutorAttendances', "Tutor role doesn't exist")
                    }
                    attendance.courseClassTutor = tutorRole
                }
            } else {
                attendance = getEntityAndValidateExistence(context, dto.id)
            }
            validateModelBeforeSave(dto, context, dto.id)
            attendance = toCayenneModel(dto, attendance)
            checkDates(session, attendance)
        }
    }

    private static void checkDates(Session session, TutorAttendance tutorAttendance){
        def startDatetime = tutorAttendance.startDatetime
        def endDatetime = tutorAttendance.endDatetime
        if(!DateUtils.isSameDay(startDatetime, session.startDatetime)){
            def hoursBetween = TimeUnit.MILLISECONDS.toHours(endDatetime.getTime() - startDatetime.getTime())

            def sessionStartCalendar = new GregorianCalendar()
            sessionStartCalendar.setTime(session.startDatetime)

            GregorianCalendar calendar = new GregorianCalendar()
            calendar.setTime(startDatetime)
            calendar.set(Calendar.YEAR, sessionStartCalendar.get(Calendar.YEAR))
            calendar.set(Calendar.DAY_OF_YEAR, sessionStartCalendar.get(Calendar.DAY_OF_YEAR))

            tutorAttendance.setStartDatetime(calendar.getTime())
            calendar.add(Calendar.HOUR, hoursBetween as int)
            tutorAttendance.setEndDatetime(calendar.getTime())
        }
    }
}
