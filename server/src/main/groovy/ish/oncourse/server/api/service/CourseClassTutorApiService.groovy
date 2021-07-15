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
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.CourseClassDao
import ish.oncourse.server.api.dao.CourseClassTutorDao
import ish.oncourse.server.api.dao.DefinedTutorRoleDao
import static ish.oncourse.server.api.servlet.ApiFilter.validateOnly
import ish.oncourse.server.api.v1.model.CourseClassTutorDTO
import ish.oncourse.server.cayenne.AssessmentClassTutor
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.DefinedTutorRole
import ish.oncourse.server.cayenne.Student
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext

class CourseClassTutorApiService extends EntityApiService<CourseClassTutorDTO, CourseClassTutor, CourseClassTutorDao>{

    @Inject
    private CourseClassDao courseClassDao
    @Inject
    private DefinedTutorRoleDao definedTutorRoleDao
    @Inject
    private ContactDao contactDao

    @Override
    Class<CourseClassTutor> getPersistentClass() {
        return CourseClassTutor
    }

    @Override
    CourseClassTutorDTO toRestModel(CourseClassTutor cayenneModel) {
        return new CourseClassTutorDTO().with { dto ->
            dto.id = cayenneModel.id
            dto.roleId = cayenneModel.definedTutorRole.id
            dto.roleName = cayenneModel.definedTutorRole.name
            dto.confirmedOn = LocalDateUtils.dateToValue(cayenneModel.confirmedOn)
            dto.isInPublicity = cayenneModel.inPublicity
            dto.contactId = cayenneModel.tutor.contact.id
            dto.tutorName = cayenneModel.tutor.contact.getFullName()
            dto.classId = cayenneModel.courseClass.id
            dto
        }
    }

    @Override
    CourseClassTutor toCayenneModel(CourseClassTutorDTO dto, CourseClassTutor cayenneModel) {
        ObjectContext context = cayenneModel.context
        cayenneModel.definedTutorRole = definedTutorRoleDao.getById(context, dto.roleId)
        cayenneModel.inPublicity = dto.isInPublicity
        cayenneModel.confirmedOn = LocalDateUtils.valueToDate(dto.confirmedOn)
        cayenneModel.tutor = contactDao.getById(context, dto.contactId).tutor
        if (dto.classId) {
            cayenneModel.courseClass = courseClassDao.getById(context, dto.classId)
        }
        cayenneModel
    }

    @Override
    void validateModelBeforeSave(CourseClassTutorDTO dto, ObjectContext context, Long id) {

        if (dto.roleId == null) {
            validator.throwClientErrorException('roleId', 'Defined tutor role is mandatory')
        }
        DefinedTutorRole role = definedTutorRoleDao.getById(context, dto.roleId)

        if (!role || !role.active) {
            validator.throwClientErrorException('roleId', 'Defined tutor role is wrong')
        }

        if (dto.contactId == null) {
            validator.throwClientErrorException('contactId', 'Tutor is mandatory')
        }

        Contact contact = contactDao.getById(context, dto.contactId)

        if (!contact || !contact.tutor) {
            validator.throwClientErrorException('contactId', 'Tutor is wrong')
        }

        CourseClass courseClass = null
        boolean newClass = validateOnly.get() && dto.classId == null

        if (!newClass) {
            if (dto.classId == null) {
                validator.throwClientErrorException('classId', 'Class is mandatory')
            }

            courseClass = courseClassDao.getById(context, dto.classId)
            if (!courseClass || courseClass.isCancelled) {
                validator.throwClientErrorException('classId', 'Class is wrong')
            }
        }

        if (id != null) {
            CourseClassTutor classTutor = entityDao.getById(context, id)
            if (classTutor != null) {
                if (classTutor.definedTutorRole != role) {
                    validator.throwClientErrorException('roleId', 'Defined tutor role can not be changed')
                } else if (classTutor.tutor != contact.tutor) {
                    validator.throwClientErrorException('contactId', 'Tutor can not be changed')
                } else if (classTutor.courseClass != courseClass) {
                    validator.throwClientErrorException('classId', 'Class can not be changed')
                }
            } else {
                validator.throwClientErrorException(id, 'id', "Tutor role with id: $id does not exist")
            }
        } else if (!newClass) {
            if (courseClass.tutorRoles.find {it.tutor.contact == contact && it.definedTutorRole == role}) {
                validator.throwClientErrorException('roleId', "Tutor arleady assigned to class as: $role.name")
            }
        }

    }

    @Override
    void validateModelBeforeRemove(CourseClassTutor cayenneModel) {
        if (cayenneModel.classCosts.find {!it.paylines.empty}) {
            validator.throwClientErrorException("id", "Tutor has payslips and can not be deleted from this class")
        }
    }

    @Override
    List<CourseClassTutorDTO> getList(Long classId) {
        ObjectContext context = cayenneService.newContext
        CourseClass courseClass = courseClassDao.getById(context, classId)
        if (courseClass == null) {
            validator.throwClientErrorException(classId, 'classId', "Class with id = '$classId' doesn't exist.")
        }
        courseClass.tutorRoles.collect{ toRestModel(it) }
    }

    @Override
    void remove (CourseClassTutor persistent, ObjectContext context) {
        persistent.courseClass.modifiedOn = new Date()
        List<AssessmentClassTutor> tutorAssessments = persistent.courseClass.assessmentClasses*.assessmentClassTutors.flatten() as List<AssessmentClassTutor>
        context.deleteObjects(tutorAssessments.findAll{it.tutor.equalsIgnoreContext(persistent.tutor) })
        context.deleteObjects(persistent.sessionsTutors)
        context.deleteObjects(persistent.classCosts)
        context.deleteObject(persistent)
    }
}
