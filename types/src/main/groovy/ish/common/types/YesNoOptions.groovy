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

enum YesNoOptions  implements DisplayableExtendedEnumeration<Integer> {

    NOT_DEFINED(0,'Not stated', null),
    NO(1,"Yes", Boolean.TRUE),
    YES(2,"No", Boolean.FALSE)


    private Integer value
    private Boolean booleanValue
    private String displayName

    private YesNoOptions(Integer value, String displayName, Boolean booleanValue) {
        this.value = value
        this.displayName = displayName
        this.booleanValue = booleanValue
    }

    @Override
    String getDisplayName() {
        return displayName
    }

    @Override
    Integer getDatabaseValue() {
        return value
    }

    Boolean getBooleanValue() {
        return booleanValue
    }
}
