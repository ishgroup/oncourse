package ish.oncourse.services.cache;

import net.sf.ehcache.CacheManager;
import org.apache.cayenne.cache.EhCacheQueryCache;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.di.Provider;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class EHQueryCacheProvider implements Provider<EhCacheQueryCache>  {

    @Inject
    private CacheManager cacheManager;


    @Override
    public EhCacheQueryCache get() {
        EhCacheQueryCache ehCacheQueryCache = new EhCacheQueryCache(cacheManager);
        return ehCacheQueryCache;
    }
}
