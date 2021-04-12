package ish.oncourse.services.alias;

import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.model.WebUrlAlias;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class GetRedirects {
    private WebSiteVersion webSiteVersion;
    private ObjectContext objectContext;
    private QueryCacheStrategy strategy;


    public List<WebUrlAlias> get() {
        return ObjectSelect.query(WebUrlAlias.class)
                .cacheStrategy(strategy != null ? strategy : QueryCacheStrategy.LOCAL_CACHE_REFRESH)
                .cacheGroup(webSiteVersion.getWebSite() != null ? webSiteVersion.getWebSite().getSiteKey() : WebUrlAlias.class.getSimpleName())
                .and(WebUrlAlias.WEB_SITE_VERSION.eq(webSiteVersion))
                .and(WebUrlAlias.REDIRECT_TO.isNotNull())
                .and(WebUrlAlias.SPECIAL_PAGE.isNull())
                .and(WebUrlAlias.MATCH_TYPE.isNull())
                .orderBy(WebUrlAlias.MODIFIED.desc()).select(objectContext);
    }

    public static GetRedirects valueOf(WebSiteVersion webSiteVersion, ObjectContext objectContext, QueryCacheStrategy strategy) {
        GetRedirects result = new GetRedirects();
        result.webSiteVersion = webSiteVersion;
        result.objectContext = objectContext;
        result.strategy = strategy;
        return result;
    }
}
