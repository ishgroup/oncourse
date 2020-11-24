/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.common.types.EntityRelationCartAction
import ish.oncourse.generator.DataGenerator
import ish.oncourse.server.ICayenneService
import ish.util.AccountUtil
import org.apache.cayenne.ObjectContext

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
        ccr.setRelationType(getRelationType(context))
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
        ccr.setRelationType(getRelationType(context))
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
        ccr.setRelationType(getRelationType(context))
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
    void testCreateCourseProductRelation() {
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
        cpr.setRelationType(getRelationType(context))
        cpr.setFromCourse(course1)
        cpr.setToProduct(p1)

        try {
			context.commitChanges()
        } catch (Exception e) {
			e.printStackTrace()
            fail("the course-product relationship cannot be created.")
        }

		assertEquals("", course1.getToCourses().size(), 0)
        assertEquals("", course1.getFromCourses().size(), 0)
        assertEquals("", course1.getProductToRelations().size(), 1)

        assertTrue(course1.getProductToRelations().get(0).getFromCourse().equalsIgnoreContext(course1))

    }

    @Test
    void testCreateProductCourseRelation() {
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        DataContext context = cayenneService.getNewNonReplicatingContext()

        Course course = context.newObject(Course.class)
        course.setName("Course ToCourse")
        course.setCode("CourseFromProduct")
        course.setFieldConfigurationSchema(DataGenerator.valueOf(context).getFieldConfigurationScheme())

        Tax tax = context.newObject(Tax.class)
        tax.setTaxCode("test")
        tax.setRate(BigDecimal.ZERO)
        tax.setReceivableFromAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(context, Account.class))
        tax.setPayableToAccount(AccountUtil.getDefaultTaxAccount(context, Account.class))

        ArticleProduct product = context.newObject(ArticleProduct.class)
        product.setName("ProductToCourse")
        product.setSku("PtoC")
        product.setTax(tax)

        context.commitChanges()

        ProductCourseRelation prc = context.newObject(ProductCourseRelation.class)
        prc.setRelationType(getRelationType(context))
        prc.setToCourse(course)
        prc.setFromProduct(product)

        try {
            context.commitChanges()
        } catch (Exception e) {
            e.printStackTrace()
            fail("the course-product relationship cannot be created.")
        }

        assertEquals("", course.getToCourses().size(), 0)
        assertEquals("", course.getFromCourses().size(), 0)
        assertEquals("", course.getProductToRelations().size(), 0)
        assertEquals("", course.getProductFromRelations().size(), 1)
        assertEquals("", product.getCourseToRelations().size(), 0)
        assertEquals("", product.getCourseFromRelations().size(), 1)

        assertTrue(course.getProductFromRelations().get(0).getToCourse().equalsIgnoreContext(course))
        assertTrue(course.getProductFromRelations().get(0).getFromProduct().equalsIgnoreContext(product))
        assertTrue(product.getCourseFromRelations().get(0).getToCourse().equalsIgnoreContext(course))
        assertTrue(product.getCourseFromRelations().get(0).getFromProduct().equalsIgnoreContext(product))

    }


    @Test
    void testGettingRelatedEntitiesOfCourseApiMethods() {
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        DataContext context = cayenneService.getNewNonReplicatingContext()
        FieldConfigurationScheme scheme = DataGenerator.valueOf(context).getFieldConfigurationScheme()

        //setup courses
        Course mainCourse = context.newObject(Course.class)
        mainCourse.setName("Main Course")
        mainCourse.setCode("MainCourse")
        mainCourse.setFieldConfigurationSchema(scheme)

        Course toCourse = context.newObject(Course.class)
        toCourse.setName("Course to main course")
        toCourse.setCode("CtoMain")
        toCourse.setFieldConfigurationSchema(scheme)

        CourseCourseRelation fromMainToCourse = context.newObject(CourseCourseRelation.class)
        fromMainToCourse.setRelationType(getRelationType(context))
        fromMainToCourse.setFromCourse(mainCourse)
        fromMainToCourse.setToCourse(toCourse)

        context.commitChanges()

        Course fromCourse = context.newObject(Course.class)
        fromCourse.setName("Course from main course")
        fromCourse.setCode("CfromMain")
        fromCourse.setFieldConfigurationSchema(scheme)

        CourseCourseRelation fromCourseToMain = context.newObject(CourseCourseRelation.class)
        fromCourseToMain.setRelationType(getRelationType(context))
        fromCourseToMain.setFromCourse(fromCourse)
        fromCourseToMain.setToCourse(mainCourse)

        context.commitChanges()

        Tax tax = context.newObject(Tax.class)
        tax.setTaxCode("tax")
        tax.setRate(BigDecimal.ZERO)
        tax.setReceivableFromAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(context, Account.class))
        tax.setPayableToAccount(AccountUtil.getDefaultTaxAccount(context, Account.class))

        ArticleProduct fromProduct = context.newObject(ArticleProduct.class)
        fromProduct.setName("FromProductToMain")
        fromProduct.setSku("PfromMain")
        fromProduct.setTax(tax)

        ProductCourseRelation fromProductToMain = context.newObject(ProductCourseRelation.class)
        fromProductToMain.setRelationType(getRelationType(context))
        fromProductToMain.setToCourse(mainCourse)
        fromProductToMain.setFromProduct(fromProduct)

        context.commitChanges()


        ArticleProduct toProduct = context.newObject(ArticleProduct.class)
        toProduct.setName("ProductToMainCourse")
        toProduct.setSku("PtoMain")
        toProduct.setTax(tax)

        CourseProductRelation fromMainToProduct = context.newObject(CourseProductRelation.class)
        fromMainToProduct.setRelationType(getRelationType(context))
        fromMainToProduct.setFromCourse(mainCourse)
        fromMainToProduct.setToProduct(toProduct)

        context.commitChanges()

        assertEquals(mainCourse.getRelatedCourses().size(), 2)
        assertEquals(mainCourse.getRelatedToCourses().size(), 1)
        assertEquals(mainCourse.getRelatedFromCourses().size(), 1)

        assertEquals(mainCourse.getRelatedProducts().size(), 2)
        assertEquals(mainCourse.getRelatedToProducts().size(), 1)
        assertEquals(mainCourse.getRelatedFromProducts().size(), 1)

        assertEquals(mainCourse.getRelatedCourses("Test relation type").size(), 2)
        assertEquals(mainCourse.getRelatedCourses("Test relation type wrong").size(), 0)
        assertEquals(mainCourse.getRelatedToCourses("Test relation type").size(), 1)
        assertEquals(mainCourse.getRelatedFromCourses("Test relation type").size(), 1)
    }


    private EntityRelationType getRelationType(ObjectContext context) {
        context.newObject(EntityRelationType).with { it ->
            it.name = "Test relation type"
            it.toName = "To name"
            it.fromName = "From name"
            it.shoppingCart = EntityRelationCartAction.NO_ACTION
            it
        }
    }
}
