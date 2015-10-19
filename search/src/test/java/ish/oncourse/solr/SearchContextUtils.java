/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import ish.math.MoneyType;
import ish.oncourse.test.InitialContextFactoryMock;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.log.JdbcEventLogger;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.Relationship;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class SearchContextUtils {
	private static Mysql mysql;
	private static boolean createSchema = false;
	private static boolean dropSchema = false;
	private static boolean createTables = false;

	private static ServerRuntime cayenneRuntime;

	public static final String SHOULD_CREATE_TABLES="createTables";
	public static final String SHOULD_CREATE_FK_CONSTRAINTS="createFKConstraints";
	public static final String SHOULD_CREATE_PK_SUPPORT="createPKSupport";

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
		setupDataSourcesWithParams(null);
	}

	public static void setupDataSourcesWithParams(Map<String, Boolean> params) throws Exception {
		// sets up the InitialContextFactoryForTest as default factory.

		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());

		DataSource oncourse = createDataSource();

		InitialContext ic = new InitialContext();
		ic.createSubcontext("java:/comp/env");
		ic.createSubcontext("java:/comp/env/jdbc");
		ic.bind("java:comp/env/jdbc/oncourse", oncourse);

		// bind the initial context instance, because the JNDIDataSourceFactory
		// looks for it.
		InitialContextFactoryMock.bind("java:comp/env", ic);

		InitialContextFactoryMock.bind("jdbc/oncourse", oncourse);
		InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse", oncourse);

		DataDomain domain = cayenneRuntime.getDataDomain();

		if (createTables) {
			createTablesForDataSourceByParams(oncourse, domain.getDataMap("oncourse"), params);
		}

		for(DataNode dataNode: cayenneRuntime.getDataDomain().getDataNodes()){
			dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType());
		}
	}

	/**
	 * Generates table for the given dataSource.
	 *
	 * @param dataSource
	 * @throws Exception
	 */
	public static void createTablesForDataSourceByParams(DataSource dataSource, DataMap map, Map<String, Boolean> params) throws Exception {
		DataDomain domain = cayenneRuntime.getDataDomain();
		List<Relationship> entityRelationshipsToRemove = new ArrayList<Relationship>();
		entityRelationshipsToRemove.add(map.getDbEntity("EntityRelation").getRelationship("relationToProduct"));
		entityRelationshipsToRemove.add(map.getDbEntity("EntityRelation").getRelationship("relationFromProduct"));
		entityRelationshipsToRemove.add(map.getDbEntity("EntityRelation").getRelationship("relationToCourse"));
		entityRelationshipsToRemove.add(map.getDbEntity("EntityRelation").getRelationship("relationFromCourse"));
		for (Relationship rel : entityRelationshipsToRemove) {
			map.getDbEntity("EntityRelation").removeRelationship(rel.getName());
		}

		DbGenerator generator = new DbGenerator(domain.getDefaultNode().getAdapter(), map, cayenneRuntime.getInjector().getInstance(JdbcEventLogger.class), Collections.<DbEntity> emptyList());
		boolean isParamsEmpty = params == null || params.isEmpty();
		if (isParamsEmpty) {
			generator.setShouldCreateTables(true);
			generator.setShouldCreateFKConstraints(true);
			generator.setShouldCreatePKSupport(true);
		} else {
			generator.setShouldCreateTables(Boolean.TRUE.equals(params.get(SHOULD_CREATE_TABLES)));
			generator.setShouldCreateFKConstraints(Boolean.TRUE.equals(params.get(SHOULD_CREATE_FK_CONSTRAINTS)));
			generator.setShouldCreatePKSupport(Boolean.TRUE.equals(params.get(SHOULD_CREATE_PK_SUPPORT)));
		}

		generator.runGenerator(dataSource);

		for (Relationship rel : entityRelationshipsToRemove) {
			map.getDbEntity("EntityRelation").addRelationship(rel);
		}
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

	/**
	 * Create datasourse to inmemory derby db by the given name.
	 *
	 * @return
	 */
	public static DataSource createDataSource() throws Exception {

		String databaseUri = System.getProperty("testDatabaseUri");
		String driverClass = System.getProperty("testDatabaseDriver");
		createTables = Boolean.valueOf(System.getProperty("testCreateTables"));

		mysql = Mysql.valueOf(databaseUri);

		truncateAllTables();

		if (createSchema) {
			createMysqlSchema(databaseUri);
		}

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(databaseUri);
		dataSource.setMaxActive(100);
		return dataSource;
	}

	private static void createMysqlSchema(String databaseUri) throws SQLException {
		Connection connection = DriverManager.getConnection(mysql.mysqlUri);
		PreparedStatement preparedStatement = connection.prepareStatement(String.format("CREATE SCHEMA %s DEFAULT CHARACTER SET ascii ;", mysql.databaseName));
		preparedStatement.execute();
		connection.close();
	}

	public static void shutdownDataSources() throws Exception {
		cayenneRuntime.shutdown();

		AbandonedConnectionCleanupThread.shutdown();

		if (dropSchema) {
			dropMysqlSchema();
		}
	}

	private static void dropMysqlSchema() throws SQLException {
		Connection connection = DriverManager.getConnection(mysql.mysqlUri);
		PreparedStatement preparedStatement = connection.prepareStatement(String.format("DROP DATABASE %s ;", mysql.databaseName));
		preparedStatement.execute();
		connection.close();
	}

	public static void truncateAllTables() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement  = null;
		Statement statement = null;
		try {
			connection = DriverManager.getConnection(mysql.mysqlUri);
			preparedStatement = connection.prepareStatement(String.format("select Concat(table_schema,'.',TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES where  table_schema = '%s';", mysql.databaseName));
			ResultSet resultSet = preparedStatement.executeQuery();


			statement = connection.createStatement();
			statement.addBatch("SET FOREIGN_KEY_CHECKS=0;");
			while (resultSet.next())
            {
                statement.addBatch(String.format("TRUNCATE TABLE %s;", resultSet.getString(1)));
            }
			statement.addBatch("SET FOREIGN_KEY_CHECKS=1;");
			statement.executeBatch();
		} finally {

			if (statement != null){
				statement.close();
			}
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}

	}

	public static class Mysql {
		String mysqlUri;
		String databaseName;
		String databaseUri;

		public static Mysql valueOf(String databaseUri) {
			Mysql result = new Mysql();
			result.databaseUri = databaseUri;
			String[] parts = StringUtils.split(databaseUri, "?");
			String[] urlParts = StringUtils.split(parts[0], "/");
			result.mysqlUri = urlParts[0] + "//" + urlParts[1] + "?" + parts[1];
			result.databaseName = urlParts[2];
			return result;
		}

	}
}
