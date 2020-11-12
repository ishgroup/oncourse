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

import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.server.cayenne.Document
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class AssessmentDao implements CayenneLayer<Assessment> {

    @Override
    Assessment newObject(ObjectContext context) {
        context.newObject(Assessment)
    }

    @Override
    Assessment getById(ObjectContext context, Long id) {
        SelectById.query(Assessment, id)
                .selectOne(context)
    }

    Assessment getAssessmentByProperty(ObjectContext context, Property<?> property, String propValue) {
        ObjectSelect.query(Assessment)
                .where(property.eq(propValue))
                .selectOne(context)
    }

    Document getDocumentById(ObjectContext context, Long id) {
        SelectById.query(Document, id).selectOne(context)
    }
}
