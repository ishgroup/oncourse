package ish.oncourse.listeners;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.logging.LogFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.sql.Connection;

public class LiquibaseServletListener implements ServletContextListener {

	private static final Logger logger = LogManager.getLogger();
	private final static String CHANGELOG = "liquibase.db.changelog.xml";
	private final static String CONTEXTS = "production";

	private DataSource dataSource;

	public LiquibaseServletListener(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		if (isShouldRun()) {
			update(dataSource, CHANGELOG, CONTEXTS);
		}
	}

	private boolean isShouldRun() {
		boolean res = true;
		String shouldRunProperty = System.getProperty(Liquibase.SHOULD_RUN_SYSTEM_PROPERTY);
		if (shouldRunProperty != null && !Boolean.valueOf(shouldRunProperty)) {
			LogFactory.getLogger().info(
					"Liquibase did not run because '" + Liquibase.SHOULD_RUN_SYSTEM_PROPERTY
							+ "' system property was set to false");
			res = false;
		}
		return res;
	}

	private void update(DataSource dataSource, String changeLog, String contexts) {
		try {
			Context initialContext = null;
			Connection connection = null;
			try {
				initialContext = new InitialContext();

				connection = dataSource.getConnection();

				Thread currentThread = Thread.currentThread();
				ClassLoader contextClassLoader = currentThread.getContextClassLoader();
				ResourceAccessor threadClFO = new ClassLoaderResourceAccessor(contextClassLoader);

				ResourceAccessor clFO = new ClassLoaderResourceAccessor();
				ResourceAccessor fsFO = new FileSystemResourceAccessor();

				Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
				Liquibase liquibase = new Liquibase(changeLog, new CompositeResourceAccessor(clFO, fsFO, threadClFO), database);

				liquibase.update(contexts);
			} finally {
				if (initialContext != null) {
					initialContext.close();
				}
				if (connection != null) {
					connection.close();
				}
			}
		} catch (Exception e) {
			logger.error("Liquibase update failed with error.", e);
			throw new RuntimeException(e);
		}
	}
}
