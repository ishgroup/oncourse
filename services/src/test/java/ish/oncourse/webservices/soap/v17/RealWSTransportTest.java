package ish.oncourse.webservices.soap.v17;

import ish.oncourse.model.College;
import ish.oncourse.webservices.soap.TestConstants;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.After;
import org.junit.Before;

import java.util.List;

import static org.junit.Assert.*;

public abstract class RealWSTransportTest implements TestConstants {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEProcessDataset.xml";
	protected V17TestEnv testEnv;

	@Before
	public void before() throws Exception {
		testEnv = new V17TestEnv(DEFAULT_DATASET_XML, null).start();
	}

	@After
	public void after() throws Exception {
		if (testEnv != null) testEnv.shutdown();
	}

	protected Long getCommunicationKey() {
		ObjectContext context = testEnv.getTestEnv().getCayenneService().newNonReplicatingContext();
		List<College> colleges = ObjectSelect.query(College.class)
				.where(ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, 1l))
				.select(context);
		assertFalse("colleges should not be empty", colleges.isEmpty());
		assertTrue("Only 1 college should have id=1", colleges.size() == 1);
		College college = colleges.get(0);
		assertNotNull("Communication key should be not NULL", college.getCommunicationKey());
		return college.getCommunicationKey();
	}

	protected String getSecurityCode() {
		ObjectContext context = testEnv.getTestEnv().getCayenneService().newNonReplicatingContext();
		List<College> colleges = ObjectSelect.query(College.class)
				.where(ExpressionFactory.matchDbExp(College.ID_PK_COLUMN, 1l))
				.select(context);
		assertFalse("colleges should not be empty", colleges.isEmpty());
		assertTrue("Only 1 college should have id=1", colleges.size() == 1);
		College college = colleges.get(0);
		assertNotNull("Security code should be not NULL", college.getWebServicesSecurityCode());
		return college.getWebServicesSecurityCode();
	}
}
