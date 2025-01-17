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
import ish.oncourse.server.cayenne.Script
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ColumnSelect
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class ScriptDao implements AutomationDao<Script> {

    @Override
    Script newObject(ObjectContext context) {
        context.newObject(Script)
    }

    @Override
    Script getById(ObjectContext context, Long id) {
        SelectById.query(Script, id)
                .selectOne(context)
    }

    @Override
    Script getByKeyCode(ObjectContext context, String keyCode) {
        ObjectSelect.query(Script)
                .where(Script.KEY_CODE.eq(keyCode))
                .selectOne(context)
    }

    @Override
    List<Script> getForEntity(String entityName, ObjectContext context) {
        ObjectSelect.query(Script)
                .where(Script.ENTITY_CLASS.eq(entityName))
                .and(Script.AUTOMATION_STATUS.eq(AutomationStatus.ENABLED))
                .orderBy(Script.NAME.asc())
                .select(context)
    }

    @Override
    List<Script> getByName(ObjectContext context, String name) {
        ObjectSelect.query(Script)
                .where(Script.NAME.eq(name))
                .select(context)
    }

    boolean hasTheSameName(ObjectContext context, String name, Long id) {
        ColumnSelect select = ObjectSelect
                .columnQuery(Script, Property.COUNT)
                .where(Script.NAME.eq(name))

        if (id != null) {
            select & Script.ID.ne(id)
        }

        select.selectOne(context) > 0
    }
}
