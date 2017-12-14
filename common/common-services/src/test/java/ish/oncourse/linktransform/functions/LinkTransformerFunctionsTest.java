package ish.oncourse.linktransform.functions;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.junit.Before;
import org.junit.Test;

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
        initTest("ish.oncourse.services", "service", ServiceTestModule.class);

        new LoadDataSet().dataSetFile("ish/oncourse/linktransform/PageLinkTransformerFunctionsTest.xml")
                .load(testContext.getDS());
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
        assertNotNull(courseClass);

        courseClass = GetCourseClassByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), "/class/DJPLFUntaggedInvisible-1")
                .get();
        assertNotNull(courseClass);
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
