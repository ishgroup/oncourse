package ish.oncourse.willow.service

import ish.oncourse.cayenne.cache.JCacheModule
import ish.oncourse.cayenne.cache.JCacheQueryCache
import ish.oncourse.test.TestInitialContextFactory
import org.apache.cayenne.cache.QueryCache
import org.apache.cayenne.configuration.server.ServerRuntime
import org.ehcache.jsr107.Eh107CacheManager
import org.junit.After
import org.junit.Test

import javax.cache.CacheManager
import javax.naming.Context
import javax.naming.InitialContext

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class EhCacheTest {
    
    @Test 
    void ehCacheTest() {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, TestInitialContextFactory.class.getName())
        TestInitialContextFactory.bind("java:comp/env", new InitialContext())
        ServerRuntime cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", new JCacheModule())

        QueryCache  cache = cayenneRuntime.injector.getInstance(QueryCache)
        assertTrue(cache instanceof JCacheQueryCache)
        CacheManager cacheManager = ((JCacheQueryCache)cache).cacheManager
        assertEquals(cacheManager.configurationMerger.xmlConfiguration.cacheConfigurations.size(), 0)
    }

    @After
    void after() {
        TestInitialContextFactory.close()
    }
}
