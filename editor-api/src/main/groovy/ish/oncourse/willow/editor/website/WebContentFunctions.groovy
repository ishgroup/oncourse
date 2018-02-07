package ish.oncourse.willow.editor.website

import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebContentVisibility
import ish.oncourse.model.WebSiteVersion
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect
import org.eclipse.jetty.server.Request

import static ish.oncourse.willow.editor.website.ResourceNameUtil.Name.BLOCK_NAME

class WebContentFunctions {

    static WebContent getBlockById(Request request, ObjectContext context, Long id) {
        return getBlockBy(ExpressionFactory.matchDbExp(WebContent.ID_PK_COLUMN, id), request, context)
    }
    
    static WebContent getBlockByName(Request request, ObjectContext context, String webContentName) {
        return getBlockBy(WebContent.NAME.eq(webContentName), request, context)
    }

    static WebContent createNewWebContent(Request request, ObjectContext ctx) {
        WebSiteVersion webSiteVersion = WebSiteVersionFunctions.getCurrentVersion(request, ctx)
        WebContent newWebContent = ctx.newObject(WebContent)
        newWebContent.name = ResourceNameUtil.getAvailableName(BLOCK_NAME, ctx, blockQualifier, getVersionQualifier(webSiteVersion))
        newWebContent.webSiteVersion = webSiteVersion

        WebContentVisibility visibility = ctx.newObject(WebContentVisibility)
        visibility.regionKey = RegionKey.unassigned
        visibility.webContent = newWebContent
        
        return newWebContent
    }

    static <T> WebContent getWebContent(Request request, ObjectContext context, Property<T> property, T value) {
        ObjectSelect<WebContent> query = ObjectSelect.query(WebContent) & getVersionQualifier(request, context)
        
        if (property) {
            query = query & property.eq(value)
        }
        return query.selectOne(context)
    }

    static List<WebContent> getBlocks(Request request, ObjectContext context) {
        return (ObjectSelect.query(WebContent)
                & getVersionQualifier(request, context)
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

    private static WebContent getBlockBy(Expression selectQualifier, Request request, ObjectContext context) {
        return (((ObjectSelect.query(WebContent)
                & getVersionQualifier(request, context))
                & selectQualifier)
                & blockQualifier)
                .selectOne(context)
    }
    
    private static Expression getVersionQualifier(WebSiteVersion version) {
        WebContent.WEB_SITE_VERSION.eq(version)
    }
    
    private static Expression getVersionQualifier(Request request, ObjectContext context) {
        WebContent.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context))
    }

    private static Expression getBlockQualifier() {
        WebContent.NAME.isNotNull().andExp(WebContent.WEB_CONTENT_VISIBILITIES.outer().dot(WebContentVisibility.WEB_NODE).isNull()
                                .orExp(WebContent.WEB_CONTENT_VISIBILITIES.outer().dot(WebContentVisibility.REGION_KEY).isNull()))
    }
}
