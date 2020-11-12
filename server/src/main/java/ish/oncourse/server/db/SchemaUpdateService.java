/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.db;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.integration.Plugin;
import ish.oncourse.server.integration.PluginService;
import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.executor.ExecutorService;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ish.oncourse.server.db.ISHDerbyFunctions.removeReserverWords;

public class SchemaUpdateService {

	private static Logger logger = LogManager.getLogger();

	private static final String PLUGINS_PATH = "plugins";
	private static final String RESOURCES_PATH = "database";
	private static final String RESOURCES_FILTER = "database.yml";

	private final ICayenneService cayenneService;

	public static ICayenneService sharedCayenneService;

	@Inject
	public SchemaUpdateService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService;
	}

	public void run() throws IOException, SQLException, DatabaseException {

		List<String> yamlFiles = PluginService.getPluggableResources(RESOURCES_PATH, ".*\\.yml")
				.stream()
				.sorted()
				.collect(Collectors.toList());

		logger.warn("Count of files from packages: " + yamlFiles.size());
		yamlFiles.forEach(file -> {
			logger.warn("FileName: " + file);
		});

		if (yamlFiles.size() == 0) {
			throw new IllegalArgumentException("Liquibase files are required for creating/updating schema!");
		}

		for (String liquibase: yamlFiles) {
			final var connection = cayenneService.getDataSource()
					.getConnection();

			applyChangeLog(connection, liquibase);
		}

		logger.warn("Schema was created. Data is up to date.");
	}

	private void applyChangeLog(Connection c, String changeLog) throws DatabaseException {
        Liquibase liquibase = null;
        try {
            liquibase = createLiquibase(c, changeLog);
            liquibase.update(StringUtils.EMPTY);
        } catch (LiquibaseException e) {
            throw new DatabaseException(e);
        } finally {
            if (liquibase != null) {
                try {
                    liquibase.forceReleaseLocks();
                } catch (LiquibaseException e) {
					logger.catching(e);
                }
            }
            if (c != null) {
                try {
                    c.rollback();
                    c.close();
                } catch (SQLException e) {
                    // nothing to do
                }
            }
        }
    }

	/**
	 * @param c Connection
	 * @return Liquibase object
	 * @throws LiquibaseException
	 */
	private Liquibase createLiquibase(Connection c, String changeLog) throws LiquibaseException {

		if (c == null) {
			throw new IllegalArgumentException("connection is required!");
		}

		var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
        removeReserverWords(database);
		Scope.getCurrentScope().getSingleton(ExecutorService.class).clearExecutor("jdbc",  database);
		database.resetInternalState();

		File dirOfChangeLog;
		URL urlToFile = ClasspathHelper.forResource(changeLog).iterator().next();
		if (urlToFile.toExternalForm().startsWith("jar:")) {
			String fromJar = urlToFile.toExternalForm().substring(urlToFile.toExternalForm().indexOf(":")+1, urlToFile.toExternalForm().indexOf("!"));
			fromJar = fromJar.substring(fromJar.indexOf(":")+1);
			dirOfChangeLog = new File(fromJar);
		} else {
			dirOfChangeLog = new File(urlToFile.getFile());
		}
		logger.warn(dirOfChangeLog.toString());
		return new Liquibase(changeLog, new FileSystemResourceAccessor(dirOfChangeLog), database);
	}
}
