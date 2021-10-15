/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.settings

import com.google.inject.Inject
import ish.oncourse.api.request.RequestService
import ish.oncourse.services.persistence.ICayenneService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class SettingsService {
    
    private static final Logger logger = LogManager.logger

    @Inject
    private ICayenneService cayenneService
    
    @Inject
    private RequestService requestService
    
    
    Integer getConcurentUsrs() {
        return  null  
    }
    
    private String getSettings(String key) {
        return  null
    }
    
}
