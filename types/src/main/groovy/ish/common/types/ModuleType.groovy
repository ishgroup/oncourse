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
import ish.oncourse.API

/**
 * Represents type of training component
 */
@API
enum ModuleType implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 0
     *
     * Unit of competency type
     */
    @API
    UNIT_OF_COMPETENCY(0, 'Unit of competency'),
    /**
     * Database value: 1
     *
     * Module type
     */
    @API
    MODULE(1, 'Module'),
    /**
     * Database value: 2
     *
     * Unit of study type
     */
    @API
    UNIT_OF_STUDY(2, 'Unit of study'),
    /**
     * Database value: 99
     *
     * Other types
     */
    @API
    OTHER(99, 'Other')

    private String displayName
    private int value

    ModuleType(int value, String displayName) {
        this.value = value
        this.displayName = displayName
    }

    @Override
    String getDisplayName() {  this.displayName }

    @Override
    Integer getDatabaseValue() { this.value }

    @Override
    String toString() { getDisplayName() }
}
