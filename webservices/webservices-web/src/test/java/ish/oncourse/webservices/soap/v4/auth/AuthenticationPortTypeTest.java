package ish.oncourse.webservices.soap.v4.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.test.ContextUtils;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.services.AppModule;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;

import java.io.InputStream;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for all major authentication flows.
 * 
 * @author anton
 * 
 */
public class AuthenticationPortTypeTest extends ServiceTest {
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.webservices.services", "app", AppModule.class, ReplicationTestModule.class);
		ContextUtils.setupDataSources();
	}
	
	public AuthenticationPortType getAuthenticationPort() throws Exception {
		return getService(AuthenticationPortType.class);
	}
	
	@After
	public void cleanUp() throws Exception {
		cleanDataSource(getDataSource("jdbc/oncourse"));
	}

	/**
	 * Tests basic flow of authentication. We pass valid college securityCode,
	 * and lastCommunicationKey here. New generated communication key is
	 * expected as the result.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAuthenticateSuccess() throws Exception {
		InputStream st = getClass().getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/auth/authDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null), dataSet);

		AuthenticationPortType port = getAuthenticationPort();

		long newCommunicationKey = port.authenticate("345ttn44$%9", 7059522699886202880L);

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
		InputStream st = getClass().getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/auth/authDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null), dataSet);

		AuthenticationPortType port = getAuthenticationPort();

		try {
			long newCommKey = port.authenticate("123456", 7059522699886202880L);
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
		InputStream st = getClass().getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/auth/authDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);

		AuthenticationPortType port = getAuthenticationPort();

		try {
			long newCommKey = port.authenticate("345ttn44$%9", 12345L);
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
	 * Test recovering from a HALT state scenario. We should accept any
	 * communication key in this state.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRecoveringFromHaltState() throws Exception {
		InputStream st = getClass().getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/auth/authDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);

		AuthenticationPortType port = getAuthenticationPort();

		long newCommKey = port.authenticate("147ttn44$%9", 705952269988620267L);

		assertTrue("Check communication key.", String.valueOf(newCommKey).length() > 5);

		// Check if college in a HALT state.
		ITable actualData = dbUnitConnection.createQueryTable("College", "select * from College where id = 2");
		String keyStatus = (String) actualData.getValue(0, "communication_key_status");
		assertEquals("Check if college in a VALID state.", KeyStatus.VALID.name(), keyStatus);
	}

	@Test
	public void testLogout() throws Exception {
		InputStream st = getClass().getClassLoader().getResourceAsStream("ish/oncourse/webservices/soap/v4/auth/authDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseConnection dbUnitConnection = new DatabaseConnection(getDataSource("jdbc/oncourse").getConnection(), null);
		DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);

		AuthenticationPortType port = getAuthenticationPort();

		long newCommunicationKey = port.authenticate("345ttn44$%9", 7059522699886202880L);

		long result = port.logout(newCommunicationKey);

		assertEquals("Expecting success logout.", result, 0);
	}
}
