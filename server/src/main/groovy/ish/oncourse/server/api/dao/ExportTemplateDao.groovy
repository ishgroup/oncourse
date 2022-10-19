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
import ish.oncourse.server.api.v1.function.export.ExportFunctions
import ish.oncourse.server.cayenne.ExportTemplate
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class ExportTemplateDao implements AutomationDao<ExportTemplate> {

    @Override
    ExportTemplate newObject(ObjectContext context) {
        context.newObject(ExportTemplate)
    }

    @Override
    ExportTemplate getById(ObjectContext context, Long id) {
        SelectById.query(ExportTemplate, id)
                .selectOne(context)
    }

    @Override
    ExportTemplate getByKeyCode(ObjectContext context, String keyCode) {
        ObjectSelect.query(ExportTemplate)
                .where(ExportTemplate.KEY_CODE.eq(keyCode))
                .selectOne(context)
    }

    List<ExportTemplate> getForEntity(String entityName, ObjectContext context) {

        List<String> avalibleTEntities = ExportFunctions.TRANSFORMATIONS_MAP[entityName.toLowerCase()]
        ObjectSelect.query(ExportTemplate)
                .where(ExportTemplate.ENTITY.in(avalibleTEntities))
                .and(ExportTemplate.AUTOMATION_STATUS.eq(AutomationStatus.ENABLED))
                .orderBy(ExportTemplate.NAME.asc())
                .select(context)
    }

}
