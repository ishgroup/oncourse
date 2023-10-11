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

package ish.oncourse.aql.impl;

import ish.oncourse.aql.model.CustomFieldDateMarker;
import ish.oncourse.aql.model.CustomFieldDateTimeMarker;
import ish.oncourse.aql.model.CustomFieldMarker;
import org.apache.cayenne.Persistent;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Classifier of java types.
 * It is used to validate available operations for given attribute,
 * as this information is not available at parsing time.
 *

 */
public enum TypeClassifier {

    /**
     * All primitive types (except boolean) and all classes that extend {@link Number}
     */
    NUMERIC,

    /**
     * All classes that extend {@link CharSequence}
     */
    STRING,

    /**
     * {@link java.util.Date}, {@link java.sql} and {@link java.time} types.
     */
    DATE,

    /**
     * boolean and Boolean
     */
    BOOLEAN,

    /**
     * All enums
     */
    ENUM,

    /**
     * Relationships
     */
    PERSISTENT,

    /**
     * Custom field
     */
    CUSTOM_FIELD,


    /**
     * System user,  special case of persistent object, that allows 'createdBy is me'
     */
    SYSTEM_USER,

    /**
     * Contact, special case of persistent object, that allows operator '~'
     */
    CONTACT,

    /**
     * Site, special case of persistent object, that allows operator '~'
     */
    SITE,

    /**
     * Room, special case of persistent object, that allows operator '~'
     */
    ROOM,

    /**
     * Qualification, special case of persistent object, that allows operator '~'
     */
    QUALIFICATION,

    /**
     * Module, special case of persistent object, that allows operator '~'
     */
    MODULE,

    /**
     * Document, special case of persistent object, that allows operator '~'
     */
    DOCUMENT,

    /**
     * Invoice, special case of persistent object, that allows operator '~'
     */
    INVOICE,

    /**
     * Account transaction, special case of persistent object, that allows operator '~'
     */
    ACCOUNT_TRANSACTION,

    /**
     * Account, special case of persistent object, that allows operator '~'
     */
    ACCOUNT,

    /**
     * Payslip, special case of persistent object, that allows operator '~'
     */
    PAYSLIP,

    CORPORATE_PASS,

    AUDIT,

    SCRIPT,

    BANKING,

    /**
     * Custom field with date type
     */
    CUSTOM_FIELD_DATE,

    /**
     * Custom field with date time type
     */
    CUSTOM_FIELD_DATE_TIME,

    /**
     * Everything else
     */
    UNKNOWN;

    private static final List<Class<?>> JAVA_DATE_TYPES = Arrays.asList(
            java.util.Date.class,
            LocalDate.class,
            LocalTime.class,
            LocalDateTime.class,
            Date.class,
            Time.class,
            Timestamp.class
    );

    public String getReadableName() {
        return this.name().toLowerCase();
    }

    public static TypeClassifier of(Class<?> javaType) {
        if(javaType == null) {
            return UNKNOWN;
        }

        if(javaType.isEnum()) {
            return ENUM;
        }

        if(javaType == CustomFieldMarker.class) {
            return CUSTOM_FIELD;
        }

        if(javaType == CustomFieldDateMarker.class) {
            return CUSTOM_FIELD_DATE;
        }

        if(javaType == CustomFieldDateTimeMarker.class) {
            return CUSTOM_FIELD_DATE;
        }

        if(Boolean.class == javaType || boolean.class == javaType) {
            return BOOLEAN;
        }

        if(javaType == byte.class
                ||  javaType == short.class
                ||  javaType == char.class
                ||  javaType == int.class
                ||  javaType == long.class
                ||  javaType == float.class
                ||  javaType == double.class) {
            return NUMERIC;
        }

        if (Number.class.isAssignableFrom(javaType)) {
            return NUMERIC;
        }

        if (CharSequence.class.isAssignableFrom(javaType)) {
            return STRING;
        }

        if (Persistent.class.isAssignableFrom(javaType)) {
            switch (javaType.getSimpleName()) {
                case "Contact":
                    return CONTACT;
                case "Site":
                    return SITE;
                case "Banking":
                    return BANKING;
                case "SystemUser":
                    return SYSTEM_USER;
            }
            return PERSISTENT;
        }

        if (JAVA_DATE_TYPES.contains(javaType)) {
            return DATE;
        }

        return UNKNOWN;
    }

}
