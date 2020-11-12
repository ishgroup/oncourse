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

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration

enum TwoFactorAuthorizationStatus implements DisplayableExtendedEnumeration<String> {

    DISABLED('disabled', 'Optional for all users'),

    ENABLED_FOR_ADMIN('enabled.admin', 'Required for admin users'),

    ENABLED_FOR_ALL('enabled.all', 'Required for all users')


    private String databaseValue

    private String displayName

    TwoFactorAuthorizationStatus(String databaseValue, String displayName) {
        this.databaseValue = databaseValue
        this.displayName = displayName
    }

    String getDatabaseValue() {
        return databaseValue
    }

    String getDisplayName() {
        return displayName
    }
}
