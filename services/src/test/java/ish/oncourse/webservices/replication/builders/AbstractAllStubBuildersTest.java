/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.functions.Functions;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.webservices.util.GenericReplicationStub;
import org.apache.cayenne.Cayenne;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractAllStubBuildersTest extends ServiceTest {
	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/webservices/replication/builders/oncourseDataSet.xml").load(testContext.getDS());
	}

	protected <E extends Queueable, S extends GenericReplicationStub> S testStubBuilder(Class<E> entityClass,
																						AbstractWillowStubBuilder<E, S> stubBuilder, Long entityId, String... excludeProperty) {
		ICayenneService cayenneService = getService(ICayenneService.class);
		E entity = Cayenne.objectForPK(cayenneService.newContext(), entityClass, entityId);
		StubBuilderTestHelper<E, S> stubBuilderTestHelper = new StubBuilderTestHelper<>(entity, excludeProperty);
		return stubBuilderTestHelper.assertStubBuilder(stubBuilder);
	}

	protected <E extends Queueable, S extends GenericReplicationStub> S testStubBuilder(Class<E> entityClass,
																						AbstractWillowStubBuilder<E, S> stubBuilder, String... excludeProperty) {
		return this.testStubBuilder(entityClass, stubBuilder, 1l, excludeProperty);
	}

	@After
	public void cleanup() throws Exception {
		Functions.setForeignKeyChecks(true, testContext.getMariaDB());
		super.cleanup();
	}
}
