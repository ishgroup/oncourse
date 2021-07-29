/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration

/**
 * Set of supported SSO providers for skillsoncourse portal
 *
 */
enum SSOProviderType implements DisplayableExtendedEnumeration<Integer> {

 
    GOOGLE(0, "Google"),
    
    MICROSOFT(1, "Microsoft"),
    
    FACEBOOK(2, "Facebook");

    private String displayValue
    private int value

    private SSOProviderType(int value, String displayValue) {
        this.displayValue = displayValue
        this.value = value
    }

    @Override
    String getDisplayName() {
        return displayValue
    }

    @Override
    Integer getDatabaseValue() {
        return value
    }

    @Override
    String toString() {
        return getDisplayName()
    }
}
