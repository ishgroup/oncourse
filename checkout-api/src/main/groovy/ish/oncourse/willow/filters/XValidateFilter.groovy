package ish.oncourse.willow.filters

import ish.oncourse.willow.service.XValidate

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter

@XValidate
class XValidateFilter implements ContainerRequestFilter {

    public static final String X_VALIDATE_HEADER = 'xValidateOnly'

    public static final ThreadLocal<Boolean> ThreadLocalValidateOnly = new ThreadLocal<Boolean>()

    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        List<String> value = requestContext.headers.get(X_VALIDATE_HEADER)
        if (value && !value.empty) {
            ThreadLocalValidateOnly.set(Boolean.valueOf(value[0]))
        } else {
            ThreadLocalValidateOnly.set(false)
        }
    }
}
