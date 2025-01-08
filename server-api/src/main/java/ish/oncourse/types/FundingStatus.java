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

@API
public enum FundingStatus implements DisplayableExtendedEnumeration<Integer> {

    /**
     * exported status
     *
     * Database value: 0
     */
    @API
    EXPORTED(0, "exported"),

    /**
     * successful status
     *
     * Database value: 1
     */
    @API
    SUCCESS(1, "success"),

    /**
     * failed status
     *
     * Database value: 2
     */
    @API
    FAILED(2, "failed");

    private final String displayValue;
    private final int value;

    FundingStatus(int value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
    }

    @Override
    public String getDisplayName() {
        return displayValue;
    }

    @Override
    public Integer getDatabaseValue() {
        return value;
    }
}
