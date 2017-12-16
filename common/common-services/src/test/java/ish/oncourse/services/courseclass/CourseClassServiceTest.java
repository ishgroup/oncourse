package ish.oncourse.services.courseclass;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.model.Survey;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CourseClassServiceTest extends ServiceTest {

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/services/courseclass/oncourseDataSet.xml").load(testContext.getDS());
    }

    @Test
    public void testGetContactCourseClasses()
    {
        ICayenneService cayenneService = getService(ICayenneService.class);
        ICourseClassService service = getService(ICourseClassService.class);
        ObjectContext objectContext = cayenneService.newContext();

        //tutor test
        Contact contact = Cayenne.objectForPK(objectContext, Contact.class, 1);

        List<CourseClass>  courseClasses = service.getContactCourseClasses(contact, CourseClassFilter.ALL);

        assertEquals("All courses count", 5, courseClasses.size());

        courseClasses = service.getContactCourseClasses(contact, CourseClassFilter.CURRENT);
        assertEquals("Test Current courses count", 2, courseClasses.size());
        assertEquals("Test order Current course 3", 3L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Current course 4", 4L, courseClasses.get(1).getId().longValue());

        courseClasses = service.getContactCourseClasses(contact, CourseClassFilter.UNCONFIRMED);
        assertEquals("Test Unconfirmed classes count", 1, courseClasses.size());
        assertEquals("Test order Current course 5", 5L, courseClasses.get(0).getId().longValue());

        courseClasses = service.getContactCourseClasses(contact, CourseClassFilter.PAST);
        assertEquals("Past courses count",2, courseClasses.size());
        assertEquals("Test order Past course 2",2L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Past course 1",1L, courseClasses.get(1).getId().longValue());


        //student test
        contact = Cayenne.objectForPK(objectContext, Contact.class, 3);
        courseClasses = service.getContactCourseClasses(contact, CourseClassFilter.ALL);
        assertEquals("Past courses count",4, courseClasses.size());
        assertEquals("Test order Past course 4",4L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Past course 3",3L, courseClasses.get(1).getId().longValue());
        assertEquals("Test order Past course 2",2L, courseClasses.get(2).getId().longValue());
        assertEquals("Test order Past course 1",1L, courseClasses.get(3).getId().longValue());
    }

    @Test
    public  void testGetContactSessions()
    {
        ICayenneService cayenneService = getService(ICayenneService.class);
        ICourseClassService service = getService(ICourseClassService.class);
        ObjectContext objectContext = cayenneService.newContext();

        Contact contact = Cayenne.objectForPK(objectContext, Contact.class, 2);

        /**
         * Test contact tutor and contact student is null
         */

        List<Session> list =  service.getContactSessions(contact);
        assertTrue("list should be empty", list.isEmpty());

        /**
         * test tutor session, does not return calceled
         */
        contact = Cayenne.objectForPK(objectContext, Contact.class, 1);
        list =  service.getContactSessions(contact);
        assertEquals("3 correct, 2 old, 1 canceled", 3, list.size());

        /**
         * test student session, does not return canceled, does not return for not success
         */
        contact = Cayenne.objectForPK(objectContext, Contact.class, 3);
        list =  service.getContactSessions(contact);
        assertEquals("2 correct, 2 old, 1 canceled, 1 failed", 2, list.size());
    }


	@Test
	public void testGetSurveysForTutor()
	{
		ICayenneService cayenneService = getService(ICayenneService.class);
		ObjectContext objectContext = cayenneService.newContext();

		CourseClass courseClass = Cayenne.objectForPK(objectContext, CourseClass.class, 1);
		ICourseClassService service = getService(ICourseClassService.class);
		List<Survey> surveys = service.getSurveysFor(courseClass);
		assertEquals(1, surveys.size());
	}

	@Test
	public void testGetSurveysForClass()
	{
		ICayenneService cayenneService = getService(ICayenneService.class);
		ObjectContext objectContext = cayenneService.newContext();

		Contact tutor = Cayenne.objectForPK(objectContext, Contact.class, 1);
		ICourseClassService service = getService(ICourseClassService.class);
		List<Survey> surveys = service.getSurveysFor(tutor.getTutor());
		assertEquals(6, surveys.size());
	}

}
