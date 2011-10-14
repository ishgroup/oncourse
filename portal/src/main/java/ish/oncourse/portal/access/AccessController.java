package ish.oncourse.portal.access;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.services.cookies.ICookiesService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

public class AccessController implements Dispatcher {

    private final static String LOGIN_PAGE = "/login";

    @Inject
    private IAuthenticationService authenticationService;

    @Inject
    private ComponentClassResolver resolver;

    @Inject
    private ComponentSource componentSource;
    
    @Inject
    private ICookiesService cookieService;
 

    public boolean dispatch(Request request, Response response) throws IOException {

        String path = request.getPath();

        int nextslashx = path.length();

        String pageName = null;

        while (true) {
            pageName = path.substring(1, nextslashx);

            if (!pageName.endsWith("/") && resolver.isPageName(pageName)) {
                break;
            }

            nextslashx = path.lastIndexOf('/', nextslashx - 1);

            if (nextslashx <= 1) {
                return false;
            }
        }

        Component page = componentSource.getPage(pageName);

        if (page != null) {
            String loginPath = request.getContextPath() + LOGIN_PAGE;

            UserRole pageWithUserRole = page.getClass().getAnnotation(UserRole.class);

            if (pageWithUserRole != null) {
                if (authenticationService.getUser() == null) {
                	cookieService.pushPreviousPagePath(path);
                    response.sendRedirect(loginPath);
                    return true;
                } else {
                    Contact user = authenticationService.getUser();

                    boolean canAccess = true;

                    if (pageWithUserRole != null && pageWithUserRole.value() != null) {
                        Set<String> pageRoles = new HashSet<String>(Arrays.asList(pageWithUserRole.value()));
                        canAccess = (pageRoles.contains("tutor") && user.getTutor() != null) || (pageRoles.contains("student") && user.getStudent() != null);
                    }

                    if (!canAccess) {
                        response.sendRedirect(loginPath);
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
