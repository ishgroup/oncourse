package ish.oncourse.willow.editor.webdav

import com.google.inject.Inject
import io.milton.http.Auth
import io.milton.http.HttpManager
import io.milton.http.Request
import io.milton.http.http11.auth.DigestResponse
import io.milton.resource.Resource
import io.milton.servlet.Config
import io.milton.servlet.DefaultMiltonConfigurator
import ish.oncourse.willow.editor.services.access.AuthenticationService
import ish.oncourse.willow.editor.services.access.AuthenticationStatus

import javax.servlet.ServletException

class Configurator extends DefaultMiltonConfigurator {
    
    @Inject
    AuthenticationService authenticationService
    
    
    @Override
    HttpManager configure(Config config) throws ServletException {
        
        builder.mainResourceFactory = new RootResourceFactory(new EditorSecurityManager(authenticationService), authenticationService)
        builder.enableCookieAuth = false
        builder.enabledJson = false

        return super.configure(config)
    }

    private static class EditorSecurityManager implements io.milton.http.SecurityManager {

        private AuthenticationService authenticationService

        EditorSecurityManager(AuthenticationService authenticationService) {
            this.authenticationService = authenticationService
        }

        @Override
        Object authenticate(DigestResponse digestRequest) {
            throw new UnsupportedOperationException("Digest authentication is not supported.")
        }

        @Override
        Object authenticate(String user, String password) {
            return AuthenticationStatus.SUCCESS == authenticationService.authenticate(user, password) ? user : null
        }

        @Override
        boolean authorise(Request request, Request.Method method, Auth auth, Resource resource) {
            return auth != null
        }

        @Override
        String getRealm(String host) {
            return 'oncourse'
        }

        @Override
        boolean isDigestAllowed() {
            return false
        }
    }
}
