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

enum GradingEntryType implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 1
     *
     * The Grading system is numeric, from minValue to maxValue.
     */
    @API
    NUMBER(1, "Number"),

    /**
     * Database value: 2
     *
     * The Grading system is entries, which looks like {name: value}; The value of each entry is a lower bound of range.
     */
    @API
    NAME(2, "Name");

    private int value
    private String displayName

    private GradingEntryType(int value, String displayName) {
        this.value = value
        this.displayName = displayName
    }

    @Override
    String getDisplayName() {
        return null
    }

    @Override
    Integer getDatabaseValue() {
        return null
    }

    @Override
    String toString() {
        return getDisplayName();
    }
}
