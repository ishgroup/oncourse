package ish.oncourse.cayenne.cache

import org.apache.cayenne.cache.QueryCache

interface ICacheInvalidationService {
    void setCache(QueryCache cache)
    
    void init()
}