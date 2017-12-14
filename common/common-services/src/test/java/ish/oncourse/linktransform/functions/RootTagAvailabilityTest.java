package ish.oncourse.linktransform.functions;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by pavel on 4/13/17.
 */
public class RootTagAvailabilityTest extends ServiceTest {
    ICayenneService cayenneService;
    IWebSiteService webSiteService;
    ICourseClassService courseClassService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceTestModule.class);

        new LoadDataSet().dataSetFile("ish/oncourse/linktransform/RootTagAvailabilityTest.xml").load(testContext.getDS());


        cayenneService = getService(ICayenneService.class);
        webSiteService = getService(IWebSiteService.class);
        courseClassService = getService(ICourseClassService.class);
    }

    @Test
    public void testAvailability() throws Exception {
        CourseClass courseClass = GetCourseClassByPath
                .valueOf(cayenneService.sharedContext(), webSiteService.getCurrentWebSite(), "/class/DJPLF-1")
                .get();
        assertNotNull(courseClass);
    }
}
