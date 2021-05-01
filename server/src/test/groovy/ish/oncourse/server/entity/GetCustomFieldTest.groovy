package ish.oncourse.server.entity


import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.AccountType
import ish.common.types.CourseClassAttendanceType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
class GetCustomFieldTest extends CayenneIshTestCase {
    private static final String SOME_STRING = "someString"
    private static final String CONTACT_FIELD_NAME = "Contact field"
    private static final String CONTACT_FIELD_KEY = "ContactField"
    private static final String CONTACT_FIELD_VALUE = "Contact value"
    private static final String COURSE_FIELD_NAME = "Course field"
    private static final String COURSE_FIELD_KEY = "CourseField"
    private static final String COURSE_FIELD_VALUE = "Course value"
    private static final String COURSE_CLASS_FIELD_NAME = "Course class field"
    private static final String COURSE_CLASS_FIELD_KEY = "CourseClassField"
    private static final String COURSE_CLASS_FIELD_VALUE = "Course class value"
    private static final String DEFAULT_FIELD_NAME = "Field with default value"
    private static final String DEFAULT_FIELD_KEY = "fieldWithDefaultValue"
    private static final String DEFAULT_FIELD_VALUE = "default"
    private static final String NULL_FIELD_NAME = "Null field"
    private static final String NULL_FIELD_KEY = "nullField"

    @BeforeEach
    void before() {
        wipeTables()
    }

    
    @Test
    void testCustomField() {
        ObjectContext context = injector.getInstance(ICayenneService).newNonReplicatingContext

        Account account = createAccount(context)
        context.commitChanges()
        Tax tax = createTax(context, account)
        context.commitChanges()

        Contact contact = createContact(context)
        CustomFieldType contactFieldType = createCustomFieldType(CONTACT_FIELD_NAME, CONTACT_FIELD_KEY, Contact.simpleName, context)
        createCustomField(CONTACT_FIELD_VALUE, contactFieldType, ContactCustomField, contact, context)

        Course course = createCourse(context)
        CustomFieldType courseFieldType = createCustomFieldType(COURSE_FIELD_NAME, COURSE_FIELD_KEY, Course.simpleName, context)
        createCustomField(COURSE_FIELD_VALUE, courseFieldType, CourseCustomField, course, context)

        CourseClass courseClass = createCourseClass(context, account, tax)
        courseClass.course = course
        CustomFieldType courseClassFieldType = createCustomFieldType(COURSE_CLASS_FIELD_NAME, COURSE_CLASS_FIELD_KEY, CourseClass.simpleName, context)
        createCustomField(COURSE_CLASS_FIELD_VALUE, courseClassFieldType, CourseClassCustomField, courseClass, context)

        createCustomFieldType(DEFAULT_FIELD_NAME, DEFAULT_FIELD_KEY, Contact.simpleName, DEFAULT_FIELD_VALUE, context)
        createCustomFieldType(NULL_FIELD_NAME, NULL_FIELD_KEY, Contact.simpleName, context)

        context.commitChanges()

        Assertions.assertNull( course.customField(CONTACT_FIELD_NAME), "contact use, customField with expected name exists")
        Assertions.assertEquals(COURSE_FIELD_VALUE, course.customField(COURSE_FIELD_NAME), "course use, customField with expected name exists", )
        Assertions.assertEquals(COURSE_CLASS_FIELD_VALUE, courseClass.customField(COURSE_CLASS_FIELD_NAME), "course class use, customField with expected name exists", )
        Assertions.assertEquals(CONTACT_FIELD_VALUE, contact.customField(CONTACT_FIELD_KEY), "Trying to find contact by KEY")
        Assertions.assertEquals(COURSE_CLASS_FIELD_VALUE, courseClass.customField(COURSE_CLASS_FIELD_KEY), "Trying to find contact by KEY")
        Assertions.assertNull(contact.customField(COURSE_FIELD_NAME), "Trying to find CourseCustomField in CONTACT")
        Assertions.assertNull(contact.customField(COURSE_CLASS_FIELD_NAME), "Trying to find CourseClassCustomField in CONTACT")
        Assertions.assertNull(course.customField(DEFAULT_FIELD_NAME), "Trying to find mandatory ContactCustomField in COURSE")
        Assertions.assertNull(contact.customField(NULL_FIELD_NAME), "ContactCustomField doesn't relate with contact and is optional")
        Assertions.assertNull(courseClass.customField(NULL_FIELD_NAME), "CourseClassCustomField doesn't relate with course and is optional")
        Assertions.assertNull(contact.customField(DEFAULT_FIELD_NAME), "ContactCustomField doesn't relate with contact BUT is mandatory")
        Assertions.assertNull(contact.customField("Non existing field"), "ContactCustomField doesn't exist")
    }
    
    private static Contact createContact(ObjectContext context){
        Contact contact = context.newObject(Contact)
        contact.firstName = SOME_STRING
        contact.lastName = SOME_STRING
        
        contact
    }
    
    private static Course createCourse(ObjectContext context){
        FieldConfigurationScheme scheme = context.newObject(FieldConfigurationScheme)
        scheme.name = SOME_STRING

        Course course = context.newObject(Course)
        course.code = SOME_STRING
        course.name = SOME_STRING
        course.fieldConfigurationSchema = scheme
        course.feeHelpClass = Boolean.FALSE
        
        course
    }

    
    private static CourseClass createCourseClass(ObjectContext context, Account account, Tax tax) {
        CourseClass cc = context.newObject(CourseClass)
        cc.code = SOME_STRING
        cc.attendanceType = CourseClassAttendanceType.FULL_TIME_ATTENDANCE
        cc.minimumPlaces = 2
        cc.maximumPlaces = 10

        cc.incomeAccount = account
        cc.tax = tax

        cc
    }

    
    private static Account createAccount(ObjectContext context) {
        Account account = context.newObject(Account)
        account.accountCode = "ACC"
        account.isEnabled = true
        account.description = "description"
        account.type = AccountType.INCOME
        account
    }

    private static Tax createTax(ObjectContext context, Account account) {
        Tax tax = context.newObject(Tax)
        tax.payableToAccount = account
        tax.receivableFromAccount = account
        tax.taxCode = "GSTT"
        tax
    }
    
    private static CustomFieldType createCustomFieldType(String name, String key, String entityIdentifier, String defaultValue = null, boolean isMandatory = false, ObjectContext context){
        CustomFieldType cft = context.newObject(CustomFieldType)
        cft.entityIdentifier = entityIdentifier
        cft.name = name
        cft.key = key
        cft.defaultValue = defaultValue
        cft.isMandatory = isMandatory
        
        cft
    }

    private static CustomField createCustomField(String value, CustomFieldType type, Class<? extends CustomField> customFieldClass, ExpandableTrait expandable = null, ObjectContext context){
        CustomField customField = context.newObject(customFieldClass)
        customField.entityIdentifier = type.entityIdentifier
        customField.customFieldType = type
        customField.value = value
        customField.relatedObject = expandable
        
        customField
    }

    @AfterEach
    void after(){
        wipeTables()
    }
}
