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

package ish.oncourse.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Actions logged in Audits
 */
@API
public enum AuditAction implements DisplayableExtendedEnumeration<String> {

    /**
     * Entity created
     */
    @API
    CREATE("CREATE", "Entity created"),

    /**
     * Entity updated
     */
    @API
    UPDATE("UPDATE", "Entity updated"),

    /**
     * Entity deleted
     */
    @API
    DELETE("DELETE", "Entity deleted"),

    /**
     * Script execution failed
     */
    @API
    SCRIPT_FAILED("SCRIPT_FAILED", "Script failed"),

    /**
     * Script execution successful
     */
    @API
    SCRIPT_EXECUTED("SCRIPT_EXECUTED", "Script executed"),

    /**
     * Email Collision
     */
    @API
    COLLISION("COLLISION", "Email Collision"),

    /**
     * Api token used
     */
    @API
    API_TOKEN("API_TOKEN", "Api token used");
    
    
    private String displayValue;
    private String value;

    AuditAction(String value, String displayValue) {
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
