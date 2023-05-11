/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration

enum DiscountAvailabilityType implements DisplayableExtendedEnumeration<Integer>{

    OFFICE_ONLY(0, "Office only"),
    ONLINE_ONLY(1, "Online only"),
    ONLINE_AND_OFFICE(2, "Online and office"),


    private String displayName;
    private int value;

    private DiscountAvailabilityType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    @Override
    String getDisplayName() {
        return displayName
    }

    @Override
    Integer getDatabaseValue() {
        return value
    }
}