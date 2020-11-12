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
import liquibase.exception.DatabaseException;
import org.apache.commons.lang3.StringUtils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;


public class DerbyUpdateNextId extends IshTaskChange {

	private List<String> excluded_tables = Arrays.asList("DATA_VERSION", "DATABASECHANGELOG",
			"DATABASECHANGELOGLOCK", "ACLROLESYSTEMUSER", "STUDENTNUMBER", "AUTO_PK_SUPPORT", "DB_VERSION");

	private static final String SELECT_MAX_ID = "SELECT MAX(ID) AS NEXT_ID FROM %s";
	private static final String RESET_NEXT_ID = "ALTER TABLE %s ALTER COLUMN ID RESTART WITH %d";


	private String tableName;

	@Override
	public void execute(Database database) throws CustomChangeException {
		String currentTableName = null;
		try {
			var connection = (JdbcConnection) database.getConnection();

			if (StringUtils.isNotBlank(tableName)) {
				updateNextId(tableName, connection);
			} else {
				var dbmd = connection.getMetaData();
				String[] types = {"TABLE"};
				var rs = dbmd.getTables(null, null, "%", types);
				while (rs.next()) {
					currentTableName = rs.getString("TABLE_NAME");
					if (!excluded_tables.contains(currentTableName)) {
						updateNextId(currentTableName, connection);
					}
				}
			}
		} catch (Exception e) {
			throw new CustomChangeException("Fail to reset nex ID for " + currentTableName + ": ", e);
		}
	}

	private void updateNextId(String tableName, JdbcConnection connection) throws DatabaseException, SQLException {
		Long nextId = null;

		var selectStatement = connection.createStatement();
		var resultSet = selectStatement.executeQuery(String.format(SELECT_MAX_ID, tableName));
		if (resultSet.next()) {
			nextId = resultSet.getLong("NEXT_ID");
		}
		resultSet.close();

		if (nextId != null) {
			var alterStatement= connection.createStatement();
			alterStatement.executeUpdate(String.format(RESET_NEXT_ID, tableName, ++nextId));
			alterStatement.close();
		}
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
