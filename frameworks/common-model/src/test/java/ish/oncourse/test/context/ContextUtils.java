package ish.oncourse.test.context;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class ContextUtils {

	public static void setupDataSources() throws NamingException {
		// sets up the InitialContextFactoryForTest as default factory.

		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				InitialContextFactoryForTest.class.getName());

		InitialContextFactoryForTest.bind("jdbc/oncourse", getDataSource("oncourse"));
		InitialContextFactoryForTest.bind("jdbc/oncourse_binary", getDataSource("oncourse_binary"));
		InitialContextFactoryForTest.bind("jdbc/oncourse_reference",
				getDataSource("oncourse_reference"));
		InitialContextFactoryForTest.bind("java:comp/env", new InitialContext());
	}

	private static DataSource getDataSource(String name) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		dataSource.setUrl("jdbc:derby:" + name + ";create=true");
		dataSource.setUsername("");
		dataSource.setPassword("");
		return dataSource;
	}

	public static void cleanUpContext() {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "");
	}
}
