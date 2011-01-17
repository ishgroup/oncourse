package ish.oncourse.webservices.soap.v4;

import ish.oncourse.webservices.soap.v4.auth.AuthenticationPortTypeTest;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.dba.hsqldb.HSQLDBAdapter;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.MapLoader;
import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.xml.sax.InputSource;

/**
 * Base class for webservices integration unit testing. It starts embedded jetty server and generate database schema in memory HSQLDB.
 * Subclass tests are supposed to insert records to database with a help of DbUnit.
 * 
 * @author anton
 *
 */
public abstract class AbstractWebServiceTest {
	
	private static Server server;

	protected static int PORT = 8888;

	protected static enum Database {
		ONCOURSE("oncourse", "oncourse.map.xml"), ONCOURSE_BINARY("oncourse_binary", "oncourseBinary.map.xml"), ONCOURSE_REFERENCE("oncourse_reference", "oncourseReference.map.xml");

		private Database(String name, String dataMapName) {
			this.name = name;
			this.dataMapName = dataMapName;
		}

		String name;
		String dataMapName;
	};

	protected static Map<Database, DataSource> DATASOURCES;

	/**
	 * Starts jetty server, register datasources and webservice application.
	 * @throws Exception
	 */
	@BeforeClass
	public static void setup() throws Exception {

		server = new Server();
		server.setStopAtShutdown(true);

		DATASOURCES = new HashMap<Database, DataSource>();

		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(PORT);
		connector.setForwarded(true);
		server.addConnector(connector);

		for (Database db : Database.values()) {

			DataSource dataSource = createDataSource(db.name);
			Resource connectionPool = new Resource("jdbc/" + db.name, dataSource);
			DATASOURCES.put(db, dataSource);

			server.addBean(connectionPool);
		}

		WebAppContext webappContext = new WebAppContext();
		webappContext.setContextPath("/services");
		webappContext.setWar(getWar());

		server.setHandler(webappContext);

		server.start();

		createTables();
	}

	private static DataSource createDataSource(String name) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl("jdbc:hsqldb:mem:" + name);
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	/**
	 * Generated dabase schema with a help of Cayenne DbGenerator.
	 * @throws Exception
	 */
	private static void createTables() throws Exception {
		
		DataDomain domain = new DataDomain("test");
		
		Map<DataMap, DataSource> m = new HashMap<DataMap, DataSource>();
		
		for (Database db : Database.values()) {
			DataMap map = loadDataMap(db.dataMapName);
			map.setName(db.dataMapName);
			m.put(map, DATASOURCES.get(db));
			domain.addMap(map);
		}
		
		for (Entry<DataMap, DataSource> e : m.entrySet()) {
			
			DbGenerator generator = new DbGenerator(new HSQLDBAdapter(), e.getKey(), Collections.<DbEntity> emptyList(), domain);
			generator.setShouldCreateTables(true);
			generator.setShouldCreateFKConstraints(true);
			generator.setShouldCreatePKSupport(true);
			generator.setShouldDropTables(true);
			
			generator.runGenerator(e.getValue());
		}
	}

	private static DataMap loadDataMap(String dataMap) throws Exception {
		InputSource in = new InputSource(AuthenticationPortTypeTest.class.getClassLoader().getResourceAsStream(dataMap));
		return new MapLoader().loadDataMap(in);
	}

	private static String getWar() {

		String userDir = System.getProperty("user.dir");

		File root = new File(userDir);
		File src = new File(root, "src");
		File main = new File(src, "main");
		File webapp = new File(main, "webapp");

		try {
			return webapp.toURI().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error building project URL", e);
		}
	}

	@AfterClass
	public static void cleanUp() throws Exception {
		server.stop();
	}
}
