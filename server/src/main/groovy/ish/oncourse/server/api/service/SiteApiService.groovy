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

package ish.oncourse.server.api.service

import ish.oncourse.server.api.dao.SiteDao
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import ish.oncourse.server.api.v1.model.SiteDTO
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.Tag
import org.apache.cayenne.ObjectContext

class SiteApiService extends TaggableApiService<SiteDTO, Site, SiteDao> {
    @Override
    Class<Site> getPersistentClass() {
        return Site
    }

    @Override
    SiteDTO toRestModel(Site cayenneModel) {
        return null
    }

    @Override
    Site toCayenneModel(SiteDTO dto, Site cayenneModel) {
        return null
    }

    @Override
    void validateModelBeforeSave(SiteDTO dto, ObjectContext context, Long id) {

    }

    @Override
    void validateModelBeforeRemove(Site cayenneModel) {

    }

    @Override
    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
