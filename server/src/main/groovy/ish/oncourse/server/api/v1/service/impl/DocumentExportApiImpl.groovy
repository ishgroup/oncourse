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

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.function.EntityFunctions.validateIdParam
import ish.oncourse.server.api.v1.service.DocumentExportApi
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.DocumentVersion

class DocumentExportApiImpl implements DocumentExportApi {

    @Inject
    private ICayenneService cayenneService

    @Override
    byte[] get(Long documentId, Long versionId) {
        checkForBadRequest(validateIdParam(documentId))

        Document document = getRecordById(cayenneService.newContext, Document, documentId, Document.VERSIONS.joint())

        DocumentVersion version = versionId ? document.versions.find { it.id == versionId } : document.currentVersion

        return version.attachmentData.content
    }
}
