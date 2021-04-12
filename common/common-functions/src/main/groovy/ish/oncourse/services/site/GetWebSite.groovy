package ish.oncourse.services.site

import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

class GetWebSite {

    public static final String SITE_KEY_HEADER = 'X-Site-Key'

    private String siteKeyHeader
    private ObjectContext context
    
    GetWebSite(String siteKeyHeader, ObjectContext context) {
        this.siteKeyHeader = siteKeyHeader
        this.context = context
    }
    
    WebSite get() {
        ObjectSelect.query(WebSite.class)
                .where(WebSite.SITE_KEY.eq(siteKeyHeader))
                .prefetch(WebSite.COLLEGE.disjointById())
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                .cacheGroup(siteKeyHeader)
                .selectFirst(context)
    }
}
