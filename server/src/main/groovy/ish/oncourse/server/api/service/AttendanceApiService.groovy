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

import ish.oncourse.server.api.dao.AttendanceDao
import ish.oncourse.server.api.v1.model.AttendanceTypeDTO
import ish.oncourse.server.api.v1.model.StudentAttendanceDTO
import ish.oncourse.server.cayenne.Attendance
import ish.util.DurationFormatter
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext


class AttendanceApiService  extends EntityApiService<StudentAttendanceDTO, Attendance, AttendanceDao> {

    @Override
    Class<Attendance> getPersistentClass() {
        return Attendance
    }

    @Override
    StudentAttendanceDTO toRestModel(Attendance cayenneModel) {
        StudentAttendanceDTO dto = new StudentAttendanceDTO()
        dto.id = cayenneModel.id
        dto.contactId = cayenneModel.student.contact.id
        dto.contactName = cayenneModel.student.contact.getFullName()
        dto.sessionId = cayenneModel.session.id
        dto.attendanceType = AttendanceTypeDTO.values()[0].fromDbType(cayenneModel.attendanceType)
        dto.note = cayenneModel.note
        dto.attendedFrom = LocalDateUtils.dateToTimeValue(cayenneModel.attendedFrom)
        dto.attendedUntil= LocalDateUtils.dateToTimeValue(cayenneModel.attendedUntil)
        dto

    }

    void updateList(List<StudentAttendanceDTO> attendances) {
        ObjectContext context = cayenneService.newContext
        attendances.each { dto ->
            validateModelBeforeSave(dto, context, dto.id)
            Attendance attendance = getEntityAndValidateExistence(context, dto.id)
            toCayenneModel(dto, attendance)
        }
        save(context)
    }

    @Override
    Attendance toCayenneModel(StudentAttendanceDTO dto, Attendance cayenneModel) {
        cayenneModel.attendanceType = dto.attendanceType.dbType
        cayenneModel.note = dto.note
        cayenneModel.attendedFrom = LocalDateUtils.timeValueToDate(dto.attendedFrom)
        cayenneModel.attendedUntil = LocalDateUtils.timeValueToDate(dto.attendedUntil)

        if (AttendanceTypeDTO.PARTIAL == dto.attendanceType && dto.attendedFrom  && dto.attendedUntil) {
            cayenneModel.durationMinutes = DurationFormatter.parseDurationInMinutes(
                    LocalDateUtils.timeValueToDate(dto.attendedFrom), LocalDateUtils.timeValueToDate(dto.attendedUntil))
        } else {
            cayenneModel.durationMinutes = null
        }
        cayenneModel
    }

    @Override
    void validateModelBeforeSave(StudentAttendanceDTO dto, ObjectContext context, Long id) {

        if (id == null) {
            validator.throwClientErrorException(id, 'id', "Attendance id should be specified")
        }

        Attendance attendance = getEntityAndValidateExistence(context, id)

        if (dto.attendanceType == null) {
            validator.throwClientErrorException(id, 'attendanceType', "Attendance type is required")
        }

        switch (dto.attendanceType) {
            case AttendanceTypeDTO.UNMARKED:
                break
            case AttendanceTypeDTO.ATTENDED:
                break
            case AttendanceTypeDTO.ABSENT_WITH_REASON:
                if (dto.note == null) {
                    validator.throwClientErrorException(id, 'note', "You must enter an explanation for a student marked as 'Absent with reason.'")
                }
                break
            case AttendanceTypeDTO.ABSENT_WITHOUT_REASON:
                break
            case AttendanceTypeDTO.PARTIAL:


                if (dto.attendedFrom == null) {
                    validator.throwClientErrorException(id, 'attendedFrom', "Attended from date must be entered for a student marked as 'Partial attendance.'")
                }
                if (dto.attendedUntil == null) {
                    validator.throwClientErrorException(id, 'attendedUntil', "Attended until date must be entered for a student marked as 'Partial attendance.'")
                }
                Date attendedFrom = LocalDateUtils.timeValueToDate(dto.attendedFrom)
                Date attendedUntil = LocalDateUtils.timeValueToDate(dto.attendedUntil)

                if (attendedFrom < attendance.session.startDatetime || attendedFrom > attendance.session.endDatetime) {
                    validator.throwClientErrorException(id, 'attendedFrom', "Attended from date must be in range of session interval.")
                }
                if (attendedUntil < attendance.session.startDatetime || attendedUntil > attendance.session.endDatetime) {
                    validator.throwClientErrorException(id, 'attendedFrom', "Attended until date must be in range of session interval.")
                }

                if (attendedFrom == attendedUntil) {
                    validator.throwClientErrorException(id, 'attendedFrom', "Attended until date is equal attended from date. Duration should be different from zero.")
                }

                if (attendedFrom > attendedUntil) {
                    validator.throwClientErrorException(id, 'attendedFrom', "Attended until date is less than from date")
                }
        }

    }

    @Override
    void validateModelBeforeRemove(Attendance cayenneModel) {
        throw new UnsupportedOperationException()
    }

    @Override
    List<StudentAttendanceDTO> getList(Long classId) {
        entityDao.getByClassId(cayenneService.newContext, classId).collect {toRestModel(it)}
    }
}
