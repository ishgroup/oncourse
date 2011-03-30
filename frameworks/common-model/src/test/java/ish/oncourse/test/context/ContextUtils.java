package ish.oncourse.test.context;

import java.util.Collections;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.dba.derby.DerbyAdapter;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * The class for {@link InitialContext} setup.
 * 
 * @author ksenia
 * 
 */
public class ContextUtils {

	private static ServerRuntime cayenneRuntime;

	static {
		cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml");
	}

	public static ObjectContext createObjectContext() {
		return cayenneRuntime.getContext();
	}

	/**
	 * Binds needed dataSources.
	 * 
	 * @throws Exception
	 */
	public static void setupDataSources() throws Exception {
		// sets up the InitialContextFactoryForTest as default factory.

		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());

		// bind the initial context instance, because the JNDIDataSourceFactory
		// looks for it.
		InitialContextFactoryMock.bind("java:comp/env", new InitialContext());

		DataSource oncourse = createDataSource("oncourse");

		InitialContextFactoryMock.bind("jdbc/oncourse", oncourse);
		InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse", oncourse);

		DataSource oncourseBinary = createDataSource("oncourse_binary");

		InitialContextFactoryMock.bind("jdbc/oncourse_binary", oncourseBinary);
		InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse_binary", oncourseBinary);

		DataSource oncourseReference = createDataSource("oncourse_reference");

		InitialContextFactoryMock.bind("jdbc/oncourse_reference", oncourseReference);
		InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse_reference", oncourseReference);

		createTablesForDataSource(oncourse);
		createTablesForDataSource(oncourseBinary);
		createTablesForDataSource(oncourseReference);
	}

	/**
	 * Create datasourse to inmemory derby db by the given name.
	 * 
	 * @param name
	 * @return
	 */
	public static DataSource createDataSource(String name) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		dataSource.setUrl("jdbc:derby:memory:" + name + ";create=true");
		dataSource.setUsername("");
		dataSource.setPassword("");
		return dataSource;
	}

	/**
	 * Generates table for the given dataSource.
	 * 
	 * @param dataSource
	 * @throws Exception
	 */
	public static void createTablesForDataSource(DataSource dataSource) throws Exception {

		DataDomain domain = cayenneRuntime.getDataDomain();
		for (DataMap e : domain.getDataMaps()) {

			DbGenerator generator = new DbGenerator(new DerbyAdapter(), e, Collections.<DbEntity> emptyList(), domain);
			generator.setShouldCreateTables(true);
			generator.setShouldCreateFKConstraints(true);
			generator.setShouldCreatePKSupport(true);

			generator.runGenerator(dataSource);
		}
	}

	/**
	 * Sets empty string to the {@link Context#INITIAL_CONTEXT_FACTORY} system
	 * property.
	 */
	public static void cleanUpContext() {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "");
	}
}
