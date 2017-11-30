package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebMenu
import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteVersion
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.eclipse.jetty.server.Request

import static ish.oncourse.willow.editor.website.WebNodeFunctions.PAGE_PATH_TEMPLATE

class WebMenuFunctions {

    static List<WebMenu> getTopLelevMenus(Request request, ObjectContext context) {
        return getRootMenu(request, context).childrenMenus.sort { m -> m.weight }
    }

    static String getUrlPath(WebMenu webMenu) {
        if (webMenu.webNode) {
            String urlPath = webMenu.webNode.webUrlAliases.find {it.default}?.urlPath
            return urlPath?:"${PAGE_PATH_TEMPLATE}${webMenu.webNode.nodeNumber.toString()}"
        } else {
            return webMenu.url
        }
    }
    
    private static WebMenu getRootMenu(Request request, ObjectContext context) {
        return ((ObjectSelect.query(WebMenu.class) 
                & siteQualifier(request, context)) 
                & WebMenu.PARENT_WEB_MENU.isNull())
                .localCache(WebMenu.class.getSimpleName())
                .selectOne(context)
    }

    private static Expression siteQualifier(Request request, ObjectContext context) {
        WebSite site = WebSiteFunctions.getCurrentWebSite(request, context)
        Expression expression = (site == null) ? 
                WebMenu.WEB_SITE_VERSION.dot(WebSiteVersion.WEB_SITE).dot(WebSite.COLLEGE).eq(WebSiteFunctions.getCurrentCollege(request, context)):
                WebMenu.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context))
        return expression
    }
}
