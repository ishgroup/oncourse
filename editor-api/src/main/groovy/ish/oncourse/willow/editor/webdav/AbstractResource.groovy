package ish.oncourse.willow.editor.webdav

import io.milton.http.Auth
import io.milton.http.Request
import io.milton.http.SecurityManager
import io.milton.resource.Resource

abstract class AbstractResource implements Resource {

    private SecurityManager securityManager

    AbstractResource(SecurityManager securityManager) {
        this.securityManager = securityManager
    }

    @Override
    Object authenticate(String user, String requestedPassword) {
        securityManager.authenticate(user, requestedPassword)
    }

    @Override
    String checkRedirect(Request request) {
        return null
    }

    @Override
    boolean authorise(Request request, Request.Method method, Auth auth) {
        return securityManager.authorise(request, method, auth, this)
    }

    @Override
    String getRealm() {
        return securityManager.getRealm(null)
    }
}
