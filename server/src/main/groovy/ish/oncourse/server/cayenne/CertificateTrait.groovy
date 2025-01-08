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

package ish.oncourse.server.cayenne

import javax.inject.Inject
import ish.oncourse.API
import ish.oncourse.server.api.service.PortalWebsiteService
import ish.util.UrlUtil

trait CertificateTrait {

    @Inject
    private PortalWebsiteService portalWebsiteService
    
    abstract String getUniqueCode()

    /**
     * A certificate can be issued for outcomes toward a certain qualification even if the qualification
     * itself is not issued. Check getIsQualification() to see whether this was a full qualification.
     *
     * @return the qualification
     */
    @API
    String getPortalUrl() {
        return UrlUtil.buildCertificatePortalUrl(uniqueCode, portalWebsiteService.getPortalSubdomain())
    }
}
