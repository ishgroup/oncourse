/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.model;

import ish.oncourse.test.TestContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.validation.ValidationException;
import org.junit.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author marek
 */
public class SessionTest {
	private TestContext testContext = new TestContext();


	/**
	 * Entity to test.
	 */
	private Session session;

	private ObjectContext context;

	public SessionTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		testContext.open();
		context = testContext.getServerRuntime().newContext();

	}

	@After
	public void tearDown() {
		testContext.close();
	}

	/**
	 * Test of getDurationMinutes method, of class Session.
	 */
	@Test
	public void testGetDurationMinutesPositive() {
		System.out.println("getDurationMinutes");
		Session instance = new Session();
		instance.setStartDate(dateFrom(LocalDateTime.of(2010, 1, 1, 12, 0, 0, 0)));
		instance.setEndDate(dateFrom(LocalDateTime.of(2010, 1, 1, 13, 0, 0, 0)));
		Long expResult = 60L;
		Long result = instance.getDurationMinutes();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getDurationMinutes method, of class Session.
	 */
	@Test
	public void testGetDurationMinutesNegative() {
		System.out.println("getDurationMinutes");
		Session instance = new Session();
		instance.setStartDate(dateFrom(LocalDateTime.of(2010, 1, 1, 13, 0, 0, 0)));
		instance.setEndDate(dateFrom(LocalDateTime.of(2010, 1, 1, 12, 0, 0, 0)));
		Long expResult = -60L;
		Long result = instance.getDurationMinutes();
		assertEquals(expResult, result);
	}

	@Test
	public void testRequiredCollegeId() throws Exception {
		session = context.newObject(Session.class);
		assertNull("College status should be null before test", session.getCollege());

		boolean invalid = false;
		try {
			context.commitChanges();
		} catch (ValidationException ve) {
			invalid = true;
		}
		assertTrue("commit should be in failure status", invalid);

	}

	private static Date dateFrom(LocalDateTime localDateTime) {
		return Date.from(ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).toInstant());
	}

}