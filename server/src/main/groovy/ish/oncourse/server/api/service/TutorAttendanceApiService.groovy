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
import ish.oncourse.server.api.dao.TutorAttendanceDao
import ish.oncourse.server.api.v1.model.TutorAttendanceDTO
import ish.oncourse.server.api.v1.model.TutorAttendanceTypeDTO
import ish.oncourse.server.cayenne.TutorAttendance
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.ObjectContext

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
        dto.sessionId = cayenneModel.session.id
        dto.courseClassTutorId = cayenneModel.courseClassTutor.id
        dto.contactName = cayenneModel.courseClassTutor.tutor.contact.fullName
        dto.attendanceType = TutorAttendanceTypeDTO.values()[0].fromDbType(cayenneModel.attendanceType)
        dto.note = cayenneModel.note
        dto.durationMinutes = cayenneModel.durationMinutes
        dto.hasPayslip = cayenneModel.hasPayslips()
        dto
    }

    @Override
    TutorAttendance toCayenneModel(TutorAttendanceDTO dto, TutorAttendance cayenneModel) {
        cayenneModel.attendanceType = dto.attendanceType.dbType
        cayenneModel.note = dto.note
        cayenneModel.durationMinutes = dto.durationMinutes
        cayenneModel.markedByUser = cayenneModel.context.localObject(userService.currentUser)
        cayenneModel
    }

    @Override
    void validateModelBeforeSave(TutorAttendanceDTO dto, ObjectContext context, Long id) {
        if (id == null) {
            validator.throwClientErrorException(id, 'id', "Attendance id should be specified")
        }

        if (dto.attendanceType == null) {
            validator.throwClientErrorException(id, 'attendanceType', "Attendance type is required")
        }
        TutorAttendance attendance = getEntityAndValidateExistence(context, dto.id)

        if (attendance.hasPayslips() && (attendance.attendanceType != dto.attendanceType.dbType)) {
            validator.throwClientErrorException(id, 'attendanceType', "Attendance with linked payslip cannot be changed")
        }

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
        ObjectContext context = cayenneService.newContext
        attendanceDTOList.each { dto ->
            validateModelBeforeSave(dto, context, dto.id)
            TutorAttendance attendance = getEntityAndValidateExistence(context, dto.id)
            toCayenneModel(dto, attendance)
        }
        save(context)
    }
}
