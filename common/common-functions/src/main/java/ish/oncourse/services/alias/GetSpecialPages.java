package ish.oncourse.services.alias;

import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.model.WebUrlAlias;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.List;

public class GetSpecialPages {
    private WebSiteVersion webSiteVersion;
    private ObjectContext objectContext;
    private boolean useCache = true;


    public List<WebUrlAlias> get() {
        return ObjectSelect.query(WebUrlAlias.class)
                .cacheStrategy(useCache ? QueryCacheStrategy.LOCAL_CACHE : QueryCacheStrategy.LOCAL_CACHE_REFRESH)
                .cacheGroup(webSiteVersion.getWebSite() != null ? webSiteVersion.getWebSite().getSiteKey() : WebUrlAlias.class.getSimpleName())
                .and(WebUrlAlias.WEB_SITE_VERSION.eq(webSiteVersion))
                .and(WebUrlAlias.SPECIAL_PAGE.isNotNull())
                .and(WebUrlAlias.MATCH_TYPE.isNotNull())
                .and(WebUrlAlias.REDIRECT_TO.isNull())
                .orderBy(WebUrlAlias.MODIFIED.desc()).select(objectContext);
    }

    public static GetSpecialPages valueOf(WebSiteVersion webSiteVersion, ObjectContext objectContext, boolean useCache) {
        GetSpecialPages result = new GetSpecialPages();
        result.webSiteVersion = webSiteVersion;
        result.objectContext = objectContext;
        result.useCache = useCache;
        return result;
    }
}
