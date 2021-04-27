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
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.api.dao.AssessmentDao
import ish.oncourse.server.cayenne.GradingType
import ish.oncourse.server.document.DocumentService
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import ish.oncourse.server.api.v1.function.TagFunctions
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import ish.oncourse.server.api.v1.model.AssessmentDTO
import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.server.cayenne.AssessmentAttachmentRelation
import ish.oncourse.server.cayenne.AssessmentTagRelation
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.users.SystemUserService
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.trimToEmpty
import static org.apache.commons.lang3.StringUtils.trimToNull

class AssessmentApiService extends TaggableApiService<AssessmentDTO, Assessment, AssessmentDao> {

    @Inject
    private DocumentService documentService

    @Inject
    private GradingApiService gradingApiService

    @Inject
    private SystemUserService systemUserService

    @Override
    Class<Assessment> getPersistentClass() {
        return Assessment
    }

    @Override
    AssessmentDTO toRestModel(Assessment cayenneModel) {
        new AssessmentDTO().with { assessment ->
            assessment.id = cayenneModel.id
            assessment.code = cayenneModel.code
            assessment.name = cayenneModel.name
            assessment.tags = cayenneModel.tags.collect { toRestTagMinimized(it) }
            assessment.active = cayenneModel.active
            assessment.description = cayenneModel.description
            assessment.gradingTypeId = cayenneModel.gradingType?.id
            assessment.documents = cayenneModel.activeAttachments.collect { toRestDocument(it.document, it.documentVersion?.id, documentService) }
            assessment.createdOn = LocalDateUtils.dateToTimeValue(cayenneModel.createdOn)
            assessment.modifiedOn = LocalDateUtils.dateToTimeValue(cayenneModel.modifiedOn)
            assessment
        }
    }

    @Override
    Assessment toCayenneModel(AssessmentDTO restModel, Assessment cayenneModel) {
        cayenneModel.code = trimToNull(restModel.code)
        cayenneModel.name = trimToNull(restModel.name)
        cayenneModel.active = restModel.active
        cayenneModel.description = trimToNull(restModel.description)
        cayenneModel.gradingType = restModel.gradingTypeId ?
                gradingApiService.getEntityAndValidateExistence(cayenneModel.context, restModel.gradingTypeId) :
                null as GradingType

        updateTags(cayenneModel, cayenneModel.taggingRelations, restModel.tags*.id, AssessmentTagRelation, cayenneModel.context)
        updateDocuments(cayenneModel, cayenneModel.attachmentRelations, restModel.documents, AssessmentAttachmentRelation, cayenneModel.context)

        cayenneModel
    }

    @Override
    void validateModelBeforeSave(AssessmentDTO restModel, ObjectContext context, Long id) {
        if (isBlank(restModel.code)) {
            validator.throwClientErrorException(id, 'code', 'Code is required.')
        } else if (trimToEmpty(restModel.code).size() > 64) {
            validator.throwClientErrorException(id, 'code', 'Code cannot be more than 64 chars.')
        } else {
            Long assessmentId = entityDao.getAssessmentByProperty(context,
                    Assessment.CODE,
                    trimToNull(restModel.code))?.id
            if(assessmentId && assessmentId != id) {
                validator.throwClientErrorException(id, 'code', 'Code must be unique.')
            }
        }

        if (isBlank(restModel.name)) {
            validator.throwClientErrorException(id, 'name', 'Name is required.')
        } else if (trimToEmpty(restModel.name).size() > 200) {
            validator.throwClientErrorException(id, 'name', 'Name cannot be more than 200 chars.')
        } else {
            Long assessmentId = entityDao.getAssessmentByProperty(context,
                    Assessment.NAME,
                    trimToNull(restModel.name))?.id
            if(assessmentId && assessmentId != id) {
                validator.throwClientErrorException(id, 'name', 'Name must be unique.')
            }
        }

        if (isBlank(restModel.description)) {
            validator.throwClientErrorException(id, 'description', 'Description is required.')
        } else if (trimToEmpty(restModel.description).size() > 32000) {
            validator.throwClientErrorException(id, 'description', 'Description cannot be more than 32000 chars.')
        }
        if (restModel.active == null) {
            validator.throwClientErrorException(id, 'active', 'Active flag is required.')
        }

        TagFunctions.validateTagForSave(Assessment, context, restModel.tags*.id)
                ?.with { validator.throwClientErrorException(it) }

        for(Long documentId : restModel.documents*.id) {
            Document document = entityDao.getDocumentById(context, documentId)
            if(document == null) {
                validator.throwClientErrorException(id, 'documents',
                        "Document with id = " + documentId + " doesn\'t exist.")
            }
        }

        TagFunctions.validateRelationsForSave(Assessment, context, restModel.tags*.id, TaggableClasses.ASSESSMENT)
                ?.with { validator.throwClientErrorException(it) }
    }

    @Override
    void validateModelBeforeRemove(Assessment cayenneModel) {
        if (!cayenneModel.assessmentClasses.empty) {
            validator.throwClientErrorException(cayenneModel.id,
                    'assessmentClasses', 'Cannot delete assessment attached to classes.')
        }
    }

    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
