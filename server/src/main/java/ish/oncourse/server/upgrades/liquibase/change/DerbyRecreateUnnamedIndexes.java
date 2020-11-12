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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class DerbyRecreateUnnamedIndexes extends IshTaskChange {

	private List<String> tables = Arrays.asList("REPORTOVERLAY", "SCRIPT", "PAYMENTMETHOD", "EMAILTEMPLATE");
	private static final String COLUMN_NAME = "NAME";
	private static final String DROP_INDEX_STATEMENT = "ALTER TABLE %s DROP CONSTRAINT \"%s\"";
	private static final String ADD_INDEX_STATEMENT = "ALTER TABLE %s ADD CONSTRAINT \"%s\" UNIQUE (NAME)";


	@Override
	public void execute(Database database) throws CustomChangeException {

		try {
			var connection = (JdbcConnection) database.getConnection();
			var dbmd = connection.getMetaData();

			for (var tableName : tables) {
				var rs = dbmd.getIndexInfo(null, null, tableName, true, true);
				while (rs.next()) {
					if (COLUMN_NAME.equals(rs.getString("COLUMN_NAME"))) {
						var alterStatement = connection.createStatement();
						alterStatement.execute(String.format(DROP_INDEX_STATEMENT, tableName, rs.getString("INDEX_NAME")));
						alterStatement.close();

						alterStatement = connection.createStatement();
						alterStatement.execute(String.format(ADD_INDEX_STATEMENT, tableName, tableName + "_NAME_UNIQUE_CONSTRAINT"));
						alterStatement.close();
					}
				}
			}

		} catch (Exception e) {
			throw new CustomChangeException("Fail to reset index: ", e);
		}
	}
}
