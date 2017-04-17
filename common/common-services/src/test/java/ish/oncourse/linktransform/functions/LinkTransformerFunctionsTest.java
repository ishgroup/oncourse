package ish.oncourse.linktransform.functions;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.PaymentInSuccessFailAbandonTest;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Test for checking object accessibility on PageLinkTransformer class
 * Created by pavel on 3/9/17.
 */
public class LinkTransformerFunctionsTest extends ServiceTest {

    ICayenneService cayenneService;
    IWebSiteService webSiteService;
    ICourseClassService courseClassService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceModule.class);

        InputStream st = PaymentInSuccessFailAbandonTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/linktransform/PageLinkTransformerFunctionsTest.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(st);
        DataSource refDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

        cayenneService = getService(ICayenneService.class);
        webSiteService = getService(IWebSiteService.class);
        courseClassService = getService(ICourseClassService.class);
    }

    /**
     * Course of course class marked by invisible tag. Course class is visible.
     * CourseClass should be accessible.
     * @throws Exception
     */
    @Test
    public void testCourseClassAccessebilityByRootTag() throws Exception {
        CourseClass courseClass = GetCourseClassByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), "/class/DJPLFTagged-1")
                .get();

        assertNotNull(courseClass);
        assertTrue(courseClass.getIsWebVisible());

        courseClass = GetCourseClassByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), "/class/DJPLFUntagged-1")
                .get();
        assertNotNull(courseClass);
        assertTrue(courseClass.getIsWebVisible());

        courseClass = GetCourseClassByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), "/class/DJPLFTaggedInvisible-1")
                .get();
        assertNull(courseClass);

        courseClass = GetCourseClassByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), "/class/DJPLFUntaggedInvisible-1")
                .get();
        assertNull(courseClass);
    }

    /**
     * Course marked by invisible tag. Course is visible on web.
     * Course should be accessible.
     * @throws Exception
     */
    @Test
    public void testCourseAccessebilityByRootTag() throws Exception {
        Course course = GetCourseByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), "/course/DJPLFTagged")
                .get();
        assertNotNull(course);
        assertTrue(course.getIsWebVisible());

        course = GetCourseByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), "/course/DJPLFUntagged")
                .get();
        assertNotNull(course);
        assertTrue(course.getIsWebVisible());

        course = GetCourseByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), "/course/DJPLFTaggedInvisible")
                .get();
        assertNull(course);

        course = GetCourseByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), "/course/DJPLFUntaggedInvisible")
                .get();
        assertNull(course);
    }
}
