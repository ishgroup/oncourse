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
import groovy.transform.CompileStatic
import ish.oncourse.server.api.dao.TutorAttendanceDao
import ish.oncourse.server.api.v1.model.TutorAttendanceDTO
import ish.oncourse.server.api.v1.model.TutorAttendanceTypeDTO
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.TutorAttendance
import ish.oncourse.server.users.SystemUserService
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext

@CompileStatic
class TutorAttendanceApiService extends EntityApiService<TutorAttendanceDTO, TutorAttendance, TutorAttendanceDao> {

    @Inject
    private SystemUserService userService

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
        if (dto.actualPayableDurationMinutes == null) {
            validator.throwClientErrorException(id, 'actualPayableDurationMinutes', "Roster end is required")
        }
        
        if (id != null) {
            TutorAttendance attendance = getEntityAndValidateExistence(context, dto.id)

            if (attendance.hasPayslips() && (attendance.attendanceType != dto.attendanceType.dbType)) {
                validator.throwClientErrorException(id, 'attendanceType', "Attendance with linked payslip cannot be changed")
            }
        }

//        List<TutorAttendance> tutorsToDelete = session.sessionTutors.findAll { !(it.courseClassTutor.id in dto.courseClassTutorIds) }
//        TutorAttendance attendance = tutorsToDelete.find { it.hasPayslips() }
//        if (attendance) {
//            validator.throwClientErrorException(id, 'courseClassTutorIds', "Unable to unlink tutor: $attendance.courseClassTutor.tutor.contact.fullName, payslip already generated for this session" )
//        }
//        dto.courseClassTutorIds.each { roleId ->
//            if (classTutorDao.getById(context, roleId) == null) {
//                validator.throwClientErrorException(id?:dto.temporaryId, 'courseClassTutorIds', "Tutor role doesn't exist")
//            }
//        }

    }

    @Override
    void validateModelBeforeRemove(TutorAttendance cayenneModel) {
        throw new UnsupportedOperationException()
    }

    @Override
    List<TutorAttendanceDTO> getList(Long classId) {
        entityDao.getByClassId(cayenneService.newContext, classId).collect {toRestModel(it)}
    }

    void updateList(List<TutorAttendanceDTO> attendanceDTOList) {
//        attendanceDTOList*.courseClassTutorId.findAll { !(it in session.sessionTutors*.courseClassTutor.id) }.each { id ->
//            CourseClassTutor classTutor = classTutorDao.getById(context, id)
//            tutorAttendanceDao.newObject(session.context, session, classTutor)
//        }
//        List<TutorAttendance> tutorsToDelete = session.sessionTutors.findAll { !(it.courseClassTutor.id in dto.courseClassTutorIds) }
//        session.context.deleteObjects(tutorsToDelete)
        ObjectContext context = cayenneService.newContext
        attendanceDTOList.each { dto ->
            validateModelBeforeSave(dto, context, dto.id)
            TutorAttendance attendance = getEntityAndValidateExistence(context, dto.id)
            toCayenneModel(dto, attendance)
        }
        save(context)
    }
}
