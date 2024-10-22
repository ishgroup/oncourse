/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration

public enum FieldValidationType implements DisplayableExtendedEnumeration<Integer>{
    CONTACT_EMAIL_ADDRESS(0, "Email of related contact");

    private String displayName
    private int databaseValue

    FieldValidationType(int databaseValue, String displayName) {
        this.displayName = displayName
        this.databaseValue = databaseValue
    }

    @Override
    String getDisplayName() {
        return displayName
    }

    @Override
    Integer getDatabaseValue() {
        return databaseValue
    }
}