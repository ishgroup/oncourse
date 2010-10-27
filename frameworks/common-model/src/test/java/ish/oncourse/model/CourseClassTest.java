/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.model;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author marek
 */
public class CourseClassTest {

    public CourseClassTest() {
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
	 * Test of getIsoStartDate method, of class CourseClass.
	 */
	@Test
	public void testGetIsoStartDate() {
		System.out.println("getIsoStartDate");
		CourseClass instance = new CourseClass();
		instance.setStartDate(new DateTime(2010, 1, 27, 19, 26, 10, 10).toDate());
		String expResult = "20100127";
		String result = instance.getIsoStartDate();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getIsoEndDate method, of class CourseClass.
	 */
	@Test
	public void testGetIsoEndDate() {
		System.out.println("getIsoEndDate");
		CourseClass instance = new CourseClass();
		instance.setEndDate(new DateTime(2010, 1, 30, 19, 26, 10, 10).toDate());
		String expResult = "20100130";
		String result = instance.getIsoEndDate();
		assertEquals(expResult, result);
	}

}