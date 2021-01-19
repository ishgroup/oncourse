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
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.document.DocumentService

import static ish.oncourse.server.api.function.CayenneFunctions.deleteRecord
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.function.EntityFunctions.validateEntityExistence
import static ish.oncourse.server.api.function.EntityFunctions.validateIdParam
import ish.oncourse.server.api.service.SiteApiService
import static ish.oncourse.server.api.v1.function.SiteFunctions.toDbSite
import static ish.oncourse.server.api.v1.function.SiteFunctions.toRestSite
import static ish.oncourse.server.api.v1.function.SiteFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.SiteFunctions.validateForSave
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.SiteDTO
import ish.oncourse.server.api.v1.service.SiteApi
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.ObjectContext

class SiteApiImpl implements SiteApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private DocumentService documentService

    @Inject
    private PreferenceController preferenceController

    @Inject
    private SystemUserService systemUserService

    @Inject
    private SiteApiService entityApiService

    @Override
    void remove(Long id) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext
        Site entity = getRecordById(context, Site, id)
        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForDelete(entity))

        deleteRecord(context, entity)
    }

    @Override
    SiteDTO get(Long id) {
        toRestSite(getRecordById(cayenneService.newContext, Site, id), preferenceController, documentService)
    }

    @Override
    void create(SiteDTO site) {
        ObjectContext context = cayenneService.newContext

        checkForBadRequest(validateForSave(site, context, false))

        Site newSite = context.newObject(Site)
        toDbSite(site, newSite, context, context.localObject(systemUserService.currentUser))

        context.commitChanges()
    }

    @Override
    void update(Long id, SiteDTO site) {
        checkForBadRequest(validateIdParam(id))

        ObjectContext context = cayenneService.newContext

        Site entity = getRecordById(context, Site, id)
        checkForBadRequest(validateEntityExistence(id, entity))
        checkForBadRequest(validateForSave(site, context, true, id))

        toDbSite(site, entity, context, context.localObject(systemUserService.currentUser))
        context.commitChanges()
    }

    @Override
    void bulkChange(DiffDTO diff) {
        entityApiService.bulkChange(diff)
    }
}
