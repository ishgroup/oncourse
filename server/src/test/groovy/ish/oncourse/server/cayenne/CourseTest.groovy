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
        course1.setFeeHelpClass(Boolean.FALSE)

        Course course2 = context.newObject(Course.class)
        course2.setName("Course 1-2")
        course2.setCode("C12")
        course2.setFieldConfigurationSchema(scheme)
        course2.setFeeHelpClass(Boolean.FALSE)

        context.commitChanges()

        EntityRelation courseCourseRelation = context.newObject(EntityRelation.class)
        courseCourseRelation.setRelationType(getRelationType(context))
        courseCourseRelation.setFromEntityAngelId(course1.id)
        courseCourseRelation.setFromEntityIdentifier(Course.simpleName)
        courseCourseRelation.setToEntityAngelId(course2.id)
        courseCourseRelation.setToEntityIdentifier(Course.simpleName)

        try {
			context.commitChanges()
        } catch (Exception e) {
            fail("the course-course relationship cannot be created.")
        }

		//verify the relatioships
		assertEquals(course1.relatedToCourses.size(), 1)
        assertEquals(course1.relatedFromCourses.size(), 0)


        assertEquals(course2.relatedToCourses.size(), 0)
        assertEquals(course2.relatedFromCourses.size(), 1)

        assertTrue(course1.relatedToCourses[0].equalsIgnoreContext(course2))
        assertTrue(course2.relatedFromCourses[0].equalsIgnoreContext(course1))

    }

	@Test
    void testCreateCourseProductRelation() {
		ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        DataContext context = cayenneService.getNewNonReplicatingContext()

        Course course1 = context.newObject(Course.class)
        course1.setName("Course 4-1")
        course1.setCode("C41")
        course1.setFieldConfigurationSchema(DataGenerator.valueOf(context).getFieldConfigurationScheme())
        course1.setFeeHelpClass(Boolean.FALSE)

        Tax t = context.newObject(Tax.class)
        t.setTaxCode("test")
        t.setRate(BigDecimal.ZERO)
        t.setReceivableFromAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(context, Account.class))
        t.setPayableToAccount(AccountUtil.getDefaultTaxAccount(context, Account.class))

        ArticleProduct product1 = context.newObject(ArticleProduct.class)
        product1.setName("Product 1")
        product1.setSku("P1")
        product1.setTax(t)

        context.commitChanges()

        EntityRelation courseProductRelation = context.newObject(EntityRelation.class)
        courseProductRelation.setRelationType(getRelationType(context))
        courseProductRelation.setFromEntityIdentifier(Course.simpleName)
        courseProductRelation.setFromEntityAngelId(course1.id)
        courseProductRelation.setToEntityIdentifier(Product.simpleName)
        courseProductRelation.setToEntityAngelId(product1.id)

        try {
			context.commitChanges()
        } catch (Exception e) {
            fail("the course-product relationship cannot be created.")
        }

		assertEquals(course1.relatedToCourses.size(), 0)
        assertEquals(course1.relatedFromCourses.size(), 0)
        assertEquals(course1.relatedProducts.size(), 1)

        assertTrue(course1.relatedToProducts[0].equalsIgnoreContext(product1))

    }

    @Test
    void testCreateProductCourseRelation() {
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)
        DataContext context = cayenneService.getNewNonReplicatingContext()

        Course course = context.newObject(Course.class)
        course.setName("Course ToCourse")
        course.setCode("CourseFromProduct")
        course.setFieldConfigurationSchema(DataGenerator.valueOf(context).getFieldConfigurationScheme())
        course.setFeeHelpClass(Boolean.FALSE)

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

        EntityRelation productCourseRelation = context.newObject(EntityRelation.class)
        productCourseRelation.setRelationType(getRelationType(context))
        productCourseRelation.setToEntityIdentifier(Course.simpleName)
        productCourseRelation.setToEntityAngelId(course.id)
        productCourseRelation.setFromEntityIdentifier(Product.simpleName)
        productCourseRelation.setFromEntityAngelId(product.id)

        try {
            context.commitChanges()
        } catch (Exception e) {
            fail("the course-product relationship cannot be created.")
        }

        assertEquals(course.relatedCourses.size(), 0)
        assertEquals(course.relatedToProducts.size(), 0)
        assertEquals(course.relatedFromProducts.size(), 1)
        assertEquals(product.relatedCourses.size(), 1)

        assertTrue(course.relatedFromProducts[0].equalsIgnoreContext(product))
        assertTrue(product.relatedCourses[0].equalsIgnoreContext(course))

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
        mainCourse.setFeeHelpClass(Boolean.FALSE)

        Course toCourse = context.newObject(Course.class)
        toCourse.setName("Course to main course")
        toCourse.setCode("CtoMain")
        toCourse.setFieldConfigurationSchema(scheme)
        toCourse.setFeeHelpClass(Boolean.FALSE)

        context.commitChanges()

        EntityRelation fromMainToCourse = context.newObject(EntityRelation.class)
        fromMainToCourse.setRelationType(getRelationType(context))
        fromMainToCourse.setFromEntityIdentifier(Course.simpleName)
        fromMainToCourse.setFromEntityAngelId(mainCourse.id)
        fromMainToCourse.setToEntityIdentifier(Course.simpleName)
        fromMainToCourse.setToEntityAngelId(toCourse.id)



        Course fromCourse = context.newObject(Course.class)
        fromCourse.setName("Course from main course")
        fromCourse.setCode("CfromMain")
        fromCourse.setFieldConfigurationSchema(scheme)
        fromCourse.setFeeHelpClass(Boolean.FALSE)

        context.commitChanges()

        EntityRelation fromCourseToMain = context.newObject(EntityRelation.class)
        fromCourseToMain.setRelationType(getRelationType(context))
        fromCourseToMain.setFromEntityIdentifier(Course.simpleName)
        fromCourseToMain.setFromEntityAngelId(fromCourse.id)
        fromCourseToMain.setToEntityIdentifier(Course.simpleName)
        fromCourseToMain.setToEntityAngelId(mainCourse.id)


        Tax tax = context.newObject(Tax.class)
        tax.setTaxCode("tax")
        tax.setRate(BigDecimal.ZERO)
        tax.setReceivableFromAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(context, Account.class))
        tax.setPayableToAccount(AccountUtil.getDefaultTaxAccount(context, Account.class))

        ArticleProduct fromProduct = context.newObject(ArticleProduct.class)
        fromProduct.setName("FromProductToMain")
        fromProduct.setSku("PfromMain")
        fromProduct.setTax(tax)

        context.commitChanges()

        EntityRelation fromProductToMain = context.newObject(EntityRelation.class)
        fromProductToMain.setRelationType(getRelationType(context))
        fromProductToMain.setToEntityIdentifier(Course.simpleName)
        fromProductToMain.setFromEntityIdentifier(Product.simpleName)
        fromProductToMain.setToEntityAngelId(mainCourse.id)
        fromProductToMain.setFromEntityAngelId(fromProduct.id)


        ArticleProduct toProduct = context.newObject(ArticleProduct.class)
        toProduct.setName("ProductToMainCourse")
        toProduct.setSku("PtoMain")
        toProduct.setTax(tax)

        context.commitChanges()

        EntityRelation fromMainToProduct = context.newObject(EntityRelation.class)
        fromMainToProduct.setRelationType(getRelationType(context))
        fromMainToProduct.setFromEntityIdentifier(Course.simpleName)
        fromMainToProduct.setFromEntityAngelId(mainCourse.id)
        fromMainToProduct.setToEntityIdentifier(Product.simpleName)
        fromMainToProduct.setToEntityAngelId(toProduct.id)

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
