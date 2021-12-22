package ish.oncourse.willow.editor.rest

import ish.oncourse.model.*
import ish.oncourse.specialpages.RequestMatchType
import ish.oncourse.willow.editor.v1.model.Page
import ish.oncourse.willow.editor.v1.model.PageLink
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

class WebNodeToPage {

    private ObjectContext context;

    private WebNode webNode
    
    private WebNodeToPage(){}

    static WebNodeToPage valueOf(WebNode webNode, ObjectContext context) {
        WebNodeToPage serializer = new WebNodeToPage()
        serializer.webNode = webNode
        serializer.context = context
        serializer
    }

    Page getPage() {
        new Page().with {page ->
            page.id = webNode.nodeNumber
            page.title = webNode.name
            page.content = webNode.webContentVisibility?.find {it.regionKey == RegionKey.content}?.webContent?.contentTextile
            page.visible = webNode.published
            page.urls += webNode.webUrlAliases.collect { webUrlAlias ->  WebUrlAliasToPageLink.valueOf(webUrlAlias).pageLink }
            page.suppressOnSitemap = webNode.suppressOnSitemap
            page.themeId = (Integer) getPageTheme(page.urls, webNode.webSiteVersion).id
            page
        }
    }

    private WebNodeType getPageTheme(List<PageLink> pageLinks, WebSiteVersion siteVersion) {

        for (def pageLink : pageLinks) {
            WebLayoutPath layoutPath = ((ObjectSelect.query(WebLayoutPath.class)
                    .where(WebLayoutPath.WEB_SITE_VERSION.eq(siteVersion)) & WebLayoutPath.PATH.eq(pageLink.link)) &
                    WebLayoutPath.MATCH_TYPE.eq(RequestMatchType.EXACT))
                    .prefetch(WebLayoutPath.WEB_NODE_TYPE.joint())
                    .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                    .selectFirst(context)

            if (layoutPath == null) {
                layoutPath = ((ObjectSelect.query(WebLayoutPath.class)
                        .where(WebLayoutPath.WEB_SITE_VERSION.eq(siteVersion)) & WebLayoutPath.PATH.eq(pageLink.link)) &
                        WebLayoutPath.MATCH_TYPE.eq(RequestMatchType.STARTS_WITH))
                        .prefetch(WebLayoutPath.WEB_NODE_TYPE.joint())
                        .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                        .select(context)
                        .stream()
                        .filter { path -> pageLink.link.startsWith(path.getPath()) }
                        .max { p -> p.path.length() }
                        .orElse(null);
            }

            if (layoutPath != null) return layoutPath.webNodeType
        }

        ObjectSelect.query(WebNodeType.class).
                where(WebNodeType.NAME.eq(WebNodeType.PAGE).andExp(WebNodeType.WEB_SITE_VERSION.eq(siteVersion))).
                selectFirst(context)
    }
}
