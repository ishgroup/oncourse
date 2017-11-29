package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSiteLayout
import ish.oncourse.model.WebSiteVersion
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.eclipse.jetty.server.Request

class WebSiteLayoutFunctions {
    
    static WebSiteLayout getDefaultLayout(WebSiteVersion version, ObjectContext ctx) {
        return getLayout(WebSiteLayout.LAYOUT_KEY.eq(WebNodeType.DEFAULT_LAYOUT_KEY), version, ctx)
    }

    static WebSiteLayout getLayoutById(Long id, Request request, ObjectContext ctx) {
        WebSiteVersion version = WebSiteVersionFunctions.getCurrentVersion(request, ctx)
        return getLayout(ExpressionFactory.matchDbExp(WebSiteLayout.ID_PK_COLUMN, id), version, ctx)
    }

    static List<WebSiteLayout> getLayouts(Request request, ObjectContext ctx) {

        return ObjectSelect.query(WebSiteLayout).
                where(WebSiteLayout.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, ctx))).
                cacheGroup(WebSiteLayout.simpleName).
                cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).
                select(ctx)
    }

    
    private static WebSiteLayout getLayout(Expression selectQualifier, WebSiteVersion webSiteVersion, ObjectContext ctx) {
        return (ObjectSelect.query(WebSiteLayout)
                .where(selectQualifier)
                & WebSiteLayout.WEB_SITE_VERSION.eq(webSiteVersion)).
                cacheGroup(WebNodeType.simpleName).
                cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).
                selectOne(ctx)
    }
}
