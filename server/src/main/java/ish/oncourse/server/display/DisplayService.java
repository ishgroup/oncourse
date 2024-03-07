/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.display;

import com.google.inject.Inject;
import io.bootique.annotation.BQConfigProperty;
import ish.oncourse.server.PreferenceController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DisplayService {
    private static final Logger logger = LogManager.getLogger();

    private Boolean subjectsAsEntity = null;

    public Boolean isSubjectsAsEntity() {
        return subjectsAsEntity;
    }

    @BQConfigProperty
    public void setSubjectsAsEntity(boolean subjectsAsEntity) {
        logger.warn("server has subjectsAsEntity = " + subjectsAsEntity);
        this.subjectsAsEntity = subjectsAsEntity;
    }
}
