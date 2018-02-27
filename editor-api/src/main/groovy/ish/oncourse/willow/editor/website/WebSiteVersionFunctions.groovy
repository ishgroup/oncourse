package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.services.site.GetDeployedVersion
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.Ordering
import org.eclipse.jetty.server.Request

class WebSiteVersionFunctions {

    static WebSiteVersion getCurrentVersion(Request request, ObjectContext context) {
        WebSite webSite = WebSiteFunctions.getCurrentWebSite(request, context)
        (ObjectSelect.query(WebSiteVersion)
                .where(WebSiteVersion.WEB_SITE.eq(webSite)) & WebSiteVersion.DEPLOYED_ON.isNull()).
                selectOne(context)
    }

    static WebSiteVersion getDeployedVersion(Request request, ObjectContext context) {
        WebSite webSite = WebSiteFunctions.getCurrentWebSite(request, context)
        GetDeployedVersion.valueOf(context, webSite, false).get()
    }
    
    static List<WebSiteVersion> getSiteVersions(Request request, ObjectContext context) {
        WebSite webSite = WebSiteFunctions.getCurrentWebSite(request, context)
        Ordering ordering = WebSiteVersion.DEPLOYED_ON.desc()
        ordering.nullSortedFirst = true
        List<WebSiteVersion> versions = ObjectSelect.query(WebSiteVersion)
                .where(WebSiteVersion.WEB_SITE.eq(webSite))
                .select(context)
        ordering.orderedList(versions)
    }

    static WebSiteVersion getVersionById(Long id, Request request, ObjectContext context) {
        WebSite webSite = WebSiteFunctions.getCurrentWebSite(request, context)
        (ObjectSelect.query(WebSiteVersion).where(ExpressionFactory.matchDbExp(WebSiteVersion.ID_PK_COLUMN, id)) & WebSiteVersion.WEB_SITE.eq(webSite)).selectOne(context)
    }

}
