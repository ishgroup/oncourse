package ish.oncourse.services.courseclass;

import ish.oncourse.model.*;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;

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

        Contact contact = Cayenne.objectForPK(objectContext, Contact.class, 1);

        List<CourseClass>  courseClasses = service.getContactCourseClasses(contact, CourseClassPeriod.ALL);

        assertEquals("All courses count",5, courseClasses.size());
        courseClasses = service.getContactCourseClasses(contact, CourseClassPeriod.CURRENT);
        assertEquals("Test Current courses count",3, courseClasses.size());
        assertEquals("Test order Current course 3",3L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Current course 4",4L, courseClasses.get(1).getId().longValue());
        assertEquals("Test order Current course 5",5L, courseClasses.get(2).getId().longValue());


        courseClasses = service.getContactCourseClasses(contact, CourseClassPeriod.PAST);
        assertEquals("Past courses count",2, courseClasses.size());
        assertEquals("Test order Past course 2",2L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Past course 1",1L, courseClasses.get(1).getId().longValue());
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

}
