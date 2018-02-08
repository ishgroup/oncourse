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
import liquibase.util.NetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

public class TheLiquibaseServletListener implements ServletContextListener {

	private static final Logger logger = LogManager.getLogger();

	@Inject
	private LiquibaseParams params;

	public TheLiquibaseServletListener() {
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		//start to init the liquibase
		String hostName;
		try {
			hostName = NetUtil.getLocalHost().getHostName();
		} catch (Exception e) {
			servletContextEvent.getServletContext().log("Cannot find hostname: " + e.getMessage());
			return;
		}

		String shouldRunProperty = System.getProperty(Liquibase.SHOULD_RUN_SYSTEM_PROPERTY);
		if (shouldRunProperty != null && !Boolean.valueOf(shouldRunProperty)) {
			LogFactory.getLogger().info(
					"Liquibase did not run on " + hostName + " because '" + Liquibase.SHOULD_RUN_SYSTEM_PROPERTY
							+ "' system property was set to false");
			return;
		}

		if (!isShouldRun(hostName)) {
			servletContextEvent.getServletContext().log(
					"LiquibaseServletListener did not run due to liquibase.host.includes and/or liquibase.host.excludes");
			return;
		}

		validateSrcDest(params);

		for (int i = 0; i < params.getChangeLogFilesParam().size(); i++) {
			update(params.getDataSources().get(i), params.getChangeLogFilesParam().get(i),params.getDefaultSchema(), params.getLiquibaseParameters(), params.getContexts());
		}
	}

	private void validateSrcDest(LiquibaseParams params) throws RuntimeException {

		String errorMessage = null;

		if (params.getChangeLogFilesParam() == null) {
			errorMessage = "Cannot run Liquibase, changelogs is not set";
		}
		if (params.getDataSources() == null) {
			errorMessage = "Cannot run Liquibase, data sources is not set";
		}

		if (params.getChangeLogFilesParam().size() != params.getDataSources().size()) {
			errorMessage = "The number of data sources, should be equal to the number of changeLogFiles";
		}

		if (errorMessage != null) {
			logger.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
	}

	private boolean isShouldRun(String hostName){
		boolean shouldRun = false;
		if (params.getMachineIncludes().isEmpty() && params.getMachineExcludes().isEmpty()) {
			shouldRun = true;
		} else if (!params.getMachineIncludes().isEmpty()) {
			for(String machine : params.getMachineIncludes()) {
				if (hostName.equalsIgnoreCase(machine.trim())) {
					shouldRun = true;
				}
			}
		} else if (!params.getMachineExcludes().isEmpty()) {
			shouldRun = true;
			for (String machine : params.getMachineExcludes()) {
				if (hostName.equalsIgnoreCase(machine.trim())) {
					shouldRun = false;
				}
			}
		}
		return shouldRun;
	}

	private void update(DataSource dataSource, String changeLog, String defaultSchema, Map<String, String> lqParams, String contexts) {
		try {

			Context ic = null;
			Connection connection = null;

			try {
				ic = new InitialContext();

				connection = dataSource.getConnection();

				Thread currentThread = Thread.currentThread();
				ClassLoader contextClassLoader = currentThread.getContextClassLoader();
				ResourceAccessor threadClFO = new ClassLoaderResourceAccessor(contextClassLoader);

				ResourceAccessor clFO = new ClassLoaderResourceAccessor();
				ResourceAccessor fsFO = new FileSystemResourceAccessor();

				Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
				database.setDefaultSchemaName(defaultSchema);
				Liquibase liquibase = new Liquibase(changeLog, new CompositeResourceAccessor(clFO, fsFO, threadClFO), database);

				lqParams.forEach((key, val) -> liquibase.setChangeLogParameter(key, val));

				liquibase.update(contexts);

			} finally {
				if (ic != null) {
					ic.close();
				}
				if (connection != null) {
					connection.close();
				}
			}

		} catch (Exception e) {
			logger.error("Liquibase update failed with error.", e);
			if (params.getFailOnError()) {
				throw new RuntimeException(e);
			}
		}
	}
}
