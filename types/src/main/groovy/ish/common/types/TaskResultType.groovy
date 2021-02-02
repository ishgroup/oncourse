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

@API
enum TaskResultType implements DisplayableExtendedEnumeration<Integer> {

    @API
    IN_PROGRESS(0, 'Success'),

    @API
    SUCCESS(1, 'Success'),

    @API
    FAILURE(2, 'Failure')

    private int value
    private String displayName

    private TaskResultType(int value, String displayName) {
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