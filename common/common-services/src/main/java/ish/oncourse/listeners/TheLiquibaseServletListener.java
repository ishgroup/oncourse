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
import liquibase.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Enumeration;

public class TheLiquibaseServletListener implements ServletContextListener {

	private static final Logger logger = LogManager.getLogger();

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

		String machineIncludes = servletContextEvent.getServletContext().getInitParameter("liquibase.host.includes");
		String machineExcludes = servletContextEvent.getServletContext().getInitParameter("liquibase.host.excludes");
		String failOnError = servletContextEvent.getServletContext().getInitParameter("liquibase.onerror.fail");

		boolean shouldRun = false;
		if (machineIncludes == null && machineExcludes == null) {
			shouldRun = true;
		} else if (machineIncludes != null) {
			for (String machine : machineIncludes.split(",")) {
				machine = machine.trim();
				if (hostName.equalsIgnoreCase(machine)) {
					shouldRun = true;
				}
			}
		} else if (machineExcludes != null) {
			shouldRun = true;
			for (String machine : machineExcludes.split(",")) {
				machine = machine.trim();
				if (hostName.equalsIgnoreCase(machine)) {
					shouldRun = false;
				}
			}
		}

		if (!shouldRun) {
			servletContextEvent.getServletContext().log(
					"LiquibaseServletListener did not run due to liquibase.host.includes and/or liquibase.host.excludes");
			return;
		}

		String dataSourcesParam = servletContextEvent.getServletContext().getInitParameter("liquibase.datasource");
		String changeLogFilesParam = servletContextEvent.getServletContext().getInitParameter("liquibase.changelog");
		String contexts = servletContextEvent.getServletContext().getInitParameter("liquibase.contexts");
		String defaultSchema = StringUtils.trimToNull(servletContextEvent.getServletContext().getInitParameter("liquibase.schema.default"));

		if (changeLogFilesParam == null) {
			String errorMessage = "Cannot run Liquibase, liquibase.changelog is not set";
			logger.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		if (dataSourcesParam == null) {
			String errorMessage = "Cannot run Liquibase, liquibase.datasource is not set";
			logger.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}

		String[] changeLogFiles = changeLogFilesParam.split(",");
		String[] dataSources = dataSourcesParam.split(",");

		if (changeLogFiles.length != dataSources.length) {
			String errorMessage = "The number of datasources, should be equal to the number of changeLogFiles";
			logger.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}

		for (int i = 0; i < changeLogFiles.length; i++) {
			String dataSourceName = dataSources[i];
			String changeLogFile = changeLogFiles[i];

			try {

				Context ic = null;
				Connection connection = null;

				try {
					ic = new InitialContext();
					DataSource dataSource = (DataSource) ic.lookup(dataSourceName);

					connection = dataSource.getConnection();

					Thread currentThread = Thread.currentThread();
					ClassLoader contextClassLoader = currentThread.getContextClassLoader();
					ResourceAccessor threadClFO = new ClassLoaderResourceAccessor(contextClassLoader);

					ResourceAccessor clFO = new ClassLoaderResourceAccessor();
					ResourceAccessor fsFO = new FileSystemResourceAccessor();

					Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
					database.setDefaultSchemaName(defaultSchema);
					Liquibase liquibase = new Liquibase(changeLogFile, new CompositeResourceAccessor(clFO, fsFO, threadClFO), database);

					Enumeration<String> initParameters = servletContextEvent.getServletContext().getInitParameterNames();
					while (initParameters.hasMoreElements()) {
						String name = initParameters.nextElement().trim();
						if (name.startsWith("liquibase.parameter.")) {
							liquibase.setChangeLogParameter(name.substring("liquibase.parameter".length()), servletContextEvent
									.getServletContext().getInitParameter(name));
						}
					}

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
				if (!"false".equals(failOnError)) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
