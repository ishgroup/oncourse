package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSiteVersion
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

class WebNodeTypeFunctions {

    static WebNodeType getDefaultWebNodeType(WebSiteVersion webSiteVersion) {
        
        WebNodeType webNodeType =  (ObjectSelect.query(WebNodeType).where(WebNodeType.WEB_SITE_VERSION.eq(webSiteVersion)) 
                & WebNodeType.NAME.eq(WebNodeType.PAGE)).
                cacheGroup(WebNodeType.simpleName).
                cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).
                selectFirst(webSiteVersion.objectContext)

        return webNodeType
    }
}
