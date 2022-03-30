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

import ish.oncourse.server.api.dao.EmailTemplateDao
import ish.oncourse.server.api.v1.model.EmailTemplateDTO
import ish.oncourse.server.api.v1.model.MessageTypeDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.EmailTemplate
import org.apache.cayenne.ObjectContext

import static org.apache.commons.lang3.StringUtils.trimToNull
import static org.apache.http.util.TextUtils.isBlank

class EmailTemplateApiService extends AutomationApiService<EmailTemplateDTO, EmailTemplate, EmailTemplateDao> {

    public static final String SUBJECT_PREFIX = 'subject'


    @Override
    Class<EmailTemplate> getPersistentClass() {
        return EmailTemplate
    }

    @Override
    protected EmailTemplateDTO createDto() {
        return new EmailTemplateDTO()
    }

    @Override
    EmailTemplateDTO toRestModel(EmailTemplate emailTemplate) {
        EmailTemplateDTO dto = super.toRestModel(emailTemplate)
        dto.setSubject(emailTemplate.subject)
        dto.setPlainBody(emailTemplate.bodyPlain)
        dto.setType(MessageTypeDTO.values()[0].fromDbType(emailTemplate.type))
        return dto
    }

    @Override
    EmailTemplate toCayenneModel(EmailTemplateDTO emailTemplateDTO, EmailTemplate emailTemplate) {
        emailTemplate = super.toCayenneModel(emailTemplateDTO, emailTemplate) as EmailTemplate
        emailTemplate.subject = emailTemplateDTO.subject
        emailTemplate.bodyPlain = emailTemplateDTO.plainBody
        emailTemplate.type = emailTemplateDTO.type.dbType

        return emailTemplate
    }

    @Override
    void validateModelBeforeSave(EmailTemplateDTO emailTemplateDTO, ObjectContext context, Long id) {
        super.validateModelBeforeSave(emailTemplateDTO, context, id, false, false)
        if (emailTemplateDTO.type == MessageTypeDTO.EMAIL) {
            if (isBlank(emailTemplateDTO.body)) {
                EntityValidator.throwClientErrorException(id, "body", "Body is required.");
            }
        } else {
            if (isBlank(emailTemplateDTO.plainBody)) {
                EntityValidator.throwClientErrorException(id, "plainBody", "Body is required.");
            }
        }
        if (emailTemplateDTO.entity) {
            if (context.entityResolver.getObjEntity(trimToNull(emailTemplateDTO.entity)) == null) {
                validator.throwClientErrorException(id, 'entity', 'Incorrect entity name')
            }
        }
    }

    @Override
    void validateModelBeforeRemove(EmailTemplate emailTemplate) {
        super.validateModelBeforeRemove(emailTemplate);
    }

    @Override
    public EmailTemplate updateInternal(EmailTemplateDTO dto) {
        EmailTemplate exportTemplate = super.updateInternal(dto) as EmailTemplate
        exportTemplate.getContext().commitChanges()
        return exportTemplate
    }

}
