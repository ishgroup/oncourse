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

@API
enum DeliverySchedule implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 1
     */
    @API
    ON_ENROL(1, 'On enrol'),

    /**
     * Database value: 2
     */
    @API
    ON_START(2, 'On start'),

    /**
     * Database value: 3
     */
    @API
    MIDWAY(3, 'Midway'),

    /**
     * Database value: 4
     */
    @API
    AT_COMPLETION(4, 'At completion'),

    /**
     * Database value: 5
     */
    @API
    ON_DEMAND(5, 'On demand')

    private String displayName
    private int value

    private DeliverySchedule(int value, String displayName) {
        this.value = value
        this.displayName = displayName
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
