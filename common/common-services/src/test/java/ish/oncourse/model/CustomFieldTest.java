package ish.oncourse.model;

import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * Created by alex on 4/27/17.
 */
public class CustomFieldTest extends ServiceTest{

    private ICayenneService cayenneService;
    private ObjectContext context;
    private College college;


    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceTestModule.class);

		new LoadDataSet().dataSetFile("ish/oncourse/model/customFieldDataSet.xml").load(testContext.getDS());

        cayenneService = getService(ICayenneService.class);
        context = cayenneService.newContext();
        college = SelectById.query(College.class, 10L).selectOne(context);
    }

    @Test
    public void getContactCustomFieldsTest(){

        Contact contact = SelectById.query(Contact.class, 2L).selectOne(context);
        List<ContactCustomField> fields = contact.getCustomFields();

        Assert.assertEquals(1, fields.size());
        Assert.assertEquals("20", fields.get(0).getId() + "");
    }

    @Test
    public void getCourseCustomFieldsTest(){
        Course course = SelectById.query(Course.class, 3L).selectOne(context);
        List<CourseCustomField> fields = course.getCustomFields();

        Assert.assertEquals(1, fields.size());
        Assert.assertEquals("40", fields.get(0).getId() + "");
    }

    @Test
    public void getEnrolmentCustomFieldsTest(){
        Enrolment enrolment = SelectById.query(Enrolment.class, 4L).selectOne(context);
        List<EnrolmentCustomField> fields = enrolment.getCustomFields();

        Assert.assertEquals(1, fields.size());
        Assert.assertEquals("50", fields.get(0).getId() + "");
    }

    @Test
    public void createContactCustomFieldTest(){
        Contact contact = SelectById.query(Contact.class, 1L).selectOne(context);

        //customFields amount of contact before adding a new one
        int customFieldAmount = contact.getCustomFields().size();

        //creating and filling with data new ContactCustomField
        ContactCustomField contactField = context.newObject(ContactCustomField.class);
        contactField.setCreated(new Date());
        contactField.setModified(new Date());
        CustomFieldType fieldType = SelectById.query(CustomFieldType.class, 100L).selectOne(context);
        contactField.setCustomFieldType(fieldType);
        contactField.setCollege(college);

        //adding new ContactCustomField to our contact
        contactField.setRelatedObject(contact);

        context.commitChanges();

        //getting contact again with new context
        Contact actual = SelectById.query(Contact.class, 1L).selectOne(cayenneService.newContext()); //new context

        //check if amount of related CustomField is increased
        Assert.assertEquals(customFieldAmount + 1, actual.getCustomFields().size());
    }

    @Test
    public void createCourseCustomFieldTest(){
        Course course = SelectById.query(Course.class, 2L).selectOne(context);

        //customFields amount of course before adding a new one
        int customFieldAmount = course.getCustomFields().size();

        //creating and filling with data new ContactCustomField
        CourseCustomField courseField = context.newObject(CourseCustomField.class);
        courseField.setCreated(new Date());
        courseField.setModified(new Date());
        CustomFieldType fieldType = SelectById.query(CustomFieldType.class, 200L).selectOne(context);
        courseField.setCustomFieldType(fieldType);
        courseField.setCollege(college);

        //adding new CourseCustomField to our course
        courseField.setRelatedObject(course);

        context.commitChanges();

        //getting course again with new context
        Course actual = SelectById.query(Course.class, 2L).selectOne(cayenneService.newContext());

        //check if amount of related CustomField is increased
        Assert.assertEquals(customFieldAmount + 1, actual.getCustomFields().size());
    }

    @Test
    public void createEnrolmentCustomFieldTest(){
        Enrolment enrolment = SelectById.query(Enrolment.class, 3L).selectOne(context);

        //customFields amount of enrolment before adding a new one
        int customFieldAmount = enrolment.getCustomFields().size();

        //creating and filling with data new EnrolmentCustomField
        EnrolmentCustomField enrolmentField = context.newObject(EnrolmentCustomField.class);
        enrolmentField.setCreated(new Date());
        enrolmentField.setModified(new Date());
        CustomFieldType fieldType = SelectById.query(CustomFieldType.class, 300L).selectOne(context);
        enrolmentField.setCustomFieldType(fieldType);
        enrolmentField.setCollege(college);

        //adding new EnrolmentCustomField to our enrolment
        enrolmentField.setRelatedObject(enrolment);

        context.commitChanges();

        //getting contact again with new context
        Enrolment actual = SelectById.query(Enrolment.class, 3L).selectOne(cayenneService.newContext());

        //check if amount of related CustomField is increased
        Assert.assertEquals(customFieldAmount + 1, actual.getCustomFields().size());
    }

    /**
     * check if attribute "entityName" contains right value
     * when the ContactCustomField committed
     */
    @Test
    public void checkContactCustomFieldAutofillEntityidentifierOnCommit(){
        Contact contact = SelectById.query(Contact.class, 1L).selectOne(context);
        CustomFieldType fieldType = SelectById.query(CustomFieldType.class, 100L).selectOne(context);

        ContactCustomField contactField = context.newObject(ContactCustomField.class);
        contactField.setCreated(new Date());
        contactField.setModified(new Date());
        contactField.setRelatedObject(contact);
        contactField.setCustomFieldType(fieldType);
        contactField.setCollege(college);

        try {
            context.commitChanges();
        } catch (CayenneRuntimeException e){
            Assert.fail(e.getMessage());
        }
    }

    /**
     * check if attribute "entityIdentifier" contains right value
     * when the CourseCustomField committed
     */
    @Test
    public void checkCourseCustomFieldAutofillEntityidentifierOnCommit(){
        Course course = SelectById.query(Course.class, 3L).selectOne(context);
        CustomFieldType fieldType = SelectById.query(CustomFieldType.class, 200L).selectOne(context);

        CourseCustomField courseField = context.newObject(CourseCustomField.class);
        courseField.setCreated(new Date());
        courseField.setModified(new Date());
        courseField.setRelatedObject(course);
        courseField.setCustomFieldType(fieldType);
        courseField.setCollege(college);

        try {
            context.commitChanges();
        } catch (CayenneRuntimeException e){
            Assert.fail(e.getMessage());
        }
    }

    /**
     * check if attribute "entityIdentifier" contains right value
     * when the EnrolmentCustomField committed
     */
    @Test
    public void checkEnrolmentCustomFieldAutofillEntityidentifierOnCommit(){
        Enrolment enrolment = SelectById.query(Enrolment.class, 3L).selectOne(context);
        CustomFieldType fieldType = SelectById.query(CustomFieldType.class, 300L).selectOne(context);

        EnrolmentCustomField enrolmentField = context.newObject(EnrolmentCustomField.class);
        enrolmentField.setCreated(new Date());
        enrolmentField.setModified(new Date());
        enrolmentField.setRelatedObject(enrolment);
        enrolmentField.setCustomFieldType(fieldType);
        enrolmentField.setCollege(college);

        try {
            context.commitChanges();
        } catch (CayenneRuntimeException e){
            Assert.fail(e.getMessage());
        }
    }
}
