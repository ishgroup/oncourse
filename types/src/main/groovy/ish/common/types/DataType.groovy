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
 * Data types for AutomationBinding records represent the type of data they can hold.
 */
@API
public enum DataType implements DisplayableExtendedEnumeration<Integer> {

    /**
     * Text value, also known as a String
     *
     * Database value: 0
     */
    @API
    TEXT(0, "Text"),

    /**
     * Renders as a multiline field with three lines by default. No validation. Allows new lines.
     *
     * Database value: 9
     */
    @API
    LONG_TEXT(9, "Long text"),

    /**
     * Date (without time or timezone)
     *
     * Database value: 1
     */
    @API
    DATE(1, "Date"),

    /**
     * Date and time, includes a timezone (by default the same as the server timezone)
     *
     * Database value: 2
     */
    @API
    DATE_TIME(2, "Date time"),

    /**
     * Boolean (true or false)
     *
     * Database value: 3
     */
    @API
    BOOLEAN(3, "Checkbox"),

    /**
     * Entity (the objectclass of a record in the database)
     *
     * Database value: 4
     */
    @API
    ENTITY(4, "Record"),

    /**
     * File (extra file for evaluate scripts)
     *
     * Database value: 5
     */
    @API
    FILE(5, "File"),

    /**
     * Money value
     *
     * Database value: 6
     */
    @API
    MONEY(6, "Money"),


    /**
     * Choices list
     *
     * Database value: 7
     */
    @API
    LIST(7, "List"),


    /**
     *  Map of label - code
     *
     * Database value: 8
     */
    @API
    MAP(8, "Map"),

    /**
     * String which is validated to be a URL.
     *
     * Database value: 10
     */
    @API
    URL(10, "URL"),

    /**
     * String which is validated to be a email address.
     *
     * Database value: 11
     */
    @API
    EMAIL(11, "Email"),


    /**
     * Message template drop down list.
     *
     * Database value: 12
     */
    @API
    MESSAGE_TEMPLATE(12, "Message template"),

    /**
     * Extra bindings
     *
     * Database value: 13
     */
    @API
    OBJECT(13, "Object"),

    /**
     * Pattern text
     *
     * Database value: 14
     */
    @API
    PATTERN_TEXT(14, "Pattern text");



    private Integer value;
    private String displayValue;

    DataType(Integer value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
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
