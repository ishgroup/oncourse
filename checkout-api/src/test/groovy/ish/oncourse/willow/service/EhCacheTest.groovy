package ish.oncourse.willow.service

import ish.oncourse.willow.cache.JCacheModule
import ish.oncourse.willow.cache.JCacheQueryCache
import org.apache.cayenne.cache.QueryCache
import org.apache.cayenne.configuration.server.ServerRuntime
import org.junit.Test

import javax.cache.CacheManager
import javax.naming.Context
import javax.naming.InitialContext

import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals


class EhCacheTest {
    
    @Test 
    void ehCacheTest() {

        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName())
        InitialContextFactoryMock.bind("java:comp/env", new InitialContext())
        ServerRuntime cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", new JCacheModule())

        QueryCache  cache = cayenneRuntime.injector.getInstance(QueryCache)
        assertTrue(cache instanceof JCacheQueryCache)
        CacheManager cacheManager = ((JCacheQueryCache)cache).cacheManager
        assertEquals(cacheManager.configurationMerger.xmlConfiguration.cacheConfigurations.size(), 4)
    }
}
