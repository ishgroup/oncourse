package ish.oncourse.services.site

import ish.oncourse.model.WebHostName
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class GetDomain {
    
    private final static Logger logger = LogManager.getLogger()
    private String serverName
    private ObjectContext context

    GetDomain(String serverName, ObjectContext context) {
        this.context = context
        this.serverName = serverName.toLowerCase()
    }
    
    WebHostName get() {
        WebHostName collegeDomain
        String siteKey = new GetSiteKey(serverName).get()

        if (siteKey) {
            // there's not domain for technical site
            return null
        } else {
            collegeDomain = (ObjectSelect.query(WebHostName.class) & WebHostName.NAME.eq(serverName))
                    .cacheStrategy(QueryCacheStrategy.SHARED_CACHE, WebHostName.class.getSimpleName())
                    .selectFirst(context)
        }


        if (collegeDomain) {
            logger.debug("Request server name: {}; returning domain object with {}", serverName,  collegeDomain.name) 
        }

        collegeDomain
    }
}
