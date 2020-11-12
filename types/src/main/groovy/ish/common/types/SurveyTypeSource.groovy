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
enum SurveyTypeSource implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 1
     */
    @API
    ONCOURSE(1, 'onCourse'),

    /**
     * Database value: 2
     */
    @API
    INTEGRATION(2, 'Integration'),

    /**
     * Database value: 10
     */
    @API
    OTHER(10, 'Other')

    private String displayName
    private int value

    private SurveyTypeSource(int value, String displayName) {
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
