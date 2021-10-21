/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.service.impl

import groovy.transform.CompileStatic
import ish.oncourse.configuration.Configuration
import ish.oncourse.willow.billing.v1.model.PropertiesDTO
import ish.oncourse.willow.billing.v1.service.PropertiesApi

import static ish.oncourse.configuration.Configuration.AdminProperty.CLIENT_ID

@CompileStatic
class PropertiesApiImpl implements PropertiesApi {

    @Override
    PropertiesDTO getProperties() {
        def clientId = Configuration.getValue(CLIENT_ID)
        return new PropertiesDTO(clientId)
    }
}
