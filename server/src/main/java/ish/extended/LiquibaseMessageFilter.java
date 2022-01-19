/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.extended;

import liquibase.logging.LogMessageFilter;

import java.util.Arrays;
import java.util.List;

public class LiquibaseMessageFilter implements LogMessageFilter {

    protected static final List<String> hiddenMessages = Arrays.asList(
            "Executing with the 'jdbc' executor",
            "No configuration value for liquibase.sql.logLevel found",
            "Configuration liquibase.sql.logLevel is using the default value of FINE"
    );

    @Override
    public String filterMessage(String message) {
        return message == null ? null : hiddenMessages.contains(message) ? null : message;
    }
}
