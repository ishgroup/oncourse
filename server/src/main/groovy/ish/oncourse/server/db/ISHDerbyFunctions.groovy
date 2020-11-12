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

package ish.oncourse.server.db

import groovy.transform.CompileDynamic
import liquibase.database.AbstractJdbcDatabase
import liquibase.database.Database
import liquibase.database.core.DerbyDatabase


/**
 * Created by akoiro on 8/06/2016.
 */
@CompileDynamic
class ISHDerbyFunctions {
    public static final String SQL_CREATE_IFNULL = 'CREATE FUNCTION IFNULL\n' +
            '(  value DECIMAL(10,2) , result DECIMAL(10,2)  )\n' +
            'RETURNS DECIMAL(10,2)\n' +
            'PARAMETER STYLE JAVA\n' +
            'NO SQL LANGUAGE JAVA\n' +
            'EXTERNAL NAME \'ish.oncourse.server.print.ISHDerbyFunctions.IFNULL\''

    public static final String SQL_CREATE_REPLACE = 'CREATE FUNCTION REPLACE\n' +
            '(source CLOB, toReplace VARCHAR, replacement VARCHAR)\n' +
            'RETURNS CLOB\n' +
            'PARAMETER STYLE JAVA\n' +
            'NO SQL LANGUAGE JAVA\n' +
            'EXTERNAL NAME \'ish.oncourse.server.print.ISHDerbyFunctions.REPLACE\''


    static BigDecimal IFNULL(BigDecimal value, BigDecimal result) {
        return value != null ? value : result
    }

    static String REPLACE(String value, String toReplace, String replacement) {
        if (!value) {
            return value
        }
        value.replace(toReplace,replacement)
    }

    static void removeReserverWords(Database database) {
        if (database instanceof DerbyDatabase) {
            (AbstractJdbcDatabase.metaClass.getProperty(database, 'reservedWords') as HashSet).clear()
        }
    }

}
