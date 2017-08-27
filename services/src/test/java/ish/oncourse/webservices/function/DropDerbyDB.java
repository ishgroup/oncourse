/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.function;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.SQLException;

/**
 * User: akoiro
 * Date: 27/8/17
 */
public class DropDerbyDB {

	private String name;

	public DropDerbyDB(String name) {
		this.name = name;
	}

	public void drop() {
		try {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
			dataSource.setUrl(String.format("jdbc:derby:memory:%s;drop=true", name));
			dataSource.setUsername("");
			dataSource.setPassword("");
			dataSource.setMaxActive(100);
			dataSource.getConnection();
			dataSource.close();
		} catch (SQLException e) {
		}
	}
}
