/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

enum LeadStatus implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 0
     *
     * Lead isn't active.
     */
    @API
    CLOSE(0, "Close"),

    /**
     * Database value: 1
     *
     * Lead is active.
     */
    @API
    OPEN(1, "Open");

    private String displayName
    private int value

    private LeadStatus(int value, String displayName) {
        this.displayName = displayName
        this.value = value
    }

    @Override
    String getDisplayName() {
        return this.displayName
    }

    @Override
    Integer getDatabaseValue() {
        return this.value
    }

    @Override
    String toString() {
        return this.displayName
    }
}
