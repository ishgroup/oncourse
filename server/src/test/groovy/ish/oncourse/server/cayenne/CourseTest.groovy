/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.common.types.EntityRelationCartAction
import ish.oncourse.generator.DataGenerator
import ish.util.AccountUtil
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class CourseTest extends TestWithDatabase {

    /**
     * test create course-course relationship, assigning relationships from inside the relation object
     */
    @Test
    void testCourseCourses1() {
        FieldConfigurationScheme scheme = DataGenerator.valueOf(cayenneContext).getFieldConfigurationScheme()
        //setup courses
        Course course1 = cayenneContext.newObject(Course.class)
        course1.setName("Course 1-1")
        course1.setCode("C11")
        course1.setFieldConfigurationSchema(scheme)
        course1.setFeeHelpClass(Boolean.FALSE)

        Course course2 = cayenneContext.newObject(Course.class)
        course2.setName("Course 1-2")
        course2.setCode("C12")
        course2.setFieldConfigurationSchema(scheme)
        course2.setFeeHelpClass(Boolean.FALSE)

        cayenneContext.commitChanges()

        EntityRelation courseCourseRelation = cayenneContext.newObject(EntityRelation.class)
        courseCourseRelation.setRelationType(getRelationType(cayenneContext))
        courseCourseRelation.setFromEntityAngelId(course1.id)
        courseCourseRelation.setFromEntityIdentifier(Course.simpleName)
        courseCourseRelation.setToEntityAngelId(course2.id)
        courseCourseRelation.setToEntityIdentifier(Course.simpleName)

        try {
            cayenneContext.commitChanges()
        } catch (Exception ignored) {
            Assertions.fail("the course-course relationship cannot be created.")
        }

        //verify the relatioships
        Assertions.assertEquals(course1.relatedToCourses.size(), 1)
        Assertions.assertEquals(course1.relatedFromCourses.size(), 0)


        Assertions.assertEquals(course2.relatedToCourses.size(), 0)
        Assertions.assertEquals(course2.relatedFromCourses.size(), 1)

        Assertions.assertTrue(course1.relatedToCourses[0].equalsIgnoreContext(course2))
        Assertions.assertTrue(course2.relatedFromCourses[0].equalsIgnoreContext(course1))
    }

    @Test
    void testCreateCourseProductRelation() {
        Course course1 = cayenneContext.newObject(Course.class)
        course1.setName("Course 4-1")
        course1.setCode("C41")
        course1.setFieldConfigurationSchema(DataGenerator.valueOf(cayenneContext).getFieldConfigurationScheme())
        course1.setFeeHelpClass(Boolean.FALSE)

        Tax t = cayenneContext.newObject(Tax.class)
        t.setTaxCode("test")
        t.setRate(BigDecimal.ZERO)
        t.setReceivableFromAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(cayenneContext, Account.class))
        t.setPayableToAccount(AccountUtil.getDefaultTaxAccount(cayenneContext, Account.class))

        ArticleProduct product1 = cayenneContext.newObject(ArticleProduct.class)
        product1.setName("Product 1")
        product1.setSku("P1")
        product1.setTax(t)

        cayenneContext.commitChanges()

        EntityRelation courseProductRelation = cayenneContext.newObject(EntityRelation.class)
        courseProductRelation.setRelationType(getRelationType(cayenneContext))
        courseProductRelation.setFromEntityIdentifier(Course.simpleName)
        courseProductRelation.setFromEntityAngelId(course1.id)
        courseProductRelation.setToEntityIdentifier(Product.simpleName)
        courseProductRelation.setToEntityAngelId(product1.id)

        try {
            cayenneContext.commitChanges()
        } catch (Exception ignored) {
            Assertions.fail("the course-product relationship cannot be created.")
        }

        Assertions.assertEquals(course1.relatedToCourses.size(), 0)
        Assertions.assertEquals(course1.relatedFromCourses.size(), 0)
        Assertions.assertEquals(course1.relatedProducts.size(), 1)

        Assertions.assertTrue(course1.relatedToProducts[0].equalsIgnoreContext(product1))

    }

    @Test
    void testCreateProductCourseRelation() {
        Course course = cayenneContext.newObject(Course.class)
        course.setName("Course ToCourse")
        course.setCode("CourseFromProduct")
        course.setFieldConfigurationSchema(DataGenerator.valueOf(cayenneContext).getFieldConfigurationScheme())
        course.setFeeHelpClass(Boolean.FALSE)

        Tax tax = cayenneContext.newObject(Tax.class)
        tax.setTaxCode("test")
        tax.setRate(BigDecimal.ZERO)
        tax.setReceivableFromAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(cayenneContext, Account.class))
        tax.setPayableToAccount(AccountUtil.getDefaultTaxAccount(cayenneContext, Account.class))

        ArticleProduct product = cayenneContext.newObject(ArticleProduct.class)
        product.setName("ProductToCourse")
        product.setSku("PtoC")
        product.setTax(tax)

        cayenneContext.commitChanges()

        EntityRelation productCourseRelation = cayenneContext.newObject(EntityRelation.class)
        productCourseRelation.setRelationType(getRelationType(cayenneContext))
        productCourseRelation.setToEntityIdentifier(Course.simpleName)
        productCourseRelation.setToEntityAngelId(course.id)
        productCourseRelation.setFromEntityIdentifier(Product.simpleName)
        productCourseRelation.setFromEntityAngelId(product.id)

        try {
            cayenneContext.commitChanges()
        } catch (Exception ignored) {
            Assertions.fail("the course-product relationship cannot be created.")
        }

        Assertions.assertEquals(course.relatedCourses.size(), 0)
        Assertions.assertEquals(course.relatedToProducts.size(), 0)
        Assertions.assertEquals(course.relatedFromProducts.size(), 1)
        Assertions.assertEquals(product.relatedCourses.size(), 1)

        Assertions.assertTrue(course.relatedFromProducts[0].equalsIgnoreContext(product))
        Assertions.assertTrue(product.relatedCourses[0].equalsIgnoreContext(course))

    }
    
    @Test
    void testGettingRelatedEntitiesOfCourseApiMethods() {
        FieldConfigurationScheme scheme = DataGenerator.valueOf(cayenneContext).getFieldConfigurationScheme()

        //setup courses
        Course mainCourse = cayenneContext.newObject(Course.class)
        mainCourse.setName("Main Course")
        mainCourse.setCode("MainCourse")
        mainCourse.setFieldConfigurationSchema(scheme)
        mainCourse.setFeeHelpClass(Boolean.FALSE)

        Course toCourse = cayenneContext.newObject(Course.class)
        toCourse.setName("Course to main course")
        toCourse.setCode("CtoMain")
        toCourse.setFieldConfigurationSchema(scheme)
        toCourse.setFeeHelpClass(Boolean.FALSE)

        cayenneContext.commitChanges()

        EntityRelation fromMainToCourse = cayenneContext.newObject(EntityRelation.class)
        fromMainToCourse.setRelationType(getRelationType(cayenneContext))
        fromMainToCourse.setFromEntityIdentifier(Course.simpleName)
        fromMainToCourse.setFromEntityAngelId(mainCourse.id)
        fromMainToCourse.setToEntityIdentifier(Course.simpleName)
        fromMainToCourse.setToEntityAngelId(toCourse.id)


        Course fromCourse = cayenneContext.newObject(Course.class)
        fromCourse.setName("Course from main course")
        fromCourse.setCode("CfromMain")
        fromCourse.setFieldConfigurationSchema(scheme)
        fromCourse.setFeeHelpClass(Boolean.FALSE)

        cayenneContext.commitChanges()

        EntityRelation fromCourseToMain = cayenneContext.newObject(EntityRelation.class)
        fromCourseToMain.setRelationType(getRelationType(cayenneContext))
        fromCourseToMain.setFromEntityIdentifier(Course.simpleName)
        fromCourseToMain.setFromEntityAngelId(fromCourse.id)
        fromCourseToMain.setToEntityIdentifier(Course.simpleName)
        fromCourseToMain.setToEntityAngelId(mainCourse.id)


        Tax tax = cayenneContext.newObject(Tax.class)
        tax.setTaxCode("tax")
        tax.setRate(BigDecimal.ZERO)
        tax.setReceivableFromAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(cayenneContext, Account.class))
        tax.setPayableToAccount(AccountUtil.getDefaultTaxAccount(cayenneContext, Account.class))

        ArticleProduct fromProduct = cayenneContext.newObject(ArticleProduct.class)
        fromProduct.setName("FromProductToMain")
        fromProduct.setSku("PfromMain")
        fromProduct.setTax(tax)

        cayenneContext.commitChanges()

        EntityRelation fromProductToMain = cayenneContext.newObject(EntityRelation.class)
        fromProductToMain.setRelationType(getRelationType(cayenneContext))
        fromProductToMain.setToEntityIdentifier(Course.simpleName)
        fromProductToMain.setFromEntityIdentifier(Product.simpleName)
        fromProductToMain.setToEntityAngelId(mainCourse.id)
        fromProductToMain.setFromEntityAngelId(fromProduct.id)


        ArticleProduct toProduct = cayenneContext.newObject(ArticleProduct.class)
        toProduct.setName("ProductToMainCourse")
        toProduct.setSku("PtoMain")
        toProduct.setTax(tax)

        cayenneContext.commitChanges()

        EntityRelation fromMainToProduct = cayenneContext.newObject(EntityRelation.class)
        fromMainToProduct.setRelationType(getRelationType(cayenneContext))
        fromMainToProduct.setFromEntityIdentifier(Course.simpleName)
        fromMainToProduct.setFromEntityAngelId(mainCourse.id)
        fromMainToProduct.setToEntityIdentifier(Product.simpleName)
        fromMainToProduct.setToEntityAngelId(toProduct.id)

        cayenneContext.commitChanges()

        Assertions.assertEquals(mainCourse.getRelatedCourses().size(), 2)
        Assertions.assertEquals(mainCourse.getRelatedToCourses().size(), 1)
        Assertions.assertEquals(mainCourse.getRelatedFromCourses().size(), 1)

        Assertions.assertEquals(mainCourse.getRelatedProducts().size(), 2)
        Assertions.assertEquals(mainCourse.getRelatedToProducts().size(), 1)
        Assertions.assertEquals(mainCourse.getRelatedFromProducts().size(), 1)

        Assertions.assertEquals(mainCourse.getRelatedCourses("Test relation type").size(), 2)
        Assertions.assertEquals(mainCourse.getRelatedCourses("Test relation type wrong").size(), 0)
        Assertions.assertEquals(mainCourse.getRelatedToCourses("Test relation type").size(), 1)
        Assertions.assertEquals(mainCourse.getRelatedFromCourses("Test relation type").size(), 1)
    }

    private static EntityRelationType getRelationType(ObjectContext context) {
        context.newObject(EntityRelationType).with { it ->
            it.name = "Test relation type"
            it.toName = "To name"
            it.fromName = "From name"
            it.shoppingCart = EntityRelationCartAction.NO_ACTION
            it
        }
    }
}
