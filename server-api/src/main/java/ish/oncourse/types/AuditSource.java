/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Where an audited change came from
 */
@API
public enum AuditSource implements DisplayableExtendedEnumeration<String> {

    /**
     * A logged in user, through the browser session
     */
    @API
    USER("USER", "User"),

    /**
     * A request authorised with an API token
     */
    @API
    API_TOKEN("API_TOKEN", "API token"),

    /**
     * The onCourse Assistant, acting for the logged in user
     */
    @API
    ASSISTANT("ASSISTANT", "Assistant"),

    /**
     * An automation (script) run
     */
    @API
    AUTOMATION("AUTOMATION", "Automation");


    private String displayValue;
    private String value;

    AuditSource(String value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
    }

    @Override
    public String getDisplayName() {
        return displayValue;
    }

    @Override
    public String getDatabaseValue() {
        return value;
    }
}
