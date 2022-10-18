/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.configs

import ish.oncourse.server.cayenne.EmailTemplate

class MessageModel extends AutomationModel{
    private String entityClass
    private String type
    private String subject

    MessageModel(EmailTemplate emailTemplate) {
        super(emailTemplate)

        this.entityClass = emailTemplate.entity
        this.type = emailTemplate.type?.name()
        this.subject = emailTemplate.subject
    }

    String getEntityClass() {
        return entityClass
    }

    String getType() {
        return type
    }

    String getSubject() {
        return subject
    }
}
