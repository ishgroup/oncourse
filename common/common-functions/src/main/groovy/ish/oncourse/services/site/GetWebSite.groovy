package ish.oncourse.services.site

import ish.oncourse.model.WebHostName
import ish.oncourse.model.WebSite
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

class GetWebSite {

    private String serverName
    private String siteKeyHeader
    private ObjectContext context
    
    GetWebSite(String serverName, String siteKeyHeader, ObjectContext context) {
        this.serverName = serverName
        this.siteKeyHeader = siteKeyHeader
        this.context = context
    }
    
    WebSite get() {
        WebHostName currentDomain = new GetDomain(serverName, context).get()

        if (currentDomain == null) {
            ObjectSelect.query(WebSite.class)
                    .where(WebSite.SITE_KEY.eq(siteKeyHeader))
                    .prefetch(WebSite.COLLEGE.joint())
                    .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, WebSite.class.toString())
                    .selectFirst(context)
        } else {
            currentDomain.webSite
        }
    }
}
