package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteVersion
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.eclipse.jetty.server.Request

class WebSiteVersionFunctions {

    static WebSiteVersion getCurrentVersion(Request request, ObjectContext context) {
        WebSite webSite = WebSiteFunctions.getCurrentWebSite(request, context)
        (ObjectSelect.query(WebSiteVersion).
                localCache(WebSiteVersion.simpleName).
                where(WebSiteVersion.WEB_SITE.eq(webSite)) & WebSiteVersion.DEPLOYED_ON.isNull()).
                selectOne(context)
    }
}
