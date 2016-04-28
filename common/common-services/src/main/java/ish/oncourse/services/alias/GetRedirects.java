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
    private boolean useCache = true;


    public List<WebUrlAlias> get() {
        return ObjectSelect.query(WebUrlAlias.class)
                .cacheStrategy(useCache ? QueryCacheStrategy.LOCAL_CACHE : QueryCacheStrategy.LOCAL_CACHE_REFRESH, WebUrlAlias.class.getSimpleName())
                .and(WebUrlAlias.WEB_SITE_VERSION.eq(webSiteVersion))
                .and(WebUrlAlias.REDIRECT_TO.isNotNull())
                .orderBy(WebUrlAlias.MODIFIED.desc()).select(objectContext);
    }

    public static GetRedirects valueOf(WebSiteVersion webSiteVersion, ObjectContext objectContext, boolean useCache) {
        GetRedirects result = new GetRedirects();
        result.webSiteVersion = webSiteVersion;
        result.objectContext = objectContext;
        result.useCache = useCache;
        return result;
    }
}
