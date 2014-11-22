package ish.oncourse.portal.access;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PageLinkTransformer;
import ish.oncourse.services.cookies.ICookiesService;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class AccessController implements Dispatcher {

	private final static String LOGIN_PAGE = "/login";
	private final static String FORGOT_PASSWORD_PAGE = "/forgotpassword";
	private final static String PASSWORD_RECOVERY_PAGE = "/passwordrecovery";
	private final static String SELECT_COLLEGE_PAGE = "/selectcollege";
	private final static String CALENDAR_FILE = "/calendar";
	private final static String UNSUBSCRIBE_PAGE = "/unsubscribe";

    private final static String[] RESOURCES = {"/css","/js","/img","/fonts"};


	@Inject
	private IAuthenticationService authenticationService;

	@Inject
	private ComponentClassResolver resolver;

	@Inject
	private ComponentSource componentSource;

	@Inject
	private ICookiesService cookieService;

    @Inject
    private IPortalService portalService;

	public boolean dispatch(Request request, Response response)
			throws IOException {

		String path = request.getPath();

        for (String resource : RESOURCES) {
            if (path.toLowerCase().startsWith(resource))
                return true;
        }

        Matcher matcher = PageLinkTransformer.REGEXP_USI_PATH.matcher(path.toLowerCase());
        if (matcher.matches()) {
            return false;
        }

		int nextslashx = path.length();

		String pageName;

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

        Contact contact = portalService.getContact();

		if (page != null) {
			String loginPath = request.getContextPath() + LOGIN_PAGE;

			if (contact == null) {
				if (!path.equals(LOGIN_PAGE) && !path.equals(FORGOT_PASSWORD_PAGE) && !path.startsWith(PASSWORD_RECOVERY_PAGE) && !path.equals(SELECT_COLLEGE_PAGE) &&
						!path.startsWith(CALENDAR_FILE) && !path.startsWith(UNSUBSCRIBE_PAGE)) {
					cookieService.pushPreviousPagePath(path);
					response.sendRedirect(loginPath);
					return true;
				}
				return false;
			} else {
				UserRole pageWithUserRole = page.getClass().getAnnotation(UserRole.class);

				if (pageWithUserRole != null) {
					boolean canAccess = true;

					if (pageWithUserRole.value() != null) {
						Set<String> pageRoles = new HashSet<>(Arrays.asList(pageWithUserRole.value()));
						if (pageRoles.size() > 0) {
							canAccess = (pageRoles.contains("tutor") && contact.getTutor() != null) || (pageRoles.contains("student") && contact.getStudent() != null);
						} else {
							canAccess = contact.getStudent() != null;
						}
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
