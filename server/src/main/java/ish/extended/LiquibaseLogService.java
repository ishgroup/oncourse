/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.extended;

import liquibase.logging.Logger;
import liquibase.logging.core.AbstractLogService;

public class LiquibaseLogService extends AbstractLogService {

    private static final int DEFAULT_PRIORITY = 5;
    private int priority;

    public LiquibaseLogService() {
        this(DEFAULT_PRIORITY);
    }

    public LiquibaseLogService(int proprity) {
        this.priority = proprity;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * @see liquibase.servicelocator.StandardServiceLocator
     */
    @Override
    public Logger getLog(Class clazz) {
        return new LiquibaseLoggerAdapter(new LiquibaseMessageFilter());
    }
}
