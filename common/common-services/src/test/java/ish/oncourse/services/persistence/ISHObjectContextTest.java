package ish.oncourse.services.persistence;

import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.test.InitialContextFactoryMock;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.util.ContextUtil;
import org.apache.cayenne.cache.EhCacheQueryCache;
import org.apache.cayenne.cache.NestedQueryCache;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ISHObjectContextTest extends ServiceTest {
	
	@Test
	public void testEhCacheQueryCache() throws Exception {
		InitialContextFactoryMock.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.TRUE);
		InitialContextFactoryMock.bind(ContextUtil.QUERY_CACHE_ENABLED_PROPERTY_KEY, Boolean.TRUE);

		initTest("ish.oncourse.services", "", ServiceTestModule.class);
		ICayenneService cayenneService = getService(ICayenneService.class);
		ISHObjectContext context = (ISHObjectContext) cayenneService.newContext();
		assertNotNull("Context should be created.", context);
		assertEquals("If we use EhCacheQueryCache for entities we also should use it for the queries", EhCacheQueryCache.class,
				((NestedQueryCache)context.getQueryCache()).getDelegate().getClass());
	}

	@Test
	public void testNoopQueryCache() throws Exception {
		InitialContextFactoryMock.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);
		InitialContextFactoryMock.bind(ContextUtil.QUERY_CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);

		initTest("ish.oncourse.services", "", ServiceTestModule.class);
		ICayenneService cayenneService = getService(ICayenneService.class);
		ISHObjectContext context = (ISHObjectContext) cayenneService.newContext();
		assertNotNull("Context should be created.", context);
		assertEquals("If we not use EhCacheQueryCache for entities we also should not use it for the queries",
				NoopQueryCache.class, ((NestedQueryCache) context.getQueryCache()).getDelegate().getClass());
	}

	@Test(expected = RuntimeException.class)
	public void testDefaultCommitAttempts() throws Exception {
		InitialContextFactoryMock.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);
		InitialContextFactoryMock.bind(ContextUtil.QUERY_CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);

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
