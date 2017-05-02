package ish.oncourse.willow.filters

import groovy.transform.CompileStatic
import ish.oncourse.willow.service.CollegeInfo
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter

@CollegeInfo
@CompileStatic
class RequestFilter implements ContainerRequestFilter {
    
    static final ThreadLocal<String> ThreadLocalXOrigin = new ThreadLocal<String>()
    
    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        List<String> hosts = requestContext.headers.get('X-Origin')
        if (!hosts)
            throw new NullPointerException("X-Origin should be not null.")
        if (hosts.size() != 1) {
            throw new IllegalArgumentException("X-Origin should be single value")
        }
        ThreadLocalXOrigin.set(hosts.get(0))
    }
}
