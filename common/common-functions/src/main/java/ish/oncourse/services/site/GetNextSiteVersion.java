/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

public class GetNextSiteVersion {

    private ObjectContext objectContext;
    private WebSite webSite;
    
    public Long get() {
        Long maxValue = ObjectSelect.columnQuery(WebSiteVersion.class, WebSiteVersion.SITE_VERSION.max()).where(WebSiteVersion.WEB_SITE.eq(webSite)).selectOne(objectContext);
        return ++maxValue;
    }

    public static GetNextSiteVersion valueOf(ObjectContext objectContext, WebSite webSite) {
        GetNextSiteVersion result = new GetNextSiteVersion();
        result.objectContext = objectContext;
        result.webSite = webSite;
        return result;
    }
}
