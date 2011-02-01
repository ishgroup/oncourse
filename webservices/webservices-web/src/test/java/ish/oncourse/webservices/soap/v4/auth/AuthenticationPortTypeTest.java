package ish.oncourse.webservices.soap.v4.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.webservices.soap.v4.AbstractWebServiceTest;

import java.io.InputStream;
import java.net.URL;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

/**
 * Unit test for all major authentication flows.
 * 
 * @author anton
 * 
 */
public class AuthenticationPortTypeTest extends AbstractWebServiceTest {
	
	private static final String WSDL_LOCATION = String.format("http://localhost:%s/services/v4/auth?wsdl", PORT);
	
	/**
	 * Tests basic flow of authentication. We pass valid college securityCode,
	 * and lastCommunicationKey here. New generated communication key is
	 * expected as the result.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAuthenticateSuccess() throws Exception {
		InputStream st = getClass().getClassLoader().getResourceAsStream("baseCollegeDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(DATASOURCES.get(Database.ONCOURSE).getConnection(), null), dataSet);

		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(new URL(WSDL_LOCATION));

		Object[] results = client.invoke("authenticate", "345ttn44$%9", 7059522699886202880L);

		assertNotNull(results);
		assertTrue(results.length > 0);

		Long newCommunicationKey = (Long) results[0];

		assertTrue("Check communication key.", String.valueOf(newCommunicationKey).length() > 5);
	}

	/**
	 * Try to authenticate, but use invalid college security code. We expected
	 * services throws exception here.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidSecurityCode() throws Exception {
		InputStream st = getClass().getClassLoader().getResourceAsStream("baseCollegeDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(DATASOURCES.get(Database.ONCOURSE).getConnection(), null), dataSet);

		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(new URL(WSDL_LOCATION));

		try {
			Object[] results = client.invoke("authenticate", "123456", 7059522699886202880L);
			fail("Passed wrong Security code. Failure is expected.");
		} catch (Exception e) {
			assertNotNull(e.getMessage());
		}
	}

	/**
	 * Try to authenticate, but use invalid communicaiton key. We expected
	 * services throws exception here and selected college will be put in a halt
	 * state.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInvalidCommunicationKey() throws Exception {
		InputStream st = getClass().getClassLoader().getResourceAsStream("baseCollegeDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseConnection dbUnitConnection = new DatabaseConnection(DATASOURCES.get(Database.ONCOURSE).getConnection(), null);
		DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);

		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(new URL(WSDL_LOCATION));

		try {
			Object[] results = client.invoke("authenticate", "345ttn44$%9", 12345L);
			fail("Passed wrong communication key. Failure is expected.");
		} catch (Exception e) {
			assertNotNull(e.getMessage());
		}

		// Check if college in a HALT state.
		ITable actualData = dbUnitConnection.createQueryTable("College", "select * from College where id = 1");
		String keyStatus = (String) actualData.getValue(0, "communication_key_status");
		assertEquals("Check if college in a HALT state.", KeyStatus.HALT.name(), keyStatus);
	}
	
	/**
	 * Test recovering from a HALT state scenario. We should accept any communication key in this state.
	 * @throws Exception
	 */
	@Test
	public void testRecoveringFromHaltState() throws Exception {
		InputStream st = getClass().getClassLoader().getResourceAsStream("baseCollegeDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseConnection dbUnitConnection = new DatabaseConnection(DATASOURCES.get(Database.ONCOURSE).getConnection(), null);
		DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);

		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(new URL(WSDL_LOCATION));

		Object[] results = client.invoke("authenticate", "147ttn44$%9", 705952269988620267L);
		
		assertNotNull(results);
		assertTrue(results.length > 0);

		Long newCommunicationKey = (Long) results[0];

		assertTrue("Check communication key.", String.valueOf(newCommunicationKey).length() > 5);

		// Check if college in a HALT state.
		ITable actualData = dbUnitConnection.createQueryTable("College", "select * from College where id = 2");
		String keyStatus = (String) actualData.getValue(0, "communication_key_status");
		assertEquals("Check if college in a VALID state.", KeyStatus.VALID.name(), keyStatus);
	}
}
