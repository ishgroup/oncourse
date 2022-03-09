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
import ish.oncourse.server.cayenne.Import
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class ImportDao implements AutomationDao<Import> {

    @Override
    Import newObject(ObjectContext context) {
        context.newObject(Import)
    }

    @Override
    Import getById(ObjectContext context, Long id) {
        SelectById.query(Import, id)
                .selectOne(context)
    }

    @Override
    Import getByKeyCode(ObjectContext context, String keyCode) {
        ObjectSelect.query(Import)
                .where(Import.KEY_CODE.eq(keyCode))
                .selectOne(context)
    }

    @Override
    List<Import> getForEntity(String entityName, ObjectContext context) {
        ObjectSelect.query(Import)
                .where(Import.ENTITY.eq(entityName))
                .and(Import.AUTOMATION_STATUS.eq(AutomationStatus.ENABLED))
                .orderBy(Import.NAME.asc())
                .select(context)
    }
}
