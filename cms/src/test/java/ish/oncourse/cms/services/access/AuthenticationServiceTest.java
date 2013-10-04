/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cms.services.access;

import static org.junit.Assert.*;

import ish.oncourse.cms.services.CmsTestModule;
import ish.oncourse.model.SystemUser;
import ish.oncourse.services.access.AuthenticationStatus;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.security.AuthenticationUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

public class AuthenticationServiceTest extends ServiceTest {

	private ICayenneService cayenneService;

	private IAuthenticationService authenticationService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.cms", "cms", CmsTestModule.class);
		InputStream st = AuthenticationServiceTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/cms/services/authenticationTestDataSet.xml");

		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		this.cayenneService = getService(ICayenneService.class);
		this.authenticationService = getService(IAuthenticationService.class);
	}

	@Test
	public void testNoSuchUser() throws Exception {
		AuthenticationStatus status = authenticationService.authenticate("nonexisting", "somepassword");
		assertEquals(AuthenticationStatus.NO_MATCHING_USER, status);
	}

	@Test
	public void testAuthEmail() throws Exception {

		// wrong password
		AuthenticationStatus statusFail = authenticationService.authenticate("test@test.test", "wrong");
		assertEquals(AuthenticationStatus.INVALID_CREDENTIALS, statusFail);

		// correct password
		AuthenticationStatus statusSuccess = authenticationService.authenticate("test@test.test", "password");
		assertEquals(AuthenticationStatus.SUCCESS, statusSuccess);
	}

	@Test
	public void testAuthLogin() throws Exception {
		// wrong password
		AuthenticationStatus statusFail = authenticationService.authenticate("test", "wrong");
		assertEquals(AuthenticationStatus.INVALID_CREDENTIALS, statusFail);

		// correct password
		AuthenticationStatus statusSuccess = authenticationService.authenticate("test", "password");
		assertEquals(AuthenticationStatus.SUCCESS, statusSuccess);
	}

	@Test
	public void testDuplicateEmail() throws Exception {
		AuthenticationStatus status = authenticationService.authenticate("dup@dup.dup", "password");
		assertEquals(AuthenticationStatus.MORE_THAN_ONE_USER, status);
	}

	@Test
	public void testOldHashReplacement() throws Exception {
		ObjectContext context = cayenneService.newContext();

		SystemUser user = Cayenne.objectForPK(context, SystemUser.class, 1);

		// check if old hash for "password" is in place
		assertEquals("5baa61e4c9b93f3f68225b6cf8331b7ee68fd8", user.getPassword());

		// try wrong password
		AuthenticationStatus status = authenticationService.authenticate(user.getLogin(), "wrong");

		assertEquals(AuthenticationStatus.INVALID_CREDENTIALS, status);
		// check if old hash for "password" is still in place
		assertEquals("5baa61e4c9b93f3f68225b6cf8331b7ee68fd8", user.getPassword());

		// try correct password
		status = authenticationService.authenticate(user.getLogin(), "password");

		assertEquals(AuthenticationStatus.SUCCESS, status);

		// check that hash is in a new format
		assertTrue(AuthenticationUtil.isValidPasswordHash(user.getPassword()));

		// check that user is still able to log in with the same password
		status = authenticationService.authenticate(user.getLogin(), "password");
		assertEquals(AuthenticationStatus.SUCCESS, status);
	}
}
