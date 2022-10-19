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

package ish.oncourse.server.api.dao

import ish.common.types.AutomationStatus
import ish.oncourse.server.api.v1.function.MessageFunctions
import ish.oncourse.server.cayenne.EmailTemplate
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class EmailTemplateDao implements AutomationDao<EmailTemplate> {

    @Override
    EmailTemplate newObject(ObjectContext context) {
        context.newObject(EmailTemplate)
    }

    @Override
    EmailTemplate getById(ObjectContext context, Long id) {
        SelectById.query(EmailTemplate, id)
                .selectOne(context)
    }

    @Override
    EmailTemplate getByKeyCode(ObjectContext context, String keyCode) {
        ObjectSelect.query(EmailTemplate)
                .where(EmailTemplate.KEY_CODE.eq(keyCode))
                .selectOne(context)
    }

    @Override
    List<EmailTemplate> getForEntity(String entityName, ObjectContext context) {
        List<String> avalibleTEntities = MessageFunctions.EMAIL_ENTITIES[entityName.toLowerCase()]
        ObjectSelect.query(EmailTemplate)
                .where(EmailTemplate.ENTITY.in(avalibleTEntities))
                .and(EmailTemplate.AUTOMATION_STATUS.eq(AutomationStatus.ENABLED))
                .orderBy(EmailTemplate.NAME.asc())
                .select(context)
    }
}
