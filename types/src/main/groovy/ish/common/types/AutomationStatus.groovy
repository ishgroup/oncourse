/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

/**
 * Status of automations
 */
@API
public enum AutomationStatus implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 0
     *
     * Automation is not installed
     */
    @API
    NOT_INSTALLED(0, "Not Installed"),

    /**
     * Database value: 1
     *
     * Automation is installed but not enabled
     */
    @API
    INSTALLED_DISABLED(1, "Installed but disabled"),

    /**
     * Database value: 2
     *
     * Automation is enabled
     */
    @API
    ENABLED(2, "Enabled"),


    private String displayName
    private int value

    private AutomationStatus(int value, String displayName) {
        this.value = value
        this.displayName = displayName
    }

    @Override
    Integer getDatabaseValue() {
        return this.value
    }

    @Override
    String getDisplayName() {
        return this.displayName
    }

    @Override
    String toString() {
        return getDisplayName()
    }
}