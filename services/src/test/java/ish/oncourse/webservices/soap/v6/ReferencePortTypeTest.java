package ish.oncourse.webservices.soap.v6;

import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.soap.ReplicationTestModule;
import ish.oncourse.webservices.util.GenericReferenceResult;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ReferencePortTypeTest extends ServiceTest {

	private ReferencePortType getReferencePort() {
		return getService(ReferencePortType.class);
	}

	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);
		
		InputStream st = ReferencePortTypeTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v6/auth/authDataSet.xml");
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null), dataSet);
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

		GenericReferenceResult result = referencePort.getRecords(1L);

		assertNotNull(result);
		int size = result.getCountryOrLanguageOrModule().size();
		assertTrue("Test results length. Expect 8 here.", size == 9);
	}
}
