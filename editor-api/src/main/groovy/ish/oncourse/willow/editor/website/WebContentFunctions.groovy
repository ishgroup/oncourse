package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebContent
import ish.oncourse.model.WebContentVisibility
import ish.oncourse.model.WebSiteVersion
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.eclipse.jetty.server.Request

class WebContentFunctions {
    private static final String NEW_WEB_CONTENT_NAME = 'New block'
    private static final String SAMPLE_WEB_CONTENT = 'Sample content'

    static WebContent getBlockByName(Request request, ObjectContext context ,String webContentName) {
        
        return (((ObjectSelect.query(WebContent)
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                .cacheGroup(WebContent.simpleName)
                & WebContent.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context))) 
                & WebContent.NAME.eq(webContentName)) 
                & blockQualifier)
                .selectOne(context)
    }

    static WebContent createNewWebContent(Request request, ObjectContext ctx) {
        WebSiteVersion webSiteVersion = WebSiteVersionFunctions.getCurrentVersion(request, ctx)
        Integer nextWebContentNumber = 2
        // TODO: get content number
        return createNewWebContentBy(webSiteVersion, "$NEW_WEB_CONTENT_NAME  ($nextWebContentNumber)", SAMPLE_WEB_CONTENT)
    }

    static WebContent createNewWebContentBy(WebSiteVersion webSiteVersion, String nodeName, String content) {
        ObjectContext ctx = webSiteVersion.objectContext
        WebContent newWebContent = ctx.newObject(WebContent)

        newWebContent.name = nodeName
        newWebContent.contentTextile = content
        newWebContent.webSiteVersion = webSiteVersion
        return newWebContent
    }

    static <T> WebContent getWebContent(Request request, ObjectContext context, Property<T> property, T value) {
        ObjectSelect<WebContent> query = ObjectSelect.query(WebContent).
                cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, WebContent.simpleName) & WebContent.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context))
        
        if (property) {
            query = query & property.eq(value)
        }
        return query.selectOne(context)
    }

    static List<WebContent> getBlocks(Request request, ObjectContext context) {
        return (ObjectSelect.query(WebContent)
                .localCache(WebContent.simpleName) 
                & WebContent.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context)) 
                & blockQualifier)
                .orderBy(WebContent.MODIFIED.desc())
                .select(context)
    }


    private static Expression getBlockQualifier() {
        WebContent.WEB_CONTENT_VISIBILITIES.outer().dot(WebContentVisibility.WEB_NODE).isNull()
                .orExp(WebContent.WEB_CONTENT_VISIBILITIES.outer().dot(WebContentVisibility.REGION_KEY).isNull())
    }
}
