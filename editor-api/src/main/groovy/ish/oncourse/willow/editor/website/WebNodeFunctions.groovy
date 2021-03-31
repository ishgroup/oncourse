package ish.oncourse.willow.editor.website

import ish.oncourse.model.*
import ish.oncourse.services.converter.CoreConverter
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.jetty.server.Request

class WebNodeFunctions {
    private static Logger logger = LogManager.logger
    private static final String SAMPLE_WEB_CONTENT = 'Sample content'
    private static final String NEW_PAGE_WEB_NODE_NAME = 'New Page'
    public static final String PAGE_PATH_TEMPLATE = "/page/"
    
    static WebNode getNodeForName(String nodeName, Request request, ObjectContext context) {
        return ((ObjectSelect.query(WebNode)
                & siteQualifier(request, context))
                & WebNode.NAME.eq(nodeName))
                .selectOne(context)
    }

    static List<WebNode> getNodes(Request request, ObjectContext context) {
        (ObjectSelect.query(WebNode) & siteQualifier(request, context))
        .prefetch(WebNode.WEB_CONTENT_VISIBILITY.disjoint())
        .prefetch(WebNode.WEB_CONTENT_VISIBILITY.dot(WebContentVisibility.WEB_CONTENT).disjoint())
        .prefetch(WebNode.WEB_URL_ALIASES.disjoint())
        .select(context)
    }

    static WebNodeType getDefaultWebNodeType(Request request, ObjectContext context) {
        return (ObjectSelect.query(WebNodeType)
                .where(WebNodeType.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context))) 
                & WebNodeType.NAME.eq(WebNodeType.PAGE))
                .selectFirst(context)
    }

    static WebNode createNewNode(Request request, ObjectContext ctx) {
        WebSiteVersion webSiteVersion = WebSiteVersionFunctions.getCurrentVersion(request, ctx)
        WebNodeType webNodeType = WebNodeTypeFunctions.getDefaultWebNodeType(webSiteVersion)
        Integer nextNodeNumber = getNextNodeNumber(request, ctx)
        return createNewNodeBy(webSiteVersion, webNodeType, "$NEW_PAGE_WEB_NODE_NAME  ($nextNodeNumber)" , SAMPLE_WEB_CONTENT, nextNodeNumber)
    }
    
    static WebNode createNewNodeBy(WebSiteVersion webSiteVersion,
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
        webContent.content = CoreConverter.convert(content)

        WebContentVisibility webContentVisibility = ctx.newObject(WebContentVisibility)
        webContentVisibility.webNode = newPageNode
        webContentVisibility.regionKey = RegionKey.content
        webContentVisibility.webContent = webContent

        return newPageNode
    }

    static Integer getNextNodeNumber(Request request, ObjectContext context) {
        Integer number = ObjectSelect.columnQuery(WebNode, WebNode.NODE_NUMBER.max())
                .where(WebNode.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context)))
                .selectFirst(context)
        return number ? ++number : 1
    }

    static WebNode getNodeByPath(String pageUrl, Request request,  ObjectContext context) {
        ObjectSelect select = ObjectSelect.query(WebNode) & WebNode.WEB_URL_ALIASES.dot(WebUrlAlias.URL_PATH).eq(pageUrl) & siteQualifier(request, context)
        select = addPrefetches(select)
        WebNode node = select.selectFirst(context)

        return node?: getNodeByTechnicalPath(pageUrl, request, context)
    }

    private static WebNode getNodeByTechnicalPath(String path, Request request, ObjectContext context) {
        String regex = /^\/page\/\d+$/
        Boolean match = path ==~ regex

        if (!match) {return null}

        String number = path.replaceFirst("/page/", "")
        Integer intNumber = Integer.valueOf(number)
        return getNodeForNumber(intNumber, request, context)
    }

    static WebNode getNodeForId(Long id, Request request, ObjectContext context) {
        ObjectSelect select = ObjectSelect.query(WebNode).where(ExpressionFactory.matchDbExp(WebNode.ID_PK_COLUMN, id)) & siteQualifier(request, context)
        select = addPrefetches(select)
        return select.selectFirst(context)
    }
    
    
    static WebNode getNodeForNumber(Integer nodeNumber, Request request, ObjectContext context) {
        ObjectSelect select = ObjectSelect.query(WebNode).where(WebNode.NODE_NUMBER.eq(nodeNumber)) & siteQualifier(request, context)
        select = addPrefetches(select)
        return select.selectFirst(context)
    }

    static ObjectSelect<WebNode> addPrefetches(ObjectSelect<WebNode> select) {
        select.prefetch(WebNode.WEB_CONTENT_VISIBILITY.disjoint())
                .prefetch(WebNode.WEB_CONTENT_VISIBILITY.dot(WebContentVisibility.WEB_CONTENT).disjoint())
                .prefetch(WebNode.WEB_URL_ALIASES.disjoint())

    }
    private static Expression siteQualifier(Request request, ObjectContext context) {
        WebSite site = WebSiteFunctions.getCurrentWebSite(request, context)
        return (site == null) ? WebNode.WEB_SITE_VERSION.dot(WebSiteVersion.WEB_SITE).dot(WebSite.COLLEGE).eq(WebSiteFunctions.getCurrentCollege(request, context)):
                WebNode.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context))
    }
}
