/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.util.GenericReplicationStub;
import org.apache.cayenne.Cayenne;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;

import javax.sql.DataSource;
import java.io.InputStream;

public abstract class AbstractAllStubBuildersTest extends ServiceTest {
	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);
		initOncourseDataSet();
	}

	private void initOncourseDataSet() throws Exception {
		InputStream st = WillowStubBuilderTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/builders/oncourseDataSet.xml");
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
		dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
	}

	protected <E extends Queueable, S extends GenericReplicationStub> void testStubBuilder(Class<E> entityClass,
																						 AbstractWillowStubBuilder<E, S> stubBuilder, Long entityId, String... excludeProperty) {
		ICayenneService cayenneService = getService(ICayenneService.class);
		E entity = Cayenne.objectForPK(cayenneService.sharedContext(), entityClass, entityId);
		StubBuilderTestHelper<E, S> stubBuilderTestHelper = new StubBuilderTestHelper<>(entity,excludeProperty);
		stubBuilderTestHelper.assertStubBuilder(stubBuilder);
	}

	protected <E extends Queueable, S extends GenericReplicationStub> void testStubBuilder(Class<E> entityClass,
																						 AbstractWillowStubBuilder<E, S> stubBuilder, String... excludeProperty) {
		this.testStubBuilder(entityClass,stubBuilder, 1l, excludeProperty);
	}



}
