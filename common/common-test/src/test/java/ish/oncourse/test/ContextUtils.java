package ish.oncourse.test;

import ish.math.MoneyType;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.log.NoopJdbcEventLogger;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.Relationship;
import org.apache.commons.dbcp.BasicDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The class for {@link InitialContext} setup.
 * 
 * @author ksenia
 * 
 */
public class ContextUtils {

	private static ServerRuntime cayenneRuntime;
	
	public static final String SHOULD_CREATE_TABLES="createTables";
	public static final String SHOULD_CREATE_FK_CONSTRAINTS="createFKConstraints";
	public static final String SHOULD_CREATE_PK_SUPPORT="createPKSupport";

	static {
		cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml");
	}

	public static ObjectContext createObjectContext() {
		return cayenneRuntime.newContext();
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

		// bind the initial context instance, because the JNDIDataSourceFactory
		// looks for it.
		InitialContextFactoryMock.bind("java:comp/env", new InitialContext());

		DataSource oncourse = createDataSource("oncourse");

		InitialContextFactoryMock.bind("jdbc/oncourse", oncourse);
		InitialContextFactoryMock.bind("java:comp/env/jdbc/oncourse", oncourse);

		DataDomain domain = cayenneRuntime.getDataDomain();

		createTablesForDataSourceByParams(oncourse, domain.getDataMap("oncourse"), params);
		for(DataNode dataNode: cayenneRuntime.getDataDomain().getDataNodes()){
			dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType());
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
	 * Generates table for the given dataSource.
	 * 
	 * @param dataSource
	 * @throws Exception
	 */
	private static void createTablesForDataSourceByParams(DataSource dataSource, DataMap map, Map<String, Boolean> params) throws Exception {
		DataDomain domain = cayenneRuntime.getDataDomain();
		List<Relationship> entityRelationshipsToRemove = new ArrayList<Relationship>();
		entityRelationshipsToRemove.add(map.getDbEntity("EntityRelation").getRelationship("relationToProduct"));
		entityRelationshipsToRemove.add(map.getDbEntity("EntityRelation").getRelationship("relationFromProduct"));
		entityRelationshipsToRemove.add(map.getDbEntity("EntityRelation").getRelationship("relationToCourse"));
		entityRelationshipsToRemove.add(map.getDbEntity("EntityRelation").getRelationship("relationFromCourse"));
		for (Relationship rel : entityRelationshipsToRemove) {
			map.getDbEntity("EntityRelation").removeRelationship(rel.getName());
		}

		List<Relationship> customFieldRelationships = new ArrayList<>();
		customFieldRelationships.add(map.getDbEntity("CustomField").getRelationship("relatedContact"));
		customFieldRelationships.add(map.getDbEntity("CustomField").getRelationship("relatedEnrolment"));
		customFieldRelationships.add(map.getDbEntity("CustomField").getRelationship("relatedCourse"));
		for (Relationship rel : customFieldRelationships) {
			map.getDbEntity("CustomField").removeRelationship(rel.getName());
		}

		DbGenerator generator = new DbGenerator(domain.getDefaultNode().getAdapter(), map, NoopJdbcEventLogger.getInstance(), Collections.<DbEntity> emptyList());
		boolean isParamsEmpty = params == null || params.isEmpty();
		generator.setShouldCreateTables(isParamsEmpty || Boolean.TRUE.equals(params.get(SHOULD_CREATE_TABLES)));
		generator.setShouldCreateFKConstraints(isParamsEmpty || Boolean.TRUE.equals(params.get(SHOULD_CREATE_FK_CONSTRAINTS)));
		generator.setShouldCreatePKSupport(isParamsEmpty || Boolean.TRUE.equals(params.get(SHOULD_CREATE_PK_SUPPORT)));

		generator.runGenerator(dataSource);

		for (Relationship rel : entityRelationshipsToRemove) {
			map.getDbEntity("EntityRelation").addRelationship(rel);
		}

		for (Relationship rel : customFieldRelationships) {
			map.getDbEntity("CustomField").addRelationship(rel);
		}
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
        dataSource.setMaxActive(100);
		return dataSource;
	}

    public static DataSource createDeleteDataSource(String name) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        dataSource.setUrl("jdbc:derby:memory:" + name + ";drop=true");
        dataSource.setUsername("");
        dataSource.setPassword("");
        dataSource.setMaxActive(100);
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
