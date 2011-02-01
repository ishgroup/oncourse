package ish.oncourse.test.context;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * The class for {@link InitialContext} setup.
 * 
 * @author ksenia
 * 
 */
public class ContextUtils {

	/**
	 * Binds needed dataSources.
	 * 
	 * @throws NamingException
	 */
	public static void setupDataSources() throws NamingException {
		// sets up the InitialContextFactoryForTest as default factory.

		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				InitialContextFactoryMock.class.getName());

		InitialContextFactoryMock.bind("jdbc/oncourse", getDataSource("oncourse"));
		InitialContextFactoryMock.bind("jdbc/oncourse_binary", getDataSource("oncourse_binary"));
		InitialContextFactoryMock.bind("jdbc/oncourse_reference",
				getDataSource("oncourse_reference"));
		// bind the initial context instance, because the JNDIDataSourceFactory
		// looks for it.
		InitialContextFactoryMock.bind("java:comp/env", new InitialContext());
	}

	/**
	 * Create datasourse to inmemory derby db by the given name.
	 * 
	 * @param name
	 * @return
	 */
	private static DataSource getDataSource(String name) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		dataSource.setUrl("jdbc:derby:memory:" + name + ";create=true");
		dataSource.setUsername("");
		dataSource.setPassword("");
		return dataSource;
	}

	/**
	 * Sets empty string to the {@link Context#INITIAL_CONTEXT_FACTORY} system
	 * property.
	 */
	public static void cleanUpContext() {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "");
	}
}
