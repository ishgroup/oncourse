/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.util

import javax.sql.DataSource
import java.sql.Connection
import java.sql.Statement

class DbConnectionUtils {
    static <T> T executeWithClose(Closure<T> closure, DataSource dataSource){
        Connection connection = null
        Statement statement = null
        try {
            connection = dataSource.getConnection()
            statement = connection.createStatement()
            return closure.call(statement)
        } finally {
            if (statement != null && !statement.isClosed())
                statement.close()
            if (connection != null && !connection.isClosed())
                connection.close()
        }
    }

    static BigDecimal getBigDecimalForDbQuery(String query, DataSource dataSource) {
        def getValue = { Statement statement ->
            return getNumberForQueryFromDb(statement, query)
        }

        return executeWithClose(getValue, dataSource) as Long
    }

    private static BigDecimal getNumberForQueryFromDb(Statement statement, String query) {
        def resultSet = statement.executeQuery(query)
        resultSet.last()
        return resultSet.getBigDecimal(1)
    }


}
