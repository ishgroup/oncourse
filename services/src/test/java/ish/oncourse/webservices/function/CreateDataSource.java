/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.function;

import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;

/**
 * User: akoiro
 * Date: 26/8/17
 */
public class CreateDataSource {
	private String name;

	public CreateDataSource(String name) {
		this.name = name;
	}

	public DataSource create() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		dataSource.setUrl(String.format("jdbc:derby:memory:%s;create=true", name));
		dataSource.setUsername("");
		dataSource.setPassword("");
		dataSource.setMaxActive(100);
		return dataSource;
	}

}
