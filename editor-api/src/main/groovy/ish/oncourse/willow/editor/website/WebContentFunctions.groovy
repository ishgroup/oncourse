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
    private static final String BLOCK_NAME_PATTERN = 'New block (%)'

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
        WebContent newWebContent = ctx.newObject(WebContent)
        newWebContent.name = getNewBlockName(webSiteVersion, ctx)
        newWebContent.webSiteVersion = webSiteVersion
        return newWebContent
    }

    private static getNewBlockName(WebSiteVersion version, ObjectContext ctx) {
        Integer nextNumber = 1
        List<String> namesList  = ((ObjectSelect.columnQuery(WebContent, WebContent.NAME)
                .where(WebContent.NAME.like(BLOCK_NAME_PATTERN)) & blockQualifier) & WebContent.WEB_SITE_VERSION.eq(version)).select(ctx)
        if (!namesList.empty) {
            String regex = /New block \(\d+\)/
            Integer integer = namesList.findAll { it ==~ regex }.collect { it.find(/\d+/).toInteger() }.max()
            nextNumber = integer ? ++integer: 1
        }
        BLOCK_NAME_PATTERN.replace('%', nextNumber.toString())
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
    
    static boolean isBlockAssigned(WebContent block) {
        if (!block.webContentVisibilities.empty
            && block.webContentVisibilities.find { it.webNodeType }) {
            return true
        }
        return false
    }


    private static Expression getBlockQualifier() {
        WebContent.WEB_CONTENT_VISIBILITIES.outer().dot(WebContentVisibility.WEB_NODE).isNull()
                .orExp(WebContent.WEB_CONTENT_VISIBILITIES.outer().dot(WebContentVisibility.REGION_KEY).isNull())
    }
}
