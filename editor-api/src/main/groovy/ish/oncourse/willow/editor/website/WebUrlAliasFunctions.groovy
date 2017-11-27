package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.model.WebUrlAlias
import ish.oncourse.services.alias.GetRedirects
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.eclipse.jetty.server.Request

class WebUrlAliasFunctions {

    static List<WebUrlAlias> getRedirects(Request request, ObjectContext context) {
        GetRedirects.valueOf(WebSiteVersionFunctions.getCurrentVersion(request, context), context, true).get()
    }
    
    static WebUrlAlias getAliasByPath(String path, Request request, ObjectContext context) {
        return ((ObjectSelect.query(WebUrlAlias.class) 
                & siteQualifier(request, context)) 
                & WebUrlAlias.URL_PATH.eq(path))
                .localCache(WebUrlAlias.simpleName)
                .selectFirst(context)
        
    }

    private static Expression siteQualifier(Request request, ObjectContext context) {
        WebSite site = WebSiteFunctions.getCurrentWebSite(request, context)
        return (site == null) ? 
                WebUrlAlias.WEB_SITE_VERSION.dot(WebSiteVersion.WEB_SITE).dot(WebSite.COLLEGE).eq(WebSiteFunctions.getCurrentCollege(request, context)) :
                WebUrlAlias.WEB_SITE_VERSION.eq(WebSiteVersionFunctions.getCurrentVersion(request, context))
    }
}
