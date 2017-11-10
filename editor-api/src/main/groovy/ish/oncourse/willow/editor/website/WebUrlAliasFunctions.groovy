package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebUrlAlias
import ish.oncourse.services.alias.GetRedirects
import org.apache.cayenne.ObjectContext
import org.eclipse.jetty.server.Request

class WebUrlAliasFunctions {

    static List<WebUrlAlias> getRedirects(Request request, ObjectContext context) {
        GetRedirects.valueOf(WebSiteVersionFunctions.getCurrentVersion(request, context), context, true).get()
    }
}
