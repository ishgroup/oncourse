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


import com.google.inject.Inject;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.integration.PluginService;
import ish.oncourse.server.license.LicenseService;
import ish.liquibase.NumberedFilesComparator;
import ish.oncourse.server.messaging.ArchivingMessagesService;
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
import org.reflections.util.ClasspathHelper;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class SchemaUpdateService {

	private static final Logger logger = LogManager.getLogger();



	private static final String DATA_UPGRADE = "data/upgrades.yml";
	private static final String RESOURCES_PATH = "database";

	private final ICayenneService cayenneService;

	public static ICayenneService sharedCayenneService;
	public static ArchivingMessagesService archivingMessagesService;

	@Inject
	public SchemaUpdateService(ICayenneService cayenneService, ArchivingMessagesService archivingMessagesService) {
		this.cayenneService = cayenneService;
		SchemaUpdateService.archivingMessagesService = archivingMessagesService;
	}

	/**
	 * Scan /database directory for *.yml liquibase files
	 * Looking inside main jar + all classpath jars  (replication plugin has own liquibase to create specific queue tables)
	 * 
	 * orger all found files by version number in file name (if present)
	 * all non numbered files put at the end 
	 * The result list looks like:
	 * 
	 * database/01.initial.schema.yml
	 * database/45.upgrade.yml
	 * ....
	 * database/105.upgrade.yml
	 * database/quartz.yml
	 * database/replication.yml
	 * 
	 * apply changes from each file in order, one by one. 
	 * 
	 * Please create separate file per version, use appropriate changeset ids 105-1 to prevent mess
	 * 
	 * @throws SQLException
	 * @throws DatabaseException
	 */
	
	public void updateSchema() throws SQLException, DatabaseException {

		List<String> yamlFiles = PluginService.getPluggableResources(RESOURCES_PATH, ".*\\.yml")
				.stream()
				.sorted( new NumberedFilesComparator())
				.collect(Collectors.toList());

		logger.warn("Count of files from packages: " + yamlFiles.size());
		yamlFiles.forEach(file -> logger.warn("FileName: " + file));

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

	public void upgradeData() throws SQLException, DatabaseException {
		sharedCayenneService = cayenneService;

		final var connection = cayenneService.getDataSource()
				.getConnection();

		applyChangeLog(connection, DATA_UPGRADE);

		sharedCayenneService = null;
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
