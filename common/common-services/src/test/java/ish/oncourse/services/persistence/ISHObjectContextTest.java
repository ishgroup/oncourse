package ish.oncourse.services.persistence;

import ish.oncourse.cayenne.cache.JCacheQueryCache;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.cache.NestedQueryCache;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ISHObjectContextTest extends ServiceTest {
	
	@Test
	public void testJCacheQueryCache() throws Exception {

		initTest("ish.oncourse.services", "", ServiceTestModule.class);
		ICayenneService cayenneService = getService(ICayenneService.class);
		ISHObjectContext context = (ISHObjectContext) cayenneService.newContext();
		assertNotNull("Context should be created.", context);
		assertEquals("If we use JCacheQueryCache for entities we also should use it for the queries", JCacheQueryCache.class,
				((NestedQueryCache)context.getQueryCache()).getDelegate().getClass());
	}

	@Test
	public void testNoopQueryCache() throws Exception {
		initTest("ish.oncourse.services", "", new NoopQueryCache(), ServiceTestModule.class);
		ICayenneService cayenneService = getService(ICayenneService.class);
		ISHObjectContext context = (ISHObjectContext) cayenneService.newContext();
		assertNotNull("Context should be created.", context);
		assertEquals("If we not use EhCacheQueryCache for entities we also should not use it for the queries",
				NoopQueryCache.class, ((NestedQueryCache) context.getQueryCache()).getDelegate().getClass());
	}

	@Test(expected = RuntimeException.class)
	public void testDefaultCommitAttempts() throws Exception {

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
