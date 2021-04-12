package ish.oncourse.services.site;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class GetDeployedVersion {
    private ObjectContext objectContext;
    private WebSite webSite;
    private boolean useCache = true;
    public WebSiteVersion get() {
        return ObjectSelect.query(WebSiteVersion.class)
                .cacheStrategy((useCache ? QueryCacheStrategy.LOCAL_CACHE: QueryCacheStrategy.LOCAL_CACHE_REFRESH))
                .cacheGroup(webSite != null ? webSite.getSiteKey() : WebSiteVersion.class.getSimpleName())
                .and(WebSiteVersion.WEB_SITE.eq(webSite))
                .orderBy(WebSiteVersion.DEPLOYED_ON.desc()).selectFirst(objectContext);
    }

    public static GetDeployedVersion valueOf(ObjectContext objectContext, WebSite webSite, boolean useCache) {
        GetDeployedVersion result = new GetDeployedVersion();
        result.objectContext = objectContext;
        result.webSite = webSite;
        result.useCache = useCache;
        return result;
    }
}
