/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.utils;

import ish.oncourse.model.Course;
import ish.oncourse.services.persistence.CayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.TestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.cayenne.query.SelectById.query;
import static org.junit.Assert.assertEquals;

/**
 * User: akoiro
 * Date: 28/3/18
 */
public class CourseItemModelTest {
 	private TestContext testContext;

	@Before
	public void before() {
		testContext = new TestContext().open();
		new LoadDataSet().dataSetFile("ish/oncourse/ui/utils/CourseItemModelTest.xml").load(testContext.getDS());
	}


	@Test
	public void test_selectRelatedCourses() {
		CayenneService cayenneService = new CayenneService(testContext.getServerRuntime(), null, null);
		Course course = query(Course.class, 1L).selectOne(cayenneService.newContext());
		List<Course> courses = course.getRelatedCourses();
		assertEquals(4, courses.size());
	}

	@After
	public void after() {
		testContext.close();
	}


}
