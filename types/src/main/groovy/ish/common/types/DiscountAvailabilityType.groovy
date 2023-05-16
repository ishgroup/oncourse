/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

/**
 * A set of values for discount availability types.
 */
@API
enum DiscountAvailabilityType implements DisplayableExtendedEnumeration<Integer>{

    /**
     * Database value: 0
     *
     * Office only
     * The single value that makes discount not available on web
     */
    @API
    OFFICE_ONLY(0, "Office only"),

    /**
     * Database value: 1
     *
     * Online only
     * Default value for old available on web option
     */
    @API
    ONLINE_ONLY(1, "Online only"),

    /**
     * Database value: 2
     *
     * Online and office
     * Also available on web
     */
    @API
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

    @Override
    String toString() {
        return displayName
    }

    static DiscountAvailabilityType fromValue(String text) {
        return values().find {it.displayName.equals(text)}
    }
}