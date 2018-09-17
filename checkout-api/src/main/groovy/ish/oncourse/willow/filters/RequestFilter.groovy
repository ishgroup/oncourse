package ish.oncourse.willow.filters

import groovy.transform.CompileStatic
import ish.oncourse.willow.service.CollegeInfo
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter

import static ish.oncourse.services.site.GetWebSite.SITE_KEY_HEADER

@CollegeInfo
@CompileStatic
class RequestFilter implements ContainerRequestFilter {

    static final ThreadLocal<String> ThreadLocalSiteKey = new ThreadLocal<String>()
    
    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        List<String> siteKeys = requestContext.headers.get(SITE_KEY_HEADER)
        if (!siteKeys) {
            throw new NullPointerException("'${SITE_KEY_HEADER}' header should be not null. Application use this for college loading.")
        } else if (siteKeys.size() != 1) {
            throw new IllegalArgumentException("'${SITE_KEY_HEADER}' should be single value")
        }
        ThreadLocalSiteKey.set(siteKeys.get(0))
    }
}
