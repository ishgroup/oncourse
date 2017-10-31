/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.test;

import ish.oncourse.test.functions.Functions;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.configuration.CayenneRuntime;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.log.NoopJdbcEventLogger;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.Relationship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: akoiro
 * Date: 27/8/17
 */
public class CreateTables {
	private static final Logger logger = LogManager.getLogger();

	public static final String SHOULD_CREATE_TABLES = "createTables";
	public static final String SHOULD_CREATE_FK_CONSTRAINTS = "createFKConstraints";
	public static final String SHOULD_CREATE_PK_SUPPORT = "createPKSupport";


	private ServerRuntime serverRuntime;
	private DataMap dataMap;
	private Map<String, Boolean> params;

	private DataDomain domain;
	private List<Relationship> entityRelationshipsToRemove = new ArrayList<>();
	private List<Relationship> customFieldRelationships = new ArrayList<>();


	public CreateTables(ServerRuntime serverRuntime, Map<String, Boolean> params) {
		this.serverRuntime = serverRuntime;
		this.dataMap = serverRuntime.getDataDomain().getDataMap("oncourse");
		this.domain = serverRuntime.getDataDomain();
		this.params = params != null && !params.isEmpty() ? params : Collections.<String, Boolean>emptyMap();
	}

	public void create() {
		try {
			Functions.TimeLog timeLog = new Functions.TimeLog();
			before();
			createGenerator().runGenerator(serverRuntime.getDataSource());
			after();
			timeLog.log(logger, "CreateTables.create() timing:");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void before() {
		entityRelationshipsToRemove.add(dataMap.getDbEntity("EntityRelation").getRelationship("relationToProduct"));
		entityRelationshipsToRemove.add(dataMap.getDbEntity("EntityRelation").getRelationship("relationFromProduct"));
		entityRelationshipsToRemove.add(dataMap.getDbEntity("EntityRelation").getRelationship("relationToCourse"));
		entityRelationshipsToRemove.add(dataMap.getDbEntity("EntityRelation").getRelationship("relationFromCourse"));
		for (Relationship rel : entityRelationshipsToRemove) {
			dataMap.getDbEntity("EntityRelation").removeRelationship(rel.getName());
		}


		customFieldRelationships.add(dataMap.getDbEntity("CustomField").getRelationship("relatedContact"));
		customFieldRelationships.add(dataMap.getDbEntity("CustomField").getRelationship("relatedEnrolment"));
		customFieldRelationships.add(dataMap.getDbEntity("CustomField").getRelationship("relatedCourse"));
		customFieldRelationships.add(dataMap.getDbEntity("CustomField").getRelationship("relatedApplication"));
		customFieldRelationships.add(dataMap.getDbEntity("CustomField").getRelationship("relatedWaitingList"));
		for (Relationship rel : customFieldRelationships) {
			dataMap.getDbEntity("CustomField").removeRelationship(rel.getName());
		}
	}


	private void after() {
		for (Relationship rel : entityRelationshipsToRemove) {
			dataMap.getDbEntity("EntityRelation").addRelationship(rel);
		}

		for (Relationship rel : customFieldRelationships) {
			dataMap.getDbEntity("CustomField").addRelationship(rel);
		}
	}


	private DbGenerator createGenerator() {
		DbGenerator generator = new DbGenerator(domain.getDefaultNode().getAdapter(), dataMap, NoopJdbcEventLogger.getInstance(), Collections.<DbEntity>emptyList());
		boolean isParamsEmpty = params.isEmpty();
		generator.setShouldCreateTables(isParamsEmpty || Boolean.TRUE.equals(params.get(SHOULD_CREATE_TABLES)));
		generator.setShouldCreateFKConstraints(isParamsEmpty || Boolean.TRUE.equals(params.get(SHOULD_CREATE_FK_CONSTRAINTS)));
		generator.setShouldCreatePKSupport(isParamsEmpty || Boolean.TRUE.equals(params.get(SHOULD_CREATE_PK_SUPPORT)));
		return generator;
	}
}
