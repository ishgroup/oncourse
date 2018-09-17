package ish.oncourse.willow.editor.website

import ish.oncourse.model.College
import ish.oncourse.model.WebSite
import ish.oncourse.services.site.GetWebSite
import org.apache.cayenne.ObjectContext
import org.eclipse.jetty.server.Request

import static ish.oncourse.services.site.GetWebSite.SITE_KEY_HEADER

class WebSiteFunctions {

    private static final String  CURRENT_COLLEGE = 'currentCollege'
    private static final String  CURRENT_WEB_SITE = 'currentWebSite'
    
    static College getCurrentCollege(Request request, ObjectContext context) {
        
        College currentCollege = (College) request.getAttribute(CURRENT_COLLEGE)
        
        if (currentCollege) {
            return context.localObject(currentCollege)
        }
        
        College college = getCurrentWebSite(request, context).college
        request.setAttribute(CURRENT_COLLEGE, college)
        return college
    }

    static WebSite getCurrentWebSite(Request request, ObjectContext context) {
        WebSite currentWebSite = (WebSite) request.getAttribute(CURRENT_WEB_SITE)
        if (currentWebSite) {
            return context.localObject(currentWebSite)
        }

        WebSite site = new GetWebSite(request.getHeader(SITE_KEY_HEADER), context).get()
        request.setAttribute(CURRENT_WEB_SITE, site)
        return site
    }
}
