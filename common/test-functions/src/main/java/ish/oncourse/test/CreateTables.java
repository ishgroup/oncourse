/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.test;

import ish.oncourse.test.functions.Functions;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.log.NoopJdbcEventLogger;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.Relationship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: akoiro
 * Date: 27/8/17
 */
public class CreateTables {
	private static final Logger logger = LogManager.getLogger();

	private ServerRuntime serverRuntime;
	private DataMap dataMap;
	private DataDomain domain;
	private List<Relationship> entityRelationshipsToRemove = new ArrayList<>();
	private List<Relationship> customFieldRelationships = new ArrayList<>();


	public CreateTables(ServerRuntime serverRuntime) {
		this.serverRuntime = serverRuntime;
		this.dataMap = serverRuntime.getDataDomain().getDataMap("oncourse");
		this.domain = serverRuntime.getDataDomain();
	}

	public void create() {
		try {
			Functions.TimeLog timeLog = new Functions.TimeLog();
			DbGenerator generator = createGenerator();
			generator.runGenerator(serverRuntime.getDataSource());
			if (generator.getFailures() != null && generator.getFailures().hasFailures()) {
				logger.error(generator.getFailures().toString());
				throw new IllegalArgumentException(generator.getFailures().toString());
			}
			timeLog.log(logger, "CreateTables.create() timing:");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private DbGenerator createGenerator() {
		DbGenerator generator = new DbGenerator(new MariaDbAdapter(domain.getDefaultNode().getAdapter()), dataMap, NoopJdbcEventLogger.getInstance(), Collections.emptyList());
		generator.setShouldCreateTables(true);
		generator.setShouldCreateFKConstraints(true);
		generator.setShouldCreatePKSupport(false);
		return generator;
	}
}
