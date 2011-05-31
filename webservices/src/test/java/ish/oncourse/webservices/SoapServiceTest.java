package ish.oncourse.webservices;

import ish.oncourse.test.ContextUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.sql.DataSource;

import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Base class for webservices integration unit testing. It starts embedded jetty
 * server and generate database schema in memory HSQLDB. Subclass tests are
 * supposed to insert records to database with a help of DbUnit.
 * 
 * @author anton
 * 
 */
public abstract class SoapServiceTest {

	private static Server server;

	protected static int PORT = 8888;

	private static Map<String, DataSource> dataSources = new HashMap<String, DataSource>();

	/**
	 * Starts jetty server, register datasources and webservice application.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setup() throws Exception {

		// Log.setLog(null);
		server = new Server();
		server.setStopAtShutdown(true);
			
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.eclipse.jetty.jndi.InitialContextFactory");
		
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(PORT);
		connector.setForwarded(true);
		server.addConnector(connector);

		DataSource oncourse = ContextUtils.createDataSource("oncourse");
		DataSource oncourseBinary = ContextUtils.createDataSource("oncourse_binary");
		DataSource oncourseReference = ContextUtils.createDataSource("oncourse_reference");

		dataSources.put("jdbc/oncourse", oncourse);
		dataSources.put("jdbc/oncourse_binary", oncourseBinary);
		dataSources.put("jdbc/oncourse_reference", oncourseReference);

		server.addBean(new Resource("jdbc/oncourse", oncourse));
		server.addBean(new Resource("jdbc/oncourse_binary", oncourseBinary));
		server.addBean(new Resource("jdbc/oncourse_reference", oncourseReference));

		ContextUtils.createOnCourseTables(oncourse);
		ContextUtils.createOnCourseBinaryTables(oncourseBinary);
		ContextUtils.createOnCourseReferenceTables(oncourseReference);

		WebAppContext webappContext = new WebAppContext();
		webappContext.setContextPath("/services");
		webappContext.setWar(getWar());

		server.setHandler(webappContext);

		server.start();
	}

	protected static DataSource getDataSource(String location) throws Exception {
		return dataSources.get(location);
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
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "");
	}
}
