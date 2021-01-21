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

import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.DocumentVersion
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class DocumentDao implements CayenneLayer<Document> {
    @Override
    Document newObject(ObjectContext context) {
        context.newObject(Document)
    }

    @Override
    Document getById(ObjectContext context, Long id) {
        SelectById.query(Document, id)
                .selectOne(context)
    }

    static Long getStoredDocumentsSize(ObjectContext context, Document document = null) {
        ObjectSelect select = ObjectSelect.query(DocumentVersion)
        if (document) {
            select = select.where(DocumentVersion.DOCUMENT.eq(document))
        }
        return select.sum(DocumentVersion.BYTE_SIZE).selectOne(context)
    }
}
