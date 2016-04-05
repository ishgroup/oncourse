package ish.oncourse.portal.access.validate;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by akoiro on 5/04/2016.
 */
public class CourseClassAccessLinksValidatorTest {
    @Test
    public void test() {
        TestContext testContext = new TestContext();
        testContext.init();

        assertTrue(CourseClassAccessLinksValidator.valueOf("https://www.skillsoncourse.com.au/portal/resource/716b5fc5-f435-4af8-a926-3f3d4b0ebb13",
                testContext.getCourseClass(),
                testContext.getPortalService(),
                testContext.getBinaryDataService()).validate());

        assertTrue(CourseClassAccessLinksValidator.valueOf("https://www.skillsoncourse.com.au/portal/resource/e934628d-8c87-4aeb-a50d-4e00f2df6d3a",
                testContext.getCourseClass(),
                testContext.getPortalService(),
                testContext.getBinaryDataService()).validate());

    }
}
