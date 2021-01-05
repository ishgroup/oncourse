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
package ish.oncourse.server.upgrades.liquibase.change;

import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;

public class ClusteredAutoincrementServiceInit extends IshTaskChange {

    @Override
    public void execute(Database database) throws CustomChangeException {
        try {
            JdbcConnection connection = (JdbcConnection) database.getConnection();
            connection.setAutoCommit(false);
            try(var statement = connection.createStatement()) {
                statement.execute(
                        "INSERT INTO `SequenceSupport` (`tableName`, `nextId`) " +
                        "SELECT 'student', coalesce(coalesce(max(`studentNumber`), 0) + 1, 1) FROM `Student`"
                );
                statement.execute(
                        "INSERT INTO `SequenceSupport` (`tableName`, `nextId`) " +
                        "SELECT 'invoice', coalesce(coalesce(max(`invoiceNumber`), 0) + 1, 1) FROM `Invoice`"
                );
            }
            connection.commit();
        } catch (Exception e) {
            throw new CustomChangeException("Adding initial values to `SequenceSupport` failed:", e);
        }
    }
}
