package ish.oncourse.server.duplicate

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.*
import ish.duplicate.CourseDuplicationRequest
import ish.math.Money
import ish.oncourse.generator.DataGenerator
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class DuplicateCourseServiceTest extends CayenneIshTestCase {

    @Test
    void testDuplicateCourseCode() throws Exception {
        FieldConfigurationScheme scheme = DataGenerator.valueOf(cayenneContext).getFieldConfigurationScheme()

        Course relatedCourse = cayenneContext.newObject(Course.class)
        relatedCourse.setCode("Nothing")
        relatedCourse.setName("Name")
        relatedCourse.setFieldConfigurationSchema(scheme)
        relatedCourse.setFeeHelpClass(Boolean.FALSE)

        Course course = cayenneContext.newObject(Course.class)
        course.setCode("Code")
        course.setName("TestCourse")
        course.setWebDescription("Test web description")
        course.setIsTraineeship(true)
        course.setIsSufficientForQualification(true)
        course.setCurrentlyOffered(true)
        course.setEnrolmentType(CourseEnrolmentType.ENROLMENT_BY_APPLICATION)
        course.setIsVET(true)
        course.setFieldOfEducation("fTest")
        course.setAllowWaitingLists(true)
        course.setReportableHours(new BigDecimal(10))
        course.setFieldConfigurationSchema(scheme)
        course.setFeeHelpClass(Boolean.FALSE)

        Product product = createProduct(cayenneContext)

        Module module = cayenneContext.newObject(Module.class)
        module.setType(ModuleType.MODULE)
        module.setNationalCode("test")
        course.addToModules(module)

        Qualification qualification = cayenneContext.newObject(Qualification.class)
        qualification.setTitle("Test qualification")
        qualification.setIsOffered(true)
        qualification.setType(QualificationType.HIGHER_TYPE)
        course.setQualification(qualification)

        Document document = cayenneContext.newObject(Document.class)
        document.setName("TestDocument")
        document.setWebVisibility(AttachmentInfoVisibility.PUBLIC)
        document.setAdded(new Date())

        CourseAttachmentRelation courseAttachmentRelation = cayenneContext.newObject(CourseAttachmentRelation.class)
        courseAttachmentRelation.setAttachedCourse(course)
        courseAttachmentRelation.setDocument(document)
        courseAttachmentRelation.setDocumentVersion(null)
        courseAttachmentRelation.setSpecialType(null)

        cayenneContext.commitChanges()

        EntityRelation courseCourseRelation = cayenneContext.newObject(EntityRelation.class)
        courseCourseRelation.setRelationType(getRelationType(cayenneContext))
        courseCourseRelation.setFromEntityIdentifier(Course.simpleName)
        courseCourseRelation.setFromEntityAngelId(course.id)
        courseCourseRelation.setToEntityIdentifier(Course.simpleName)
        courseCourseRelation.setToEntityAngelId(relatedCourse.id)


        EntityRelation productRelation = cayenneContext.newObject(EntityRelation.class)
        productRelation.setRelationType(getRelationType(cayenneContext))
        productRelation.setFromEntityIdentifier(Course.simpleName)
        productRelation.setFromEntityAngelId(course.id)
        productRelation.setToEntityIdentifier(Product.simpleName)
        productRelation.setToEntityAngelId(product.id)

        cayenneContext.commitChanges()

        DuplicateCourseService instance = injector.getInstance(DuplicateCourseService.class)
        List<Course> courses = Arrays.asList(course)
        instance.duplicateCourses(CourseDuplicationRequest.valueOf(courses))
        List<Course> duplicatedCourses = ObjectSelect.query(Course.class)
                .where(Course.ID.ne(course.getId()))
                .and(Course.ID.ne(relatedCourse.getId()))
                .select(cayenneContext)

        Assertions.assertEquals(1, duplicatedCourses.size())
        Course duplicatedCourse = duplicatedCourses.get(0)

        Assertions.assertEquals("Code1", duplicatedCourse.getCode())
        Assertions.assertEquals("TestCourse", duplicatedCourse.getName())
        Assertions.assertEquals("Test web description", duplicatedCourse.getWebDescription())
        Assertions.assertEquals(true, duplicatedCourse.getIsTraineeship())
        Assertions.assertEquals(true, duplicatedCourse.getIsSufficientForQualification())
        Assertions.assertEquals(true, duplicatedCourse.getCurrentlyOffered())
        Assertions.assertEquals(CourseEnrolmentType.ENROLMENT_BY_APPLICATION, duplicatedCourse.getEnrolmentType())
        Assertions.assertEquals(true, duplicatedCourse.getIsVET())
        Assertions.assertEquals("fTest", duplicatedCourse.getFieldOfEducation())
        Assertions.assertEquals(1, duplicatedCourse.getModules().size())

        List<CourseModule> courseModules = ObjectSelect.query(CourseModule.class)
                .where(CourseModule.COURSE.dot(Course.ID).eq(duplicatedCourse.getId()))
                .select(cayenneContext)
        Assertions.assertEquals(1, courseModules.size())
        CourseModule courseModule = courseModules.get(0)
        Assertions.assertNotNull(courseModule.getCreatedOn())
        Assertions.assertNotNull(courseModule.getModifiedOn())

        Assertions.assertNotNull(duplicatedCourse.getQualification())
        Assertions.assertEquals("Test qualification", duplicatedCourse.getQualification().getTitle())
        Assertions.assertEquals(true, duplicatedCourse.getAllowWaitingLists())
        Assertions.assertEquals(course.getReportableHours(), duplicatedCourse.getReportableHours())

        List<EntityRelation> courseCourseRelations = ObjectSelect.query(EntityRelation.class)
                .where(EntityRelation.FROM_ENTITY_ANGEL_ID.eq(duplicatedCourse.id))
                .and(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .and(EntityRelation.TO_ENTITY_ANGEL_ID.eq(relatedCourse.id))
                .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .select(cayenneContext)
        Assertions.assertEquals(1, courseCourseRelations.size())
        Assertions.assertNotNull(courseCourseRelations.get(0).getCreatedOn())
        Assertions.assertNotNull(courseCourseRelations.get(0).getModifiedOn())
        Assertions.assertEquals(duplicatedCourse.id, courseCourseRelations.get(0).getFromEntityAngelId())
        Assertions.assertEquals(relatedCourse.id, courseCourseRelations.get(0).getToEntityAngelId())


        List<EntityRelation> courseProductRelations = ObjectSelect.query(EntityRelation.class)
                .where(EntityRelation.FROM_ENTITY_ANGEL_ID.eq(duplicatedCourse.id))
                .and(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(Course.simpleName))
                .and(EntityRelation.TO_ENTITY_ANGEL_ID.eq(product.id))
                .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Product.simpleName))
                .select(cayenneContext)
        Assertions.assertEquals(1, courseProductRelations.size())
        Assertions.assertNotNull(courseProductRelations.get(0).getCreatedOn())
        Assertions.assertNotNull(courseProductRelations.get(0).getModifiedOn())
        Assertions.assertEquals(duplicatedCourse.id, courseProductRelations.get(0).getFromEntityAngelId())
        Assertions.assertEquals(product.id, courseProductRelations.get(0).getToEntityAngelId())


        List<CourseAttachmentRelation> courseAttachmentRelations = ObjectSelect.query(CourseAttachmentRelation.class)
                .where(CourseAttachmentRelation.ENTITY_RECORD_ID.eq(duplicatedCourse.getId()))
                .select(cayenneContext)

        Assertions.assertEquals(1, courseAttachmentRelations.size())
        Assertions.assertNotNull(courseAttachmentRelations.get(0).getCreatedOn())
        Assertions.assertNotNull(courseAttachmentRelations.get(0).getModifiedOn())
        Assertions.assertEquals(duplicatedCourse, courseAttachmentRelations.get(0).getAttachedCourse())
        Assertions.assertEquals("TestDocument", courseAttachmentRelations.get(0).getDocument().getName())
    }

    
    private Product createProduct(DataContext context) {
        Account account = context.newObject(Account.class)
        account.setAccountCode("1")
        account.setIsEnabled(true)
        account.setDescription("Description")
        account.setType(AccountType.EXPENSE)
        account.setId(1L)
        //commit accounts first than link to taxes (avoid exception with circular dependency on tables)
        context.commitChanges()

        Tax tax = context.newObject(Tax.class)
        tax.setPayableToAccount(account)
        tax.setReceivableFromAccount(account)
        tax.setTaxCode("GST")

        Product product = context.newObject(Product.class)
        product.setTax(tax)
        product.setIsWebVisible(false)
        product.setTaxAdjustment(new Money(100, 0))
        product.setIsOnSale(false)
        product.setSku("sku")
        product.setType(1)

        return product
    }


    private EntityRelationType getRelationType(ObjectContext context) {
        context.newObject(EntityRelationType).with { it ->
            it.name = "Test"
            it.toName = "To name"
            it.fromName = "From name"
            it.shoppingCart = EntityRelationCartAction.NO_ACTION
            it
        }
    }
}