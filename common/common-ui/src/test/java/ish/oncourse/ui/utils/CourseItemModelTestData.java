/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.utils;

import ish.oncourse.model.Course;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.test.MariaDB;
import ish.oncourse.test.TestContext;
import ish.oncourse.test.context.CCollege;
import ish.oncourse.test.context.CCourse;
import ish.oncourse.test.context.DataContext;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import static io.reactivex.Observable.range;

/**
 * User: akoiro
 * Date: 26/3/18
 */
public class CourseItemModelTestData {
//	private MariaDB source = MariaDB.valueOf("jdbc:mysql://10.100.33.1:3306/w2live_college?useSSL=false&serverTimezone=Australia/Sydney&zeroDateTimeBehavior=CONVERT_TO_NULL",
//			"andrey", "hGh46J*hje23");

	private TestContext testContext;
	private DataContext dataContext;

	@Before
	public void before() {
		testContext = new TestContext().mariaDB(MariaDB.valueOf()).open();

		dataContext = new DataContext().withObjectContext(testContext.getServerRuntime().newContext());
		CCollege college = dataContext.newCollege();
		range(0, 20).blockingForEach((i) -> college.newCourse("from" + i.toString(), "from" + i.toString()));
		range(0, 20).blockingForEach((i) -> college.newCourse("to" + i.toString(), "to" + i.toString()));


		CCourse cCourse = college.newCourse("target", "target");

		college.getCourses().stream()
				.filter(c -> c.getCourse().getCode().startsWith("from"))
				.forEach(cCourse::relatedFrom);

		college.getCourses().stream()
				.filter(c -> c.getCourse().getCode().startsWith("to"))
				.forEach(cCourse::relatedTo);


		dataContext.getObjectContext().commitChanges();

		range(0, 20).blockingForEach((i) -> college.newProduct("product" + i.toString()));

	}

	@Test
	public void test() {
		ObjectContext objectContext = testContext.getServerRuntime().newContext();

		Course course = dataContext.getColleges().get(0).getCourses().stream()
				.filter((c) -> c.getCourse().getCode().equals("target"))
				.findFirst().orElse(null).getCourse();

		CourseItemModel courseItemModel = CourseItemModel.valueOf(objectContext.localObject(course)
				, new SearchParams());

		System.out.println(CourseItemModelDaoHelper.selectRelatedCourses(course));

		System.out.println(CourseItemModelDaoHelper.selectRelatedProducts(course));
	}
}
