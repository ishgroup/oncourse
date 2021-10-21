/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.willow.billing.env

import ish.oncourse.configuration.Configuration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.configuration.Configuration.AdminProperty.BILLING_UPDATE

class EnvironmentService {

    private static final String UPDATE_SCRIPT_PATH = Configuration.getValue(BILLING_UPDATE)
    private static final Logger logger = LogManager.logger


    boolean webSiteUpdated(String webSitekey) {
       return runCommand("website $webSitekey")
    }
    
    boolean collegeCreated(String collegeKey) {
        return runCommand("collegeCreate $collegeKey")
    }
    
    boolean collegeBillingChanged(String collegeKey) {
        return runCommand("collegeBillingChange $collegeKey")
    }
    
    private boolean runCommand(String command) {
        try {
            Runtime.getRuntime().exec("$UPDATE_SCRIPT_PATH $command")
            return true
        } catch (Exception e) {
            logger.catching(e)
            return false
        }
    }
}
