package ish.oncourse.server.entity

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.AccountType
import ish.common.types.CourseClassAttendanceType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.ObjectContext
import org.junit.After
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static junit.framework.TestCase.assertNull
import static org.junit.Assert.assertEquals

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
    void before(){
        wipeTables()
    }
    
    @Test
    void testCustomField(){
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

        assertNull("contact use, customField with expected name exists", course.customField(CONTACT_FIELD_NAME))
        assertEquals("course use, customField with expected name exists", COURSE_FIELD_VALUE, course.customField(COURSE_FIELD_NAME))
        assertEquals("course class use, customField with expected name exists", COURSE_CLASS_FIELD_VALUE, courseClass.customField(COURSE_CLASS_FIELD_NAME))
        assertEquals("Trying to find contact by KEY", CONTACT_FIELD_VALUE, contact.customField(CONTACT_FIELD_KEY))
        assertEquals("Trying to find contact by KEY", COURSE_CLASS_FIELD_VALUE, courseClass.customField(COURSE_CLASS_FIELD_KEY))
        assertNull("Trying to find CourseCustomField in CONTACT", contact.customField(COURSE_FIELD_NAME))
        assertNull("Trying to find CourseClassCustomField in CONTACT", contact.customField(COURSE_CLASS_FIELD_NAME))
        assertNull("Trying to find mandatory ContactCustomField in COURSE", course.customField(DEFAULT_FIELD_NAME))
        assertNull("ContactCustomField doesn't relate with contact and is optional", contact.customField(NULL_FIELD_NAME))
        assertNull("CourseClassCustomField doesn't relate with course and is optional", courseClass.customField(NULL_FIELD_NAME))
        assertNull("ContactCustomField doesn't relate with contact BUT is mandatory",  contact.customField(DEFAULT_FIELD_NAME))
        assertNull("ContactCustomField doesn't exist", contact.customField("Non existing field"))
        
        //assertEquals("if there are customField with searchValue key and customField with searchValue name", "test value", ContactMixin.customField(contact, "Test field"))
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

    @After
    void after(){
        wipeTables()
    }
}
