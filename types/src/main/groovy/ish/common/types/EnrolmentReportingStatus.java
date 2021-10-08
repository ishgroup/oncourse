/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Enrolments can display info about student loan status.
 */
public enum EnrolmentReportingStatus implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Database value: 1
     *
     * Status of enrolment by default. It hides the VSL fields but adds the enrolment to the TCSI reporting
     */
    ELIGIBLE(1,"eligible"),

    /**
     * Database value: 2
     *
     * Hides the VSL fields and is NOT added to the TCSI reporting
     */
    NOT_ELIGIBLE(2,"not eligible"),

    /**
     * Database value: 3
     *
     * Displays the VSL fields, allows the VSL fields to be edited and adds the enrolment to TCSI reporting
     */
    ONGOING(3, "ongoing"),

    /**
     * Database value: 4
     *
     * Displays the fields but locks them for editing. This will also stop the enrolment being reported to TCSI.
     */
    FINALIZED(4,"finalized");

    private int value;
    private String displayName;

    EnrolmentReportingStatus(int value, String displayName) {
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
