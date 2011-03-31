package ish.oncourse.test;

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

		try {
			// sets up the InitialContextFactoryForTest as default factory.
			System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());

			// bind the initial context instance, because the
			// JNDIDataSourceFactory
			// looks for it.
			InitialContextFactoryMock.bind("java:comp/env", new InitialContext());
			
			cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ObjectContext createObjectContext() {
		return cayenneRuntime.getContext();
	}

	public static void setupOnCourseDataSource() throws Exception {
		DataSource oncourse = createDataSource("oncourse");
		InitialContextFactoryMock.bind("jdbc/oncourse", oncourse);
		createOnCourseTables(oncourse);
	}

	public static void setupOnCourseBinaryDataSource() throws Exception {
		DataSource oncourseBinary = createDataSource("oncourse_binary");
		InitialContextFactoryMock.bind("jdbc/oncourse_binary", oncourseBinary);
		createOnCourseBinaryTables(oncourseBinary);
	}

	public static void setupOnCourseReferenceDataSource() throws Exception {
		DataSource oncourseReference = createDataSource("oncourse_reference");
		InitialContextFactoryMock.bind("jdbc/oncourse_reference", oncourseReference);
		createOnCourseReferenceTables(oncourseReference);
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
	private static void createTablesForDataSource(DataSource dataSource, DataMap map) throws Exception {
		DataDomain domain = cayenneRuntime.getDataDomain();

		DbGenerator generator = new DbGenerator(new DerbyAdapter(), map, Collections.<DbEntity> emptyList(), domain);
		generator.setShouldCreateTables(true);
		generator.setShouldCreateFKConstraints(true);
		generator.setShouldCreatePKSupport(true);

		generator.runGenerator(dataSource);
	}
}
