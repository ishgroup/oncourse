/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish

enum DatabaseOperation {
    /**
     * Updates the contents of existing database tables from the dataset.
     */
    UPDATE,

    /**
     * Inserts new database tables and contents from the dataset.
     */
    INSERT,

    /**
     * Refresh the contents of existing database tables. Rows from the dataset will insert or replace existing data. Any
     * database rows that are not in the dataset remain unaffected.
     */
    REFRESH,

    /**
     * Deletes database table rows that matches rows from the dataset.
     */
    DELETE,

    /**
     * Deletes all rows from a database table when the table is specified in the dataset. Tables in the database but not
     * in the dataset remain unaffected.
     * @see #TRUNCATE_TABLE
     */
    DELETE_ALL,

    /**
     * Deletes all rows from a database table when the table is specified in the dataset. Tables in the database but not
     * in the dataset are unaffected. Identical to {@link #DELETE_ALL} expect this operation cannot be rolled back and
     * is supported by less database vendors.
     * @see #DELETE_ALL
     */
    TRUNCATE_TABLE,

    /**
     * Deletes all rows from a database table when the tables is specified in the dataset and subsequently insert new
     * contents. Equivalent to calling {@link #DELETE_ALL} followed by {@link #INSERT}.
     */
    CLEAN_INSERT;
}