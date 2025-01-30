/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.display;

import io.bootique.annotation.BQConfigProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DisplayService {
    private static final Logger logger = LogManager.getLogger();

    private Boolean ausReporting = null;

    public Boolean getAusReporting() {
        return ausReporting;
    }


    @BQConfigProperty
    public void setAusReporting(boolean ausReporting) {
        logger.warn("server has ausReporting = " + ausReporting);
        this.ausReporting = ausReporting;
    }
}
