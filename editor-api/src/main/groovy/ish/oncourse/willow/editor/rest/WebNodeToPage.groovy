package ish.oncourse.willow.editor.rest

import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebLayoutPath
import ish.oncourse.model.WebNode
import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSiteVersion

import ish.oncourse.specialpages.RequestMatchType
import ish.oncourse.willow.editor.v1.model.Page
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

class WebNodeToPage {

    private WebNode webNode

    private ObjectContext context

    private String pathUrl

    private WebNodeToPage(){}

    static WebNodeToPage valueOf(WebNode webNode) {
        WebNodeToPage serializer = new WebNodeToPage()
        serializer.webNode = webNode
        serializer
    }

    static WebNodeToPage valueOf(WebNode webNode, ObjectContext context, String pathUrl) {
        WebNodeToPage serializer = new WebNodeToPage()
        serializer.webNode = webNode
        serializer.context = context
        serializer.pathUrl = pathUrl
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
            page.themeId = pathUrl ? (Integer) getPageTheme(webNode.webSiteVersion).id : null
            page
        }
    }

    private WebNodeType getPageTheme(WebSiteVersion siteVersion) {

        WebLayoutPath layoutPath = ((ObjectSelect.query(WebLayoutPath.class)
                .where(WebLayoutPath.WEB_SITE_VERSION.eq(siteVersion)) & WebLayoutPath.PATH.eq(pathUrl)) &
                WebLayoutPath.MATCH_TYPE.eq(RequestMatchType.EXACT))
                .prefetch(WebLayoutPath.WEB_NODE_TYPE.joint())
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                .selectFirst(context)

        if (layoutPath == null) {
            layoutPath = (ObjectSelect.query(WebLayoutPath.class)
                    .where(WebLayoutPath.WEB_SITE_VERSION.eq(siteVersion)) & WebLayoutPath.MATCH_TYPE.eq(RequestMatchType.STARTS_WITH))
                    .prefetch(WebLayoutPath.WEB_NODE_TYPE.joint())
                    .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
                    .select(context)
                    .findAll { path -> pathUrl.startsWith(path.getPath()) }
                    .max { it.path.length() }
        }

        if (layoutPath != null) return layoutPath.webNodeType

        ObjectSelect.query(WebNodeType.class).
                where(WebNodeType.NAME.eq(WebNodeType.PAGE).andExp(WebNodeType.WEB_SITE_VERSION.eq(siteVersion))).
                selectFirst(context)
    }
}
