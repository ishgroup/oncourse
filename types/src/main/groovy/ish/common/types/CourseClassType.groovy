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
 * Course class types
 *
 */
@API
public enum CourseClassType implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 0
     *
     * Usual class with sessions
     */
    @API
    WITH_SESSIONS(0, "With Sessions"),

    /**
     * Database value: 1
     *
     * Self-paced class without sessions
     */
    @API
    DISTANT_LEARNING(1, "Distant learning"),

    /**
     * Database value: 2F
     *
     * Hybrid class with sessions and self-paced class functionality
     */
    @API
    HYBRID(2, "Hybrid");

    private String displayName;
    private int value;

    private CourseClassType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    @Override
    public Integer getDatabaseValue() {
        return this.value;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
