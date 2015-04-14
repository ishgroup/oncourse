package ish.oncourse.services.persistence;

import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.test.TestContextUtil;
import org.apache.cayenne.cache.OSQueryCache;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ISHObjectContextTest extends ServiceTest {
	
	@Before
	public void setup() throws Exception {
	}
	
	@Test
	public void testOSQueryCache() throws Exception {
		System.setProperty(TestContextUtil.TEST_IS_QUERY_CACHE_DISABLED, "false");
		initTest("ish.oncourse.services", "", ServiceTestModule.class);
		ICayenneService cayenneService = getService(ICayenneService.class);
		ISHObjectContext context = (ISHObjectContext) cayenneService.newContext();
		assertNotNull("Context should be created.", context);
		assertTrue("If we use OSQueryCache for entities we also should use it for the queries", context.getQueryCache() instanceof OSQueryCache);
	}
	
	//@Test
	public void testNoopQueryCache() throws Exception {
		System.setProperty(TestContextUtil.TEST_IS_QUERY_CACHE_DISABLED, "true");
		initTest("ish.oncourse.services", "", ServiceTestModule.class);
		ICayenneService cayenneService = getService(ICayenneService.class);
		ISHObjectContext context = (ISHObjectContext) cayenneService.newContext();
		assertNotNull("Context should be created.", context);
		assertTrue("If we not use OSQueryCache for entities we also should not use it for the queries", context.getQueryCache() instanceof NoopQueryCache);
	}

}
