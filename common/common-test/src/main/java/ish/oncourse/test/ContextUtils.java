package ish.oncourse.test;

import java.util.Collections;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
		InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse_binary", oncourse);

		DataSource oncourseReference = createDataSource("oncourse_reference");

		InitialContextFactoryMock.bind("jdbc/oncourse_reference", oncourseReference);
		InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse_reference", oncourseReference);

		DataDomain domain = cayenneRuntime.getDataDomain();

		createTablesForDataSource(oncourse, domain.getDataMap("oncourse"));
		createTablesForDataSource(oncourseBinary, domain.getDataMap("oncourseBinary"));
		createTablesForDataSource(oncourseReference, domain.getDataMap("oncourseReference"));
	}

	public static DataSource getDataSource(String location) throws Exception {
		Context context = new InitialContext();
		DataSource dataSource;
		try {
			Context envContext = (Context) context.lookup("java:comp/env");
			dataSource = (DataSource) envContext.lookup(location);
		} catch (NamingException namingEx) {
			dataSource = (DataSource) context.lookup(location);
		}

		return dataSource;
	}

	public static void createOnCourseTables() throws Exception {
		DataDomain domain = cayenneRuntime.getDataDomain();
		createTablesForDataSource(getDataSource("jdbc/oncourse"), domain.getDataMap("oncourse"));
	}
	
	public static void createOnCourseTables(DataSource dataSource) throws Exception {
		DataDomain domain = cayenneRuntime.getDataDomain();
		createTablesForDataSource(dataSource, domain.getDataMap("oncourse"));
	}
	
	public static void createOnCourseBinaryTables(DataSource dataSource) throws Exception {
		DataDomain domain = cayenneRuntime.getDataDomain();
		createTablesForDataSource(dataSource, domain.getDataMap("oncourseBinary"));
	}
	
	public static void createOnCourseReferenceTables(DataSource dataSource) throws Exception {
		DataDomain domain = cayenneRuntime.getDataDomain();
		createTablesForDataSource(dataSource, domain.getDataMap("oncourseReference"));
	}

	/**
	 * Generates table for the given dataSource.
	 * 
	 * @param dataSource
	 * @throws Exception
	 */
	private static void createTablesForDataSource(DataSource dataSource, DataMap map) throws Exception {
		DataDomain domain = cayenneRuntime.getDataDomain();

		DbGenerator generator = new DbGenerator(new DerbyAdapter(), map, Collections.<DbEntity> emptyList());
		generator.setShouldCreateTables(true);
		generator.setShouldCreateFKConstraints(true);
		generator.setShouldCreatePKSupport(true);

		generator.runGenerator(dataSource);
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
	 * Sets empty string to the {@link Context#INITIAL_CONTEXT_FACTORY} system
	 * property.
	 */
	public static void cleanUpContext() {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "");
	}
}
