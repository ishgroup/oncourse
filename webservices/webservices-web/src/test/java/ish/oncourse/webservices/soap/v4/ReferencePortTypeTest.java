package ish.oncourse.webservices.soap.v4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import ish.oncourse.test.ContextUtils;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.services.AppModule;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceResult;

import java.io.InputStream;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReferencePortTypeTest extends ServiceTest {

	private ReferencePortType getReferencePort() {
		return getService(ReferencePortType.class);
	}
	

	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", "app", AppModule.class, ReplicationTestModule.class);
		
		ContextUtils.setupDataSources();
		
		InputStream st = ReferencePortTypeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/referenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(getDataSource("jdbc/oncourse_reference").getConnection(), null),
				dataSet);

		st = ReferencePortTypeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/auth/authDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);

		DatabaseOperation.INSERT.execute(new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null), dataSet);
	}
	
	@After
	public void cleanUp() throws Exception {
		cleanDataSource(getDataSource("jdbc/oncourse_reference"));
		cleanDataSource(getDataSource("jdbc/oncourse"));
	}

	@Test
	public void testGetMaximumVersion() throws Exception {
		ReferencePortType referencePort = getReferencePort();

		Long version = referencePort.getMaximumVersion();

		assertNotNull(version);

		assertTrue("Check that max version is 2.", version.equals(2L));
	}

	@Test
	public void testGetRecords() throws Exception {
		ReferencePortType referencePort = getReferencePort();

		ReferenceResult result = referencePort.getRecords(1L);

		assertNotNull(result);
		int size = result.getCountryOrLanguageOrModule().size();
		assertTrue("Test results length. Expect 8 here.", size == 8);
	}
}
