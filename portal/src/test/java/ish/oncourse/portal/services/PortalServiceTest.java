package ish.oncourse.portal.services;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: artem
 * Date: 11/12/13
 * Time: 9:42 AM
 */
public class PortalServiceTest extends ServiceTest {



    @Before
    public void setup() throws Exception {


        initTest("ish.oncourse.portal", "portal","src/main/resources/desktop/ish/oncourse/portal/pages", AppModule.class);
        InputStream st = PortalServiceTest.class.getClassLoader().getResourceAsStream("ish/oncourse/portal/services/oncourseDataSet.xml");
        FlatXmlDataSetBuilder builder =  new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        FlatXmlDataSet dataSet = builder.build(st);

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet);

        rDataSet.addReplacementObject("[null]", null);


        DataSource onDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), rDataSet);
    }


    @Test
    public void testGetContactCourseClasses()
    {
        ICayenneService cayenneService = getService(ICayenneService.class);
        IPortalService service = getService(IPortalService.class);
        IAuthenticationService authenticationService = getService(IAuthenticationService.class);
        ObjectContext objectContext = cayenneService.sharedContext();

        //tutor test
        Contact contact = Cayenne.objectForPK(objectContext, Contact.class, 1);
        authenticationService.storeCurrentUser(contact);

        List<CourseClass> courseClasses = service.getContactCourseClasses(CourseClassFilter.ALL);

        for (CourseClass courseClass : courseClasses) {
            assertNotNull(service.getCourseClassBy(courseClass.getId()));
        }

        assertNull(service.getCourseClassBy(6));

        assertEquals("All courses count", 7, courseClasses.size());
        /**
         * self paced and unconfirmed classes are current too
         */
        courseClasses = service.getContactCourseClasses(CourseClassFilter.CURRENT);
        assertEquals("Test Current courses count", 5, courseClasses.size());
        assertEquals("Test order Current course 7", 7L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Current course 8", 8L, courseClasses.get(1).getId().longValue());
        assertEquals("Test order Current course 3", 3L, courseClasses.get(2).getId().longValue());
        assertEquals("Test order Current course 4", 4L, courseClasses.get(3).getId().longValue());
        assertEquals("Test order Current course 5", 5L, courseClasses.get(4).getId().longValue());





        courseClasses = service.getContactCourseClasses(CourseClassFilter.UNCONFIRMED);
        assertEquals("Test Unconfirmed classes count", 2, courseClasses.size());
        assertEquals("Test order Current course 5", 8L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Current course 8", 5L, courseClasses.get(1).getId().longValue());

        courseClasses = service.getContactCourseClasses(CourseClassFilter.PAST);
        assertEquals("Past courses count",2, courseClasses.size());
        assertEquals("Test order Past course 1",1L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Past course 2",2L, courseClasses.get(1).getId().longValue());


        //student test
        contact = Cayenne.objectForPK(objectContext, Contact.class, 3);
        authenticationService.storeCurrentUser(contact);
        courseClasses = service.getContactCourseClasses(CourseClassFilter.ALL);
        assertEquals("Past courses count",6, courseClasses.size());
        assertEquals("Test order Past course 7",7L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Past course 8",8L, courseClasses.get(1).getId().longValue());
        assertEquals("Test order Past course 1",1L, courseClasses.get(2).getId().longValue());
        assertEquals("Test order Past course 2",2L, courseClasses.get(3).getId().longValue());
        assertEquals("Test order Past course 3",3L, courseClasses.get(4).getId().longValue());
        assertEquals("Test order Past course 4",4L, courseClasses.get(5).getId().longValue());

        for (CourseClass courseClass : courseClasses) {
            assertNotNull(service.getCourseClassBy(courseClass.getId()));
        }

        assertNull(service.getCourseClassBy(5));


        courseClasses = service.getContactCourseClasses(CourseClassFilter.CURRENT);
        assertEquals("Current courses count",3, courseClasses.size());
        assertEquals("Test order Past course 8",8L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Past course 3",3L, courseClasses.get(1).getId().longValue());
        assertEquals("Test order Past course 4",4L, courseClasses.get(2).getId().longValue());

        courseClasses = service.getContactCourseClasses(CourseClassFilter.PAST);
        assertEquals("Current courses count",3, courseClasses.size());
        assertEquals("Test order Past course 7",7L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Past course 1",1L, courseClasses.get(1).getId().longValue());
        assertEquals("Test order Past course 2",2L, courseClasses.get(2).getId().longValue());

        courseClasses = service.getContactCourseClasses(CourseClassFilter.UNCONFIRMED);
        assertEquals("Current courses count",0, courseClasses.size());



        //when contact is student and tutor
        contact = Cayenne.objectForPK(objectContext, Contact.class, 4);
        authenticationService.storeCurrentUser(contact);

        courseClasses = service.getContactCourseClasses(CourseClassFilter.ALL);
        assertEquals("Current courses count",3, courseClasses.size());
        assertEquals("Test order Past course 8",8L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Past course 1",1L, courseClasses.get(1).getId().longValue());
        assertEquals("Test order Past course 3",3L, courseClasses.get(2).getId().longValue());

        for (CourseClass courseClass : courseClasses) {
            assertNotNull(service.getCourseClassBy(courseClass.getId()));
        }

        assertNull(service.getCourseClassBy(5));

        courseClasses = service.getContactCourseClasses(CourseClassFilter.CURRENT);
        assertEquals("Current courses count",2, courseClasses.size());
        assertEquals("Test order Past course 8",8L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Past course 3",3L, courseClasses.get(1).getId().longValue());

        courseClasses = service.getContactCourseClasses(CourseClassFilter.PAST);
        assertEquals("Current courses count",1, courseClasses.size());
        assertEquals("Test order Past course 1",1L, courseClasses.get(0).getId().longValue());

        courseClasses = service.getContactCourseClasses(CourseClassFilter.UNCONFIRMED);
        assertEquals("Current courses count",1, courseClasses.size());
        assertEquals("Test order Past course 8",8L, courseClasses.get(0).getId().longValue());
    }




}
