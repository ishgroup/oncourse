/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.model;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author marek
 */
public class SessionTest {

    public SessionTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

	/**
	 * Test of getDurationMinutes method, of class Session.
	 */ @Test
	public void testGetDurationMinutesPositive() {
		System.out.println("getDurationMinutes");
		Session instance = new Session();
		instance.setStartDate(new DateTime(2010,1,1,12,0,0,0).toDate());
		instance.setEndDate(new DateTime(2010,1,1,13,0,0,0).toDate());
		Long expResult = 60L;
		Long result = instance.getDurationMinutes();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getDurationMinutes method, of class Session.
	 */ @Test
	public void testGetDurationMinutesNegative() {
		System.out.println("getDurationMinutes");
		Session instance = new Session();
		instance.setStartDate(new DateTime(2010,1,1,13,0,0,0).toDate());
		instance.setEndDate(new DateTime(2010,1,1,12,0,0,0).toDate());
		Long expResult = -60L;
		Long result = instance.getDurationMinutes();
		assertEquals(expResult, result);
	}
}