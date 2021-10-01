package ish.oncourse.willow.filters

import groovy.transform.CompileStatic
import ish.oncourse.willow.service.CollegeInfo
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter

import static ish.oncourse.services.site.GetWebSite.SITE_KEY_HEADER

@CompileStatic
@CollegeInfo
class RequestFilter implements ContainerRequestFilter {

    public static final String PAYER_ID_HEADER = 'payerId'

    public static final ThreadLocal<String> ThreadLocalSiteKey = new ThreadLocal<String>()
    public static final ThreadLocal<Long> ThreadLocalPayerId = new ThreadLocal<Long>()

    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        List<String> siteKeys = requestContext.headers.get(SITE_KEY_HEADER)
        if (siteKeys && !siteKeys.empty) {
            ThreadLocalSiteKey.set(siteKeys[0])
        }

        List<String> payerIds = requestContext.headers.get(PAYER_ID_HEADER)
        if (payerIds && !payerIds.empty) {
            ThreadLocalPayerId.set(Long.valueOf(payerIds[0]))
        }


    }
}
