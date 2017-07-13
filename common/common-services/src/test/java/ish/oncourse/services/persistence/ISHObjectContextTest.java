package ish.oncourse.services.persistence;

import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.test.TestContextUtil;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.cache.EhCacheQueryCache;
import org.apache.cayenne.cache.NestedQueryCache;
import org.apache.cayenne.cache.OSQueryCache;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ISHObjectContextTest extends ServiceTest {
	
	@Before
	public void setup() throws Exception {
	}

	@Test
	public void testEhCacheQueryCache() throws Exception {
		System.setProperty(TestContextUtil.TEST_IS_QUERY_CACHE_DISABLED, "false");
		initTest("ish.oncourse.services", "", ServiceTestModule.class);
		ICayenneService cayenneService = getService(ICayenneService.class);
		ISHObjectContext context = (ISHObjectContext) cayenneService.newContext();
		assertNotNull("Context should be created.", context);
		assertEquals("If we use EhCacheQueryCache for entities we also should use it for the queries", EhCacheQueryCache.class,
				((NestedQueryCache)context.getQueryCache()).getDelegate().getClass());
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

	@Test(expected = RuntimeException.class)
	public void testDefaultCommitAttempts() throws Exception {
		System.setProperty(TestContextUtil.TEST_IS_QUERY_CACHE_DISABLED, "true");
		initTest("ish.oncourse.services", "", ServiceTestModule.class);
		ICayenneService cayenneService = getService(ICayenneService.class);

		ISHObjectContext context = spy((ISHObjectContext) cayenneService.newContext());
		context.setChannel(null);
		try {
			context.commitChanges();
			verify(context, times(1)).commitChanges0(anyInt());
		} catch (Exception ex) {
			verify(context, times(3)).commitChanges0(anyInt());
			throw ex;
		}
	}
}
