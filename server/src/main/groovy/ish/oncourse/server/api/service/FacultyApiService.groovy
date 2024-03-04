/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.api.dao.FacultyDao
import ish.oncourse.server.api.v1.function.TagFunctions
import ish.oncourse.server.api.v1.model.FacultyDTO
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.document.DocumentService
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext

import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.trimToEmpty
import static org.apache.commons.lang3.StringUtils.trimToNull
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments

class FacultyApiService extends TaggableApiService<FacultyDTO, Faculty, FacultyDao> {

    @Inject
    private DocumentService documentService


    @Override
    Class<Faculty> getPersistentClass() {
        return Faculty
    }

    @Override
    FacultyDTO toRestModel(Faculty faculty) {
        new FacultyDTO().with { facultyDTO ->
            facultyDTO.id = faculty.id
            facultyDTO.name = faculty.name
            facultyDTO.code = faculty.code
            facultyDTO.webDescription = faculty.webDescription
            facultyDTO.shortWebDescription = faculty.shortWebDescription
            facultyDTO.isShownOnWeb = faculty.isShownOnWeb
            facultyDTO.createdOn = LocalDateUtils.dateToTimeValue(faculty.createdOn)
            facultyDTO.modifiedOn = LocalDateUtils.dateToTimeValue(faculty.modifiedOn)
            facultyDTO.tags = faculty.allTags.collect { it.id }
            facultyDTO.documents = faculty.activeAttachments.collect { toRestDocument(it.document, documentService) }
            facultyDTO
        }
    }

    @Override
    Faculty toCayenneModel(FacultyDTO facultyDTO, Faculty faculty) {

        faculty.name = trimToNull(facultyDTO.name)
        faculty.code = trimToNull(facultyDTO.code)
        faculty.webDescription = trimToNull(facultyDTO.webDescription)
        faculty.shortWebDescription = trimToNull(facultyDTO.shortWebDescription)
        faculty.isShownOnWeb = facultyDTO.isShownOnWeb

        updateTags(faculty, faculty.taggingRelations, facultyDTO.tags, FacultyTagRelation, faculty.context)
        updateDocuments(faculty, faculty.attachmentRelations, facultyDTO.documents, FacultyAttachmentRelation, faculty.context)

        return faculty
    }

    @Override
    void validateModelBeforeSave(FacultyDTO facultyDTO, ObjectContext context, Long id) {

        if (isBlank(FacultyDTO.name)) {
            validator.throwClientErrorException(id, 'name', 'Name is required.')
        }

        if(FacultyDTO.name.length() > Faculty.FACULTY_NAME_MAX_LENGTH) {
            validator.throwClientErrorException(id, 'name',
                    "Faculty name cannot be greater than " + Faculty.FACULTY_NAME_MAX_LENGTH + " characters.")
        }

        if (isBlank(facultyDTO.code)) {
            validator.throwClientErrorException(id, 'code', 'Code is required.')
        } else if (!trimToNull(facultyDTO.code).matches('^\\w+(\\.\\w+)*$')) {
            validator.throwClientErrorException(id, 'code', 'Faculty code must start and end with letters or numbers and can contain full stops.')
        } else if (trimToNull(facultyDTO.code).length() > Faculty.FACULTY_CODE_MAX_LENGTH) {
            validator.throwClientErrorException(id, 'code', "Faculty code cannot be greater than " +  Faculty.FACULTY_CODE_MAX_LENGTH + "characters.")
        } else {
            Long facultyId = entityDao.getFacultyByCode(context, trimToNull(facultyDTO.code))?.id
            if (facultyId && facultyId != id) {
                validator.throwClientErrorException(id, 'code', 'Code must be unique.')
            }
        }

        if (facultyDTO.isShownOnWeb == null) {
            validator.throwClientErrorException(id, 'isShownOnWeb', 'Visibility is required.')
        }

        if (trimToEmpty(facultyDTO.webDescription).length() > 32000) {
            validator.throwClientErrorException(id, 'webDescription', 'Web description must be shorter then 32000 characters.')
        }

        if (trimToEmpty(facultyDTO.shortWebDescription).length() > 32000) {
            validator.throwClientErrorException(id, 'shortWebDescription', 'Short web description must be shorter then 32000 characters.')
        }

        TagFunctions.validateTagForSave(Faculty, context, facultyDTO.tags)
                ?.with { validator.throwClientErrorException(it) }

        TagFunctions.validateRelationsForSave(Faculty, context, facultyDTO.tags, TaggableClasses.FACULTY)
                ?.with { validator.throwClientErrorException(it) }
    }

    @Override
    void validateModelBeforeRemove(Faculty faculty) {
        if (!faculty.courses.empty) {
            validator.throwClientErrorException(faculty.id, 'id', "Cannot delete faculty assigned to courses.")
        }
    }

    @Override
    Closure getAction(String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            switch (key) {
                case Faculty.IS_SHOWN_ON_WEB.name:
                    validator.validateBoolean(value, key)
                    action = { Faculty f ->
                        f.isShownOnWeb = Boolean.valueOf(value)
                    }
                    break
                default:
                    validator.throwClientErrorException(key, "Unsupported attribute")
            }
        }
        return action
    }
}
