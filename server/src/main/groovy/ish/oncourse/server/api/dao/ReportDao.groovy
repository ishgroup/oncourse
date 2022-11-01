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

import com.google.inject.Inject
import ish.common.types.AutomationStatus
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.v1.function.export.ExportFunctions
import ish.oncourse.server.cayenne.Report
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class ReportDao implements AutomationDao<Report> {

    @Inject
    CayenneService cayenneService

    static Expression withExp(String entityName) {
        String key = entityName.toLowerCase()
        Report.ENTITY.in(ExportFunctions.TRANSFORMATIONS_MAP[key])
    }

    @Override
    List<Report> getForEntity(String entityName, ObjectContext context) {
        (ObjectSelect.query(Report)
                .where(withExp(entityName)) & Report.IS_VISIBLE.isTrue() & Report.AUTOMATION_STATUS.eq(AutomationStatus.ENABLED))
                .orderBy(Report.NAME.asc())
                .select(cayenneService.newContext)
    }

    @Override
    Report getByKeyCode(ObjectContext context, String keyCode) {
        ObjectSelect.query(Report)
                .where(Report.KEY_CODE.eq(keyCode))
                .selectOne(cayenneService.newContext)
    }

    @Override
    Report newObject(ObjectContext context) {
        context.newObject(Report)
    }

    @Override
    Report getById(ObjectContext context, Long id) {
        SelectById.query(Report, id)
                .prefetch(Report.BACKGROUND.joint())
                .selectOne(context)
    }

    @Override
    List<Report> getByName(ObjectContext context, String name) {
        ObjectSelect.query(Report)
                .where(Report.KEY_CODE.eq(name))
                .select(cayenneService.newContext)
    }
}
