package ish.oncourse.portal.access.validate;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by akoiro on 5/04/2016.
 */
public class AccessLinksValidatorFactoryTest {
    @Test
    public void test() {

        TestContext testContext = new TestContext();
        testContext.init();

        String prevPath = "/class/5031654";

        assertTrue(AccessLinksValidatorFactory.valueOf(testContext.getPortalService(),
                testContext.getBinaryDataService(),
                testContext.getCayenneService()).getBy(prevPath, "/resource/716b5fc5-f435-4af8-a926-3f3d4b0ebb13").validate());
    }
}
