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