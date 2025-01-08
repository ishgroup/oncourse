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

/**
 * Output types are the type of file created by an Export. This changes the output file extension and mime-type.utpu
 */
@API
public enum OutputType implements DisplayableExtendedEnumeration<Integer> {

    /**
     * csv output type
     *
     * Database value: 0
     */
    @API
    CSV(0,  "csv", "text/csv", "csv"),

    /**
     * json output type
     *
     * Database value: 1
     */
    @API
    JSON(1, "json", "application/json", "json"),

    /**
     * xml output type
     *
     * Database value: 2
     */
    @API
    XML(2, "xml", "application/xml", "xml"),

    /**
     * Calendar output type, also known as ics or ical.
     *
     * Database value: 3
     */
    @API
    ICAL(3, "calendar", "text/calendar", "ics"),

    /**
     * Text output type
     *
     * Database value: 4
     */
    @API
    TEXT(4, "text", "text/plain", "txt"),

    /**
     * Pdf output type
     *
     * Database value: 5
     */
    @API
    PDF(5, "pdf", "application/pdf", "pdf");

    private final Integer value;
    private final String displayValue;
    private final String mimeType;
    private final String fileExtension;

    OutputType(Integer value, String displayValue, String mimeType, String fileExtension) {
        this.value = value;
        this.displayValue = displayValue;
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFileExtension() {
        return fileExtension;
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
