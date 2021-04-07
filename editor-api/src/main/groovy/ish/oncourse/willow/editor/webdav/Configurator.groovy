package ish.oncourse.willow.editor.webdav

import com.google.inject.Injector
import io.milton.http.Auth
import io.milton.http.HttpManager
import io.milton.http.Request
import io.milton.http.http11.auth.DigestResponse
import io.milton.resource.Resource
import io.milton.servlet.Config
import io.milton.servlet.DefaultMiltonConfigurator
import ish.oncourse.services.authentication.AuthenticationStatus
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.services.access.AuthenticationService
import ish.oncourse.willow.editor.services.access.UserService

import javax.servlet.ServletException

import static ish.oncourse.willow.editor.webdav.WebdavModule.INJECTOR_PROPERTY

class Configurator extends DefaultMiltonConfigurator {
    
    @Override
    HttpManager configure(Config config) throws ServletException {
        Injector injector = config.servletContext.getAttribute(INJECTOR_PROPERTY) as Injector
        AuthenticationService authenticationService = injector.getInstance(AuthenticationService)
        UserService userService = injector.getInstance(UserService)
        ICayenneService cayenneService =  injector.getInstance(ICayenneService)
        RequestService requestService =  injector.getInstance(RequestService)

        builder.mainResourceFactory = new RootResourceFactory(new EditorSecurityManager(authenticationService), userService, cayenneService, requestService)
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
            return AuthenticationStatus.SUCCESS == authenticationService.authenticate(user, password, false).status ? user : null
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
