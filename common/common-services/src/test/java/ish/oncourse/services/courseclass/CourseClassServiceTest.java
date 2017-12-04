package ish.oncourse.services.courseclass;

import ish.oncourse.model.*;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.Preferences;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CourseClassServiceTest extends ServiceTest {

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceModule.class);

        InputStream st = CourseClassServiceTest.class.getClassLoader().getResourceAsStream("ish/oncourse/services/courseclass/oncourseDataSet.xml");
        FlatXmlDataSetBuilder builder =  new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        FlatXmlDataSet dataSet = builder.build(st);
        DataSource onDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
    }

    @Test
    public void testGetContactCourseClasses()
    {
        ICayenneService cayenneService = getService(ICayenneService.class);
        ICourseClassService service = getService(ICourseClassService.class);
        ObjectContext objectContext = cayenneService.sharedContext();

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
        ObjectContext objectContext = cayenneService.sharedContext();

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
		ObjectContext objectContext = cayenneService.sharedContext();

		CourseClass courseClass = Cayenne.objectForPK(objectContext, CourseClass.class, 1);
		ICourseClassService service = getService(ICourseClassService.class);
		List<Survey> surveys = service.getSurveysFor(courseClass);
		assertEquals(1, surveys.size());
	}

	@Test
	public void testGetSurveysForClass()
	{
		ICayenneService cayenneService = getService(ICayenneService.class);
		ObjectContext objectContext = cayenneService.sharedContext();

		Contact tutor = Cayenne.objectForPK(objectContext, Contact.class, 1);
		ICourseClassService service = getService(ICourseClassService.class);
		List<Survey> surveys = service.getSurveysFor(tutor.getTutor());
		assertEquals(6, surveys.size());
	}

	@Test
    public void testHideOnWebClasses() {
        ICayenneService cayenneService = getService(ICayenneService.class);
        ObjectContext context = cayenneService.newContext();
        ICourseClassService service = getService(ICourseClassService.class);
        College college = Cayenne.objectForPK(context, College.class, 1);

        Course course6 = Cayenne.objectForPK(context, Course.class, 6);

        CourseClass courseClass7 = Cayenne.objectForPK(context, CourseClass.class, 7);
        CourseClass courseClass8 = Cayenne.objectForPK(context, CourseClass.class, 8);
        CourseClass courseClass9 = Cayenne.objectForPK(context, CourseClass.class, 9);

        setHideOnWebProperty(context, college, 0, ClassAgeType.afterClassEnds);
        assertEquals(0, service.getEnrollableClasses(course6).size());
        setHideOnWebProperty(context, college, getDayDifferenceBetweenDates(courseClass8.getEndDate(), new Date()) - 4, ClassAgeType.afterClassEnds);
        assertEquals(1, service.getEnrollableClasses(course6).size());
        setHideOnWebProperty(context, college, getDayDifferenceBetweenDates(courseClass8.getEndDate(), new Date()) - 5, ClassAgeType.afterClassEnds);
        assertEquals(2, service.getEnrollableClasses(course6).size());
        setHideOnWebProperty(context, college, getDayDifferenceBetweenDates(courseClass8.getEndDate(), new Date()) - 6, ClassAgeType.afterClassEnds);
        assertEquals(3, service.getEnrollableClasses(course6).size());
        setHideOnWebProperty(context, college, getDayDifferenceBetweenDates(courseClass8.getEndDate(), new Date()) - 7, ClassAgeType.afterClassEnds);
        assertEquals(3, service.getEnrollableClasses(course6).size());

        setHideOnWebProperty(context, college, 0, ClassAgeType.beforeClassEnds);
        assertEquals(0, service.getEnrollableClasses(course6).size());
        setHideOnWebProperty(context, college, getDayDifferenceBetweenDates(courseClass8.getEndDate(), new Date()) - 5, ClassAgeType.beforeClassEnds);
        assertEquals(2, service.getEnrollableClasses(course6).size());
        setHideOnWebProperty(context, college, getDayDifferenceBetweenDates(courseClass8.getEndDate(), new Date()) - 6, ClassAgeType.beforeClassEnds);
        assertEquals(3, service.getEnrollableClasses(course6).size());
        setHideOnWebProperty(context, college, getDayDifferenceBetweenDates(courseClass8.getEndDate(), new Date()) - 7, ClassAgeType.beforeClassEnds);
        assertEquals(3, service.getEnrollableClasses(course6).size());
        setHideOnWebProperty(context, college, getDayDifferenceBetweenDates(courseClass8.getEndDate(), new Date()) - 8, ClassAgeType.beforeClassEnds);
        assertEquals(3, service.getEnrollableClasses(course6).size());
    }

    private void setHideOnWebProperty(ObjectContext context, College college, long daysCount, ClassAgeType type) {
        Preference agePreference = ObjectSelect.query(Preference.class).where(Preference.NAME.eq(Preferences.HIDE_CLASS_ON_WEB_AGE)).selectFirst(context);
        if (agePreference == null)
            agePreference = context.newObject(Preference.class);
        agePreference.setName(Preferences.HIDE_CLASS_ON_WEB_AGE);
        agePreference.setValueString(String.valueOf(daysCount));
        agePreference.setCollege(college);

        Preference ageTypePreference = ObjectSelect.query(Preference.class).where(Preference.NAME.eq(Preferences.HIDE_CLASS_ON_WEB_AGE_TYPE)).selectFirst(context);
        if (ageTypePreference == null)
            ageTypePreference = context.newObject(Preference.class);
        ageTypePreference.setName(Preferences.HIDE_CLASS_ON_WEB_AGE_TYPE);
        ageTypePreference.setValueString(String.valueOf(type.ordinal()));
        ageTypePreference.setCollege(college);
        context.commitChanges();
    }

    private long getDayDifferenceBetweenDates(Date date1, Date date2) {
        long diff = Math.abs(date2.getTime() - date1.getTime());
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}
