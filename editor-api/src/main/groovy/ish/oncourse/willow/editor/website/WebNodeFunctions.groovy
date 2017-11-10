package ish.oncourse.willow.editor.website

import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebContentVisibility
import ish.oncourse.model.WebNode
import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.services.textile.ConvertCoreTextile
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.EJBQLQuery
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.apache.cayenne.query.SQLTemplate
import org.eclipse.jetty.server.Request

class WebNodeFunctions {

    static WebNode getNodeForName(String nodeName, Request request, ObjectContext context) {
        return ((ObjectSelect.query(WebNode)
                .localCache(WebNode.simpleName) & siteQualifier(request, context)) 
                & WebNode.NAME.eq(nodeName))
                .selectOne(context)
    }

    static List<WebNode> getNodes(Request request, ObjectContext context) {
        (ObjectSelect.query(WebNode) & siteQualifier(request, context))
        .prefetch(WebNode.WEB_NODE_TYPE.disjoint())
        .prefetch(WebNode.WEB_NODE_TYPE.dot(WebNodeType.WEB_SITE_LAYOUT).disjoint())
        .prefetch(WebNode.WEB_CONTENT_VISIBILITY.disjoint())
        .prefetch(WebNode.WEB_CONTENT_VISIBILITY.dot(WebContentVisibility.WEB_CONTENT).disjoint())
        .prefetch(WebNode.WEB_URL_ALIASES.disjoint())
        .localCache(WebNode.simpleName)
        .select(context)
    }

    static WebNodeType getDefaultWebNodeType(Request request, ObjectContext context) {
        return (ObjectSelect.query(WebNodeType)
                .where(WebNodeType.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context))) 
                & WebNodeType.NAME.eq(WebNodeType.PAGE))
                .cacheGroup(WebNodeType.simpleName)
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                .selectFirst(context)
    }
    
    static synchronized WebNode createNewNodeBy(WebSiteVersion webSiteVersion,
                                   WebNodeType webNodeType,
                                   String nodeName,
                                   String content,
                                   Integer nodeNumber) {
        ObjectContext ctx = webSiteVersion.objectContext
        WebNode newPageNode = ctx.newObject(WebNode)
        newPageNode.name = nodeName
        newPageNode.webSiteVersion = webSiteVersion
        newPageNode.nodeNumber = nodeNumber
        newPageNode.webNodeType = webNodeType

        WebContent webContent = ctx.newObject(WebContent)
        webContent.webSiteVersion = webSiteVersion
        webContent.contentTextile = content
        webContent.content = ConvertCoreTextile.valueOf(content).convert()

        WebContentVisibility webContentVisibility = ctx.newObject(WebContentVisibility)
        webContentVisibility.webNode = newPageNode
        webContentVisibility.regionKey = RegionKey.content
        webContentVisibility.webContent = webContent

        return newPageNode
    }

    static synchronized Integer getNextNodeNumber(Request request, ObjectContext context) {
        Integer number = ObjectSelect.columnQuery(WebNode, WebNode.NODE_NUMBER.max())
                .where(WebNode.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context)))
                .selectFirst(context)
        return number ? ++number : 1
    }
    
    private static Expression siteQualifier(Request request, ObjectContext context) {
        WebSite site = WebSiteFunctions.getCurrentWebSite(request, context)
        return (site == null) ? WebNode.WEB_SITE_VERSION.dot(WebSiteVersion.WEB_SITE).dot(WebSite.COLLEGE).eq(WebSiteFunctions.getCurrentCollege(request, context)):
                WebNode.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context))
    }
}
