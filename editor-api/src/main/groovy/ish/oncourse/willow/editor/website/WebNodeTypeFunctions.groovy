package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSiteLayout
import ish.oncourse.model.WebSiteVersion
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.cayenne.query.SortOrder
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

        List<WebNodeType> webNodeTypes =  ObjectSelect.query(WebNodeType)
                .where(WebNodeType.WEB_SITE_VERSION.eq(webSiteVersion)).
                orderBy(WebNodeType.MODIFIED.desc()).
                cacheGroup(WebNodeType.simpleName).
                cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).
                select(webSiteVersion.objectContext)
        return webNodeTypes
    }

    static WebNodeType createNewWebNodeType(Request request, ObjectContext ctx) {
        WebSiteVersion webSiteVersion = WebSiteVersionFunctions.getCurrentVersion(request, ctx)
        WebNodeType newWebNodeType = ctx.newObject(WebNodeType)
        newWebNodeType.webSiteVersion = webSiteVersion
        newWebNodeType.name = ResourceNameUtil.getAvailableName(ResourceNameUtil.Name.THEME_NAME, ctx, WebNodeType.WEB_SITE_VERSION.eq(webSiteVersion))
        newWebNodeType.webSiteLayout = getDefaultLayout(webSiteVersion, ctx)
        newWebNodeType.webSiteLayout.webSiteVersion = webSiteVersion
        return newWebNodeType
    }

    private static WebSiteLayout getDefaultLayout(WebSiteVersion webSiteVersion, ObjectContext ctx) {
        return (ObjectSelect.query(WebSiteLayout)
                .where(WebSiteLayout.WEB_SITE_VERSION.eq(webSiteVersion)) 
                & WebSiteLayout.LAYOUT_KEY.eq(WebNodeType.DEFAULT_LAYOUT_KEY)).
                cacheGroup(WebNodeType.simpleName).
                cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).
                selectOne(ctx)
    }
}
