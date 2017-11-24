package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSiteLayout
import ish.oncourse.model.WebSiteVersion
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.eclipse.jetty.server.Request

class WebNodeTypeFunctions {

    static WebNodeType getDefaultWebNodeType(WebSiteVersion webSiteVersion) {
        
        WebNodeType webNodeType =  (ObjectSelect.query(WebNodeType).where(WebNodeType.WEB_SITE_VERSION.eq(webSiteVersion)) 
                & WebNodeType.NAME.eq(WebNodeType.PAGE)).
                cacheGroup(WebNodeType.simpleName).
                cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).
                selectFirst(webSiteVersion.objectContext)

        return webNodeType
    }

    static List<WebNodeType> getWebNodeTypes(Request request, ObjectContext context) {
        WebSiteVersion webSiteVersion = WebSiteVersionFunctions.getCurrentVersion(request, context)

        WebNodeType webNodeTypes =  ObjectSelect.query(WebNodeType).where(WebNodeType.WEB_SITE_VERSION.eq(webSiteVersion)).
                cacheGroup(WebNodeType.simpleName).
                cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).
                select(webSiteVersion.objectContext)

        return webNodeTypes
    }

    static WebNodeType createNewWebNodeType(Request request, ObjectContext ctx) {
        WebSiteVersion webSiteVersion = WebSiteVersionFunctions.getCurrentVersion(request, ctx)
        WebNodeType newWebNodeType = ctx.newObject(WebNodeType)
        newWebNodeType.name = 'todo: get new'
        newWebNodeType.webSiteVersion = webSiteVersion

        // TODO: get/create correct website layout
        newWebNodeType.webSiteLayout = new WebSiteLayout()
        newWebNodeType.webSiteLayout.layoutKey = 'default'
        newWebNodeType.webSiteLayout.webSiteVersion = webSiteVersion

        return newWebNodeType
    }
}
