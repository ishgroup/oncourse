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
import ish.oncourse.server.api.dao.ModuleDao
import ish.oncourse.server.api.dao.OutcomeDao
import ish.oncourse.server.api.dao.PriorLearningDao
import ish.oncourse.server.api.dao.QualificationDao
import ish.oncourse.server.api.function.CayenneFunctions
import ish.oncourse.server.api.v1.function.DocumentFunctions
import ish.oncourse.server.document.DocumentService

import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import ish.oncourse.server.api.v1.function.OutcomeFunctions
import static ish.oncourse.server.api.v1.function.PriorLearningFunctions.updateOutcomes
import ish.oncourse.server.api.v1.model.PriorLearningDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.PriorLearning
import ish.oncourse.server.cayenne.PriorLearningAttachmentRelation
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

class PriorLearningApiService extends EntityApiService<PriorLearningDTO, PriorLearning, PriorLearningDao> {

    @Inject
    private DocumentService documentService

    @Inject
    private QualificationDao qualificationDao

    @Inject
    private OutcomeDao outcomeDao

    @Inject
    private ModuleDao moduleDao

    @Inject
    private OutcomeApiService outcomeApiService

    @Override
    Class<PriorLearning> getPersistentClass() {
        return PriorLearning
    }

    @Override
    PriorLearningDTO toRestModel(PriorLearning cayenneModel) {
        new PriorLearningDTO().with {dto ->
            dto.id = cayenneModel.id
            dto.createdOn = LocalDateUtils.dateToTimeValue(cayenneModel.createdOn)
            dto.modifiedOn = LocalDateUtils.dateToTimeValue(cayenneModel.modifiedOn)
            dto.title = cayenneModel.title
            dto.externalReference = cayenneModel.externalRef
            dto.qualificationId = cayenneModel.qualification?.id
            dto.qualificationNationalCode = cayenneModel.qualification?.nationalCode
            dto.qualificationLevel = cayenneModel.qualification?.level
            dto.qualificationName = cayenneModel.qualification?.level ? "${cayenneModel.qualification?.level} ${cayenneModel.qualification?.title}" : cayenneModel.qualification?.title
            dto.outcomes = cayenneModel.outcomes?.collect{o -> OutcomeFunctions.toRestOutcome(o)}
            dto.documents = cayenneModel.documents?.collect{d -> DocumentFunctions.toRestDocumentMinimized(d, d.currentVersion.id, documentService)}
            dto.notes = cayenneModel.notes
            dto.contactId = cayenneModel.student?.contact?.id
            dto.contactName = cayenneModel.student?.contact?.fullName
            dto.outcomeIdTrainingOrg = cayenneModel.outcomeIdTrainingOrg
            dto
        }
    }

    @Override
    PriorLearning toCayenneModel(PriorLearningDTO dto, PriorLearning cayenneModel) {
        cayenneModel.title = dto.title
        cayenneModel.externalRef = dto.externalReference
        cayenneModel.outcomeIdTrainingOrg = dto.outcomeIdTrainingOrg
        if (dto.qualificationId != null) {
            cayenneModel.qualification = qualificationDao.getById(cayenneModel.context, dto.qualificationId)
        }

        updateOutcomes(outcomeDao, moduleDao, cayenneModel, dto.outcomes)
        updateDocuments(cayenneModel, cayenneModel.attachmentRelations, dto.documents, PriorLearningAttachmentRelation, cayenneModel.context)
        cayenneModel.notes = dto.notes
        cayenneModel.student = CayenneFunctions.getRecordById(cayenneModel.context, Contact, dto.contactId)?.student
        cayenneModel
    }

    @Override
    void validateModelBeforeSave(PriorLearningDTO dto, ObjectContext context, Long id) {

        if (dto.externalReference != null && dto.externalReference.length() > 12) {
            validator.throwClientErrorException('externalReference', "The maximum width for this field is ${12}.".toString())
        }
        if (dto.notes != null && dto.notes.length() > 32000) {
            validator.throwClientErrorException('notes', "The maximum width for notes is ${32000}.".toString())
        }
        if (dto.outcomeIdTrainingOrg != null && dto.outcomeIdTrainingOrg.length() > 12) {
            validator.throwClientErrorException('outcomeIdTrainingOrg', "The maximum width for training organisation is ${12}.".toString())
        }
        if (StringUtils.isBlank(dto.title)) {
            validator.throwClientErrorException('title', 'You should provide a title for this prior learning.')
        }
        if (dto.title != null && dto.title.length() > 200) {
            validator.throwClientErrorException('title', "The maximum width for title is ${200}.".toString())
        }

        if (dto.contactId == null) {
            validator.throwClientErrorException('contactId', "You should add a student.")
        } else {
            Contact contact = CayenneFunctions.getRecordById(context, Contact, dto.contactId)
            if (contact.student == null) {
                validator.throwClientErrorException('contactId', "Contact id:$id is not a student")
            }
        }

        if (dto.outcomes) {
            dto.outcomes.each { o ->
                o.isPriorLearning = true
                outcomeApiService.validateModelBeforeSave(o, context, o.id)
            }
        }
    }

    @Override
    void validateModelBeforeRemove(PriorLearning cayenneModel) {
        if (cayenneModel.outcomes) {
            cayenneModel.outcomes.each { o ->
                outcomeApiService.validateModelBeforeRemove(o)
            }
        }
    }
}
