/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Define which dbunit file contains the source. Using this naming to match spring-test-dbunit
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface DatabaseSetup {

    boolean readOnly() default false

    /**
     * Determines the type of {@link DatabaseOperation operation} that will be used to reset the database.
     * @return The type of operation used to reset the database
     */
    DatabaseOperation type() default DatabaseOperation.DELETE_ALL

    /**
     * Provides the locations of the datasets that will be used to reset the database.
     * @return The dataset locations
     */
    String[] value() default [""]
}