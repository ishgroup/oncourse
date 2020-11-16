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

enum EntityRelationCartAction implements DisplayableExtendedEnumeration<Integer>  {

    NO_ACTION(0, "No action"),

    SUGGESTION(1, "Suggestion"),

    ADD_ALLOW_REMOVAL(2, "Add and allow removal"),

    ADD_NO_REMOVAL(3, "Add but do not allow removal");

    private String displayName
    private int value

    private EntityRelationCartAction(int value, String displayName) {
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

    @Override
    String toString() {
        return displayName
    }
}