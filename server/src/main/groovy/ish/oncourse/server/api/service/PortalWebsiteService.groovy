/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.common.types.DataType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactTrait
import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.server.cayenne.PortalWebsite
import org.apache.cayenne.query.ObjectSelect

class PortalWebsiteService {
    @Inject
    private ICayenneService cayenneService

    String getSubdomainFor(ContactTrait contact) {
        def domainCustomField = contact.customFields.find {(it as CustomField).customFieldType.dataType == DataType.PORTAL_SUBDOMAIN}
        if(domainCustomField && !domainCustomField.value.empty){
            return domainCustomField.value
        } else {
           return getPortalSubdomain()
        }
    }

    String getPortalSubdomain(){
        def portalWebsites = ObjectSelect.query(PortalWebsite).select(cayenneService.newReadonlyContext)
        if(portalWebsites.empty)
            throw new IllegalArgumentException("portal website not configured for your college. Contact your administrator")

       return portalWebsites.first().subDomain
    }
}
