package ish.oncourse.portal.services;

import ish.oncourse.configuration.InitZKRootNode;
import ish.oncourse.model.*;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.service.TestModule;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.util.DateFormatter;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST;
import static org.junit.Assert.*;

/**
 * User: artem
 * Date: 11/12/13
 * Time: 9:42 AM
 */
public class PortalServiceTest extends ServiceTest {

    @BeforeClass
    public static void beforeClass()throws Exception  {
        String zkHostPort = "127.0.0.1:2183";
        System.setProperty(ZK_HOST.getSystemProperty(), zkHostPort);
        ServerCnxnFactory cnxnFactory = ServerCnxnFactory.createFactory();
        cnxnFactory.configure(new InetSocketAddress(2183), 10);

        File home = File.createTempFile("test-generated-", "-tmpdir");
        home.delete();
        home.mkdirs();

        ZooKeeperServer zkServer = new ZooKeeperServer(new File(home, "snap"), new File(home, "log"), 200);
        cnxnFactory.startup(zkServer);
        InitZKRootNode.valueOf(zkHostPort).init();
    }
    
    @Before
    public void setup() throws Exception {

		initTest("ish.oncourse.portal", "portal", "src/main/resources/desktop/ish/oncourse/portal/pages", TestModule.class);
		InputStream st = PortalServiceTest.class.getClassLoader().getResourceAsStream("ish/oncourse/portal/services/oncourseDataSet.xml");
        FlatXmlDataSetBuilder builder =  new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        FlatXmlDataSet dataSet = builder.build(st);

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet);

        rDataSet.addReplacementObject("[null]", null);
        rDataSet.addReplacementObject("[currentDate]", DateUtils.addYears(new Date(), 1));
        rDataSet.addReplacementObject("[oldThenMonth]", DateUtils.addMonths(new Date(), -2));
        rDataSet.addReplacementObject("[oldThen15Days]", DateUtils.addDays(new Date(), -15));


        DataSource onDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), rDataSet);
        
    }

    @Test
    public void testGetContactCourseClasses()
    {
        ICayenneService cayenneService = getService(ICayenneService.class);
        IPortalService service = getService(IPortalService.class);
        IAuthenticationService authenticationService = getService(IAuthenticationService.class);
        ObjectContext objectContext = cayenneService.newContext();

        //tutor test
        Contact contact = Cayenne.objectForPK(objectContext, Contact.class, 1);
        authenticationService.storeCurrentUser(contact);

        List<CourseClass> courseClasses = service.getContactCourseClasses(CourseClassFilter.ALL);

        for (CourseClass courseClass : courseClasses) {
            assertNotNull(service.getCourseClassBy(courseClass.getId()));
        }

        assertNull(service.getCourseClassBy(6));

        assertEquals("All courses count", 9, courseClasses.size());
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
        assertEquals("Past courses count",8, courseClasses.size());
        assertEquals("Test order Past course 7",7L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Past course 8",8L, courseClasses.get(1).getId().longValue());
        assertEquals("Test order Past course 1",1L, courseClasses.get(2).getId().longValue());
        assertEquals("Test order Current course 9", 9L, courseClasses.get(3).getId().longValue());
        assertEquals("Test order Current course 10", 10L, courseClasses.get(4).getId().longValue());
        assertEquals("Test order Past course 2",2L, courseClasses.get(5).getId().longValue());
        assertEquals("Test order Past course 3",3L, courseClasses.get(6).getId().longValue());
        assertEquals("Test order Past course 4",4L, courseClasses.get(7).getId().longValue());

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
        assertEquals("Current courses count",2, courseClasses.size());
        assertEquals("Test order Past course 1",1L, courseClasses.get(0).getId().longValue());
        assertEquals("Test order Past course 2",2L, courseClasses.get(1).getId().longValue());

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
	
	@Test
	public void testTutorCommonResources() {
		ICayenneService cayenneService = getService(ICayenneService.class);
		IPortalService service = getService(IPortalService.class);
		IAuthenticationService authenticationService = getService(IAuthenticationService.class);
		ObjectContext objectContext = cayenneService.newContext();

		Document tutorResource = Cayenne.objectForPK(objectContext, Document.class, 4);

		Contact contact = Cayenne.objectForPK(objectContext, Contact.class, 1);
		authenticationService.storeCurrentUser(contact);

		List<CourseClass> courseClasses = service.getContactCourseClasses(CourseClassFilter.ALL);
		List<Document> tutorResources = service.getTutorCommonResources(courseClasses);
		
		assertEquals(1, tutorResources.size());
		assertEquals(tutorResource.getId(), tutorResources.get(0).getId());
	}
	
	@Test
	public void testTutorClassResources() {
		ICayenneService cayenneService = getService(ICayenneService.class);
		IPortalService service = getService(IPortalService.class);
		IAuthenticationService authenticationService = getService(IAuthenticationService.class);
		ObjectContext objectContext = cayenneService.newContext();

		Document privateResource = Cayenne.objectForPK(objectContext, Document.class, 1);
		CourseClass courseClass = Cayenne.objectForPK(objectContext, CourseClass.class, 1);

		Contact contact = Cayenne.objectForPK(objectContext, Contact.class, 1);
		authenticationService.storeCurrentUser(contact);

		List<Document> tutorClassResources = service.getResourcesBy(courseClass);

		assertEquals(3, tutorClassResources.size());
		assertFalse(tutorClassResources.contains(privateResource));
	}
	
	@Test
	public void testStudentClassResources() {
		ICayenneService cayenneService = getService(ICayenneService.class);
		IPortalService service = getService(IPortalService.class);
		IAuthenticationService authenticationService = getService(IAuthenticationService.class);
		ObjectContext objectContext = cayenneService.newContext();

		Document privateResource = Cayenne.objectForPK(objectContext, Document.class, 1);
		CourseClass courseClass = Cayenne.objectForPK(objectContext, CourseClass.class, 1);

		Contact contact = Cayenne.objectForPK(objectContext, Contact.class, 3);
		authenticationService.storeCurrentUser(contact);

		List<Document> tutorClassResources = service.getResourcesBy(courseClass);

		assertEquals(1, tutorClassResources.size());
		assertFalse(tutorClassResources.contains(privateResource));
		
		CourseClass notEnrolledIntoClass = Cayenne.objectForPK(objectContext, CourseClass.class, 5);
		
		assertTrue(service.getResourcesBy(notEnrolledIntoClass).isEmpty());
	}


    @Test
    public void testHasResults() {
        ICayenneService cayenneService = getService(ICayenneService.class);
        IPortalService service = getService(IPortalService.class);
        IAuthenticationService authenticationService = getService(IAuthenticationService.class);
        ObjectContext objectContext = cayenneService.newContext();

        Contact contact = Cayenne.objectForPK(objectContext, Contact.class, 3);
        authenticationService.storeCurrentUser(contact);

        assertTrue(service.hasResults());
    }

	@Test
	public void testContactSessionsFrom() {
		ICayenneService cayenneService = getService(ICayenneService.class);
		IPortalService service = getService(IPortalService.class);
		IAuthenticationService authenticationService = getService(IAuthenticationService.class);
		ObjectContext objectContext = cayenneService.newContext();

		Contact contact = Cayenne.objectForPK(objectContext, Contact.class, 5);
		authenticationService.storeCurrentUser(contact);

		List<Session> sessions = service.getContactSessionsFrom(DateFormatter.parseDate("2103-01-01 00:00:00", TimeZone.getDefault()), contact);
		assertEquals(4, sessions.size());
		assertEquals(Long.valueOf(551), sessions.get(0).getId());
		assertEquals(Long.valueOf(561), sessions.get(1).getId());
		assertEquals(Long.valueOf(552), sessions.get(2).getId());
		assertEquals(Long.valueOf(562), sessions.get(3).getId());
	}
}
