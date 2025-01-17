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

package ish.oncourse.server.api.v1.function


import ish.oncourse.server.api.v1.model.SiteDTO
import ish.oncourse.server.cayenne.Site


class SiteFunctions {

    static SiteDTO toRestSiteMinimized(Site dbSite) {
        new SiteDTO().with { site ->
            site.id = dbSite.id
            site.name = dbSite.name
            site.suburb = dbSite.suburb
            site.postcode = dbSite.postcode
            site
        }
    }
}
