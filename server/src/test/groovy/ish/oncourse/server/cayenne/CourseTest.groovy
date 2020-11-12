/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.oncourse.generator.DataGenerator
import ish.oncourse.server.ICayenneService
import ish.util.AccountUtil
import static junit.framework.Assert.assertTrue
import static junit.framework.Assert.fail
import static junit.framework.TestCase.assertEquals
import org.apache.cayenne.access.DataContext
import org.junit.Test

class CourseTest extends CayenneIshTestCase {

	/**
	 * test create course-course relationship, assigning relationships from inside the relation object
	 */
	@Test
    void testCourseCourses1() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        DataContext context = cayenneService.getNewNonReplicatingContext()

        FieldConfigurationScheme scheme = DataGenerator.valueOf(context).getFieldConfigurationScheme()
        //setup courses
		Course course1 = context.newObject(Course.class)
        course1.setName("Course 1-1")
        course1.setCode("C11")
        course1.setFieldConfigurationSchema(scheme)

        Course course2 = context.newObject(Course.class)
        course2.setName("Course 1-2")
        course2.setCode("C12")
        course2.setFieldConfigurationSchema(scheme)

        context.commitChanges()

        CourseCourseRelation ccr = context.newObject(CourseCourseRelation.class)
        ccr.setFromCourse(course1)
        ccr.setToCourse(course2)

        try {
			context.commitChanges()
        } catch (Exception e) {
			e.printStackTrace()
            fail("the course-course relationship cannot be created.")
        }

		//verify the relatioships
		assertEquals("", course1.getToCourses().size(), 1)
        assertEquals("", course1.getFromCourses().size(), 0)


        assertEquals("", course2.getToCourses().size(), 0)
        assertEquals("", course2.getFromCourses().size(), 1)

        assertTrue(course1.getToCourses().get(0).getFromCourse().equalsIgnoreContext(course1))
        assertTrue(course1.getToCourses().get(0).getToCourse().equalsIgnoreContext(course2))

    }

	/**
	 * test create course-course relationship, assigning one of the relationships from inside the relation object
	 */
	@Test
    void testCourseCourses2() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        DataContext context = cayenneService.getNewNonReplicatingContext()

        FieldConfigurationScheme scheme = DataGenerator.valueOf(context).getFieldConfigurationScheme()

        //setup courses
		Course course1 = context.newObject(Course.class)
        course1.setName("Course 2-1")
        course1.setCode("C21")
        course1.setFieldConfigurationSchema(scheme)

        Course course2 = context.newObject(Course.class)
        course2.setName("Course 2-2")
        course2.setCode("C22")
        course2.setFieldConfigurationSchema(scheme)

        context.commitChanges()

        CourseCourseRelation ccr = context.newObject(CourseCourseRelation.class)
        ccr.setToCourse(course2)
        course1.addToToCourses(ccr)

        try {
			context.commitChanges()
        } catch (Exception e) {
			e.printStackTrace()
            fail("the course-course relationship cannot be created.")
        }

		//verify the relatioships
		assertEquals("", course1.getToCourses().size(), 1)
        assertEquals("", course1.getFromCourses().size(), 0)

        assertEquals("", course2.getToCourses().size(), 0)
        assertEquals("", course2.getFromCourses().size(), 1)

        assertTrue(course1.getToCourses().get(0).getFromCourse().equalsIgnoreContext(course1))
        assertTrue(course1.getToCourses().get(0).getToCourse().equalsIgnoreContext(course2))

    }

	/**
	 * test create course-course relationship, assigning both relationships from outside the relation object
	 */
	@Test
    void testCourseCourses3() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        DataContext context = cayenneService.getNewNonReplicatingContext()

        FieldConfigurationScheme scheme = DataGenerator.valueOf(context).getFieldConfigurationScheme()

        //setup courses
		Course course1 = context.newObject(Course.class)
        course1.setName("Course 3-1")
        course1.setCode("C31")
        course1.setFieldConfigurationSchema(scheme)

        Course course2 = context.newObject(Course.class)
        course2.setName("Course 3-2")
        course2.setCode("C32")
        course2.setFieldConfigurationSchema(scheme)

        context.commitChanges()

        CourseCourseRelation ccr = context.newObject(CourseCourseRelation.class)
        course1.addToToCourses(ccr)
        course2.addToFromCourses(ccr)

        try {
			context.commitChanges()
        } catch (Exception e) {
			e.printStackTrace()
            fail("the course-course relationship cannot be created.")
        }

		//verify the relatioships
		assertEquals("", course1.getToCourses().size(), 1)
        assertEquals("", course1.getFromCourses().size(), 0)

        assertEquals("", course2.getToCourses().size(), 0)
        assertEquals("", course2.getFromCourses().size(), 1)

        assertTrue(course1.getToCourses().get(0).getFromCourse().equalsIgnoreContext(course1))
        assertTrue(course1.getToCourses().get(0).getToCourse().equalsIgnoreContext(course2))

    }

	@Test
    void testCourseProductRelations() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        DataContext context = cayenneService.getNewNonReplicatingContext()

        Course course1 = context.newObject(Course.class)
        course1.setName("Course 4-1")
        course1.setCode("C41")
        course1.setFieldConfigurationSchema(DataGenerator.valueOf(context).getFieldConfigurationScheme())

        Tax t = context.newObject(Tax.class)
        t.setTaxCode("test")
        t.setRate(BigDecimal.ZERO)
        t.setReceivableFromAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(context, Account.class))
        t.setPayableToAccount(AccountUtil.getDefaultTaxAccount(context, Account.class))

        ArticleProduct p1 = context.newObject(ArticleProduct.class)
        p1.setName("Product 1")
        p1.setSku("P1")
        p1.setTax(t)

        context.commitChanges()

        CourseProductRelation cpr = context.newObject(CourseProductRelation.class)
        cpr.setCourse(course1)
        cpr.setProduct(p1)

        try {
			context.commitChanges()
        } catch (Exception e) {
			e.printStackTrace()
            fail("the course-product relationship cannot be created.")
        }

		assertEquals("", course1.getToCourses().size(), 0)
        assertEquals("", course1.getFromCourses().size(), 0)
        assertEquals("", course1.getProductRelations().size(), 1)

        assertTrue(course1.getProductRelations().get(0).getCourse().equalsIgnoreContext(course1))

    }
}
