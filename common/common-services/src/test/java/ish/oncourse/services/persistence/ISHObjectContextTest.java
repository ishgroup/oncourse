package ish.oncourse.services.persistence;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.junit.Before;
import org.junit.Test;

import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.TestContextUtil;
import ish.oncourse.test.ServiceTest;

public class ISHObjectContextTest extends ServiceTest {
	private static final String TEST_CACHE_GROUP_STRING = "test cache group";
	
	@Before
	public void setup() throws Exception {
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testOSQueryCache() throws Exception {
		System.setProperty(TestContextUtil.TEST_IS_QUERY_CACHE_DISABLED, "false");
		initTest("ish.oncourse.services", "", ServiceTestModule.class);
		ICayenneService cayenneService = getService(ICayenneService.class);
		ISHObjectContext context = (ISHObjectContext) cayenneService.newContext();
		assertNotNull("Context should be created.", context);
		assertTrue("If we use OSQueryCache for entities we also should use it for the queries", context.isUseQueryCache());
		
		SelectQuery query = new SelectQuery();
		assertEquals("By default select query use no cache", QueryCacheStrategy.NO_CACHE, query.getCacheStrategy());
		assertNull("By default select query have no cache group", query.getCacheGroups());
		context.updateQueryMetaDataIfCacheUsed(query);
		assertEquals("Before execute the select context should update cache strategy to context corresponding", QueryCacheStrategy.LOCAL_CACHE, 
			query.getCacheStrategy());
		String [] expectedCacheGroup = {ISHObjectContext.DEFAULT_CACHE_GROUP};
		assertEquals("Before execute the select context should update cache group to context corresponding", expectedCacheGroup, query.getCacheGroups());
		
		query = new SelectQuery();
		query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH);
		assertEquals("Select query now use local cache refresh", QueryCacheStrategy.LOCAL_CACHE_REFRESH, query.getCacheStrategy());
		assertNull("By default select query have no cache group", query.getCacheGroups());
		context.updateQueryMetaDataIfCacheUsed(query);
		assertEquals("Before execute the select context should update cache strategy only if required", QueryCacheStrategy.LOCAL_CACHE_REFRESH, 
			query.getCacheStrategy());
		assertEquals("Before execute the select we should update cache group to context corresponding", expectedCacheGroup, query.getCacheGroups());
		
		query = new SelectQuery();
		query.setCacheStrategy(QueryCacheStrategy.SHARED_CACHE);
		assertEquals("Select query now use shared cache", QueryCacheStrategy.SHARED_CACHE, query.getCacheStrategy());
		assertNull("By default select query have no cache group", query.getCacheGroups());
		context.updateQueryMetaDataIfCacheUsed(query);
		assertEquals("Before execute the select context should update cache strategy only if required", QueryCacheStrategy.SHARED_CACHE, 
			query.getCacheStrategy());
		assertEquals("Before execute the select we should update cache group to context corresponding", expectedCacheGroup, query.getCacheGroups());
		
		query = new SelectQuery();
		query.setCacheStrategy(QueryCacheStrategy.SHARED_CACHE_REFRESH);
		assertEquals("Select query now use shared cache refresh", QueryCacheStrategy.SHARED_CACHE_REFRESH, query.getCacheStrategy());
		assertNull("By default select query have no cache group", query.getCacheGroups());
		context.updateQueryMetaDataIfCacheUsed(query);
		assertEquals("Before execute the select context should update cache strategy only if required", QueryCacheStrategy.SHARED_CACHE_REFRESH, 
			query.getCacheStrategy());
		assertEquals("Before execute the select we should update cache group to context corresponding", expectedCacheGroup, query.getCacheGroups());
		
		query = new SelectQuery();
		query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
		query.setCacheGroups(TEST_CACHE_GROUP_STRING);
		String [] updatedCacheGroup = {TEST_CACHE_GROUP_STRING};
		assertEquals("Select query now use local cache", QueryCacheStrategy.LOCAL_CACHE, query.getCacheStrategy());
		assertNotNull("Now select query should have defined cache group", query.getCacheGroups());
		assertEquals("Select query should have cache group", updatedCacheGroup, query.getCacheGroups());
		context.updateQueryMetaDataIfCacheUsed(query);
		assertEquals("Before execute the select context shouldn't update cache strategy if correct", QueryCacheStrategy.LOCAL_CACHE, 
			query.getCacheStrategy());
		assertEquals("Before execute the select we shouldn't add cache group if already specified", updatedCacheGroup, query.getCacheGroups());
		
		//now turn off the context isUseQueryCache
		context.setUseQueryCache(false);
		assertFalse("If we use OSQueryCache for entities we may turn off it for the queries", context.isUseQueryCache());
		query = new SelectQuery();
		assertEquals("By default select query use no cache", QueryCacheStrategy.NO_CACHE, query.getCacheStrategy());
		assertNull("By default select query have no cache group", query.getCacheGroups());
		context.updateQueryMetaDataIfCacheUsed(query);
		assertEquals("Before execute the select context should not update cache strategy if isUseQueryCache is off", QueryCacheStrategy.NO_CACHE, 
			query.getCacheStrategy());
		assertNull("Before execute the select context should not update cache group if isUseQueryCache is off", query.getCacheGroups());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testNoopQueryCache() throws Exception {
		System.setProperty(TestContextUtil.TEST_IS_QUERY_CACHE_DISABLED, "true");
		initTest("ish.oncourse.services", "", ServiceTestModule.class);
		ICayenneService cayenneService = getService(ICayenneService.class);
		ISHObjectContext context = (ISHObjectContext) cayenneService.newContext();
		assertNotNull("Context should be created.", context);
		assertFalse("If we not use OSQueryCache for entities we also should not use it for the queries", context.isUseQueryCache());
		
		SelectQuery query = new SelectQuery();
		assertEquals("By default select query use no cache", QueryCacheStrategy.NO_CACHE, query.getCacheStrategy());
		assertNull("By default select query have no cache group", query.getCacheGroups());
		context.updateQueryMetaDataIfCacheUsed(query);
		assertEquals("Before execute the select context should update cache strategy to context corresponding", QueryCacheStrategy.NO_CACHE, 
			query.getCacheStrategy());
		assertNull("Before execute the select context should not update cache group if isUseQueryCache is off", query.getCacheGroups());
		
		query = new SelectQuery();
		query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH);
		assertEquals("Select query now use local cache refresh", QueryCacheStrategy.LOCAL_CACHE_REFRESH, query.getCacheStrategy());
		assertNull("By default select query have no cache group", query.getCacheGroups());
		context.updateQueryMetaDataIfCacheUsed(query);
		assertEquals("Before execute the select context should update cache strategy only if required", QueryCacheStrategy.LOCAL_CACHE_REFRESH, 
			query.getCacheStrategy());
		assertNull("Before execute the select context should not update cache group if isUseQueryCache is off", query.getCacheGroups());
		
		query = new SelectQuery();
		query.setCacheStrategy(QueryCacheStrategy.SHARED_CACHE);
		assertEquals("Select query now use shared cache", QueryCacheStrategy.SHARED_CACHE, query.getCacheStrategy());
		assertNull("By default select query have no cache group", query.getCacheGroups());
		context.updateQueryMetaDataIfCacheUsed(query);
		assertEquals("Before execute the select context should update cache strategy only if required", QueryCacheStrategy.SHARED_CACHE, 
			query.getCacheStrategy());
		assertNull("Before execute the select context should not update cache group if isUseQueryCache is off", query.getCacheGroups());
		
		query = new SelectQuery();
		query.setCacheStrategy(QueryCacheStrategy.SHARED_CACHE_REFRESH);
		assertEquals("Select query now use shared cache refresh", QueryCacheStrategy.SHARED_CACHE_REFRESH, query.getCacheStrategy());
		assertNull("By default select query have no cache group", query.getCacheGroups());
		context.updateQueryMetaDataIfCacheUsed(query);
		assertEquals("Before execute the select context should update cache strategy only if required", QueryCacheStrategy.SHARED_CACHE_REFRESH, 
			query.getCacheStrategy());
		assertNull("Before execute the select context should not update cache group if isUseQueryCache is off", query.getCacheGroups());
		
		query = new SelectQuery();
		query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
		query.setCacheGroups(TEST_CACHE_GROUP_STRING);
		String [] updatedCacheGroup = {TEST_CACHE_GROUP_STRING};
		assertEquals("Select query now use local cache", QueryCacheStrategy.LOCAL_CACHE, query.getCacheStrategy());
		assertNotNull("Now select query should have defined cache group", query.getCacheGroups());
		assertEquals("Select query should have cache group", updatedCacheGroup, query.getCacheGroups());
		context.updateQueryMetaDataIfCacheUsed(query);
		assertEquals("Before execute the select context shouldn't update cache strategy if correct", QueryCacheStrategy.LOCAL_CACHE, 
			query.getCacheStrategy());
		assertEquals("Before execute the select we shouldn't add cache group if already specified", updatedCacheGroup, query.getCacheGroups());
	}

}
