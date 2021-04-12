package ish.oncourse.services.menu;

import ish.oncourse.model.WebMenu;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class GetMenuChildren {
    private WebMenu parentMenu;
    private ObjectContext objectContext;
    private boolean useCache = true;
    public List<WebMenu> get() {
        List<WebMenu> result = new ArrayList<>();
        String cache;
        if (parentMenu.getWebSiteVersion() != null && parentMenu.getWebSiteVersion().getWebSite() != null) {
            cache = parentMenu.getWebSiteVersion().getWebSite().getSiteKey();
        } else {
            cache = WebMenu.class.getSimpleName();
        }
            
        result.addAll(ObjectSelect.query(WebMenu.class).
                cacheStrategy(useCache ? QueryCacheStrategy.LOCAL_CACHE : QueryCacheStrategy.LOCAL_CACHE_REFRESH)
                .cacheGroup(cache)
                .and(WebMenu.PARENT_WEB_MENU.eq(parentMenu))
                .prefetch(WebMenu.PARENT_WEB_MENU.disjoint())
                .prefetch(WebMenu.WEB_NODE.disjoint())
                .select(objectContext));
        Collections.sort(result);
        return result;
    }

    public static GetMenuChildren valueOf(WebMenu parentMenu, ObjectContext objectContext, boolean useCache) {
        GetMenuChildren result = new GetMenuChildren();
        result.parentMenu = parentMenu;
        result.objectContext = objectContext;
        result.useCache = useCache;
        return result;
    }
}
