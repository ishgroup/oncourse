package ish.oncourse.willow.filters

import ish.oncourse.willow.service.XValidate

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter

@XValidate
class XValidateFilter implements ContainerRequestFilter {

    public static final String X_VALIDATE_HEADER = 'xValidateOnly'

    static final ThreadLocal<Boolean> ThreadLocalValidateOnly = new ThreadLocal<Boolean>()

    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        String value = requestContext.headers.get(X_VALIDATE_HEADER)
        ThreadLocalValidateOnly.set(Boolean.valueOf(value))
    }
}
