package ish.oncourse.portal.access;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.validate.AccessLinksValidatorFactory;
import ish.oncourse.portal.annotations.UserRole;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.PageLinkTransformer;
import ish.oncourse.services.cookies.ICookiesOverride;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class AccessController implements Dispatcher {

	public final static String LOGIN_PAGE = "/login";
	private final static String FORGOT_PASSWORD_PAGE = "/forgotpassword";
	private final static String CREATE_ACCOUNT_PAGE = "/createaccount";
	private final static String PASSWORD_RECOVERY_PAGE = "/passwordrecovery";
	private final static String SELECT_COLLEGE_PAGE = "/selectcollege";
	private final static String CALENDAR_FILE = "/calendar";
	private final static String UNSUBSCRIBE_PAGE = "/unsubscribe";

    private final static String[] RESOURCES = {"/css","/js","/img","/fonts",
			"/certificate/img","/certificate/js","/certificate/css"};


	@Inject
	private IAuthenticationService authenticationService;

	@Inject
	private ComponentClassResolver resolver;

	@Inject
	private ComponentSource componentSource;

	@Inject
	private ICookiesService cookieService;
	
	@Inject
	private ICookiesOverride cookie;
	
    @Inject
    private IPortalService portalService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private AccessLinksValidatorFactory accessLinksValidatorFactory;

	@Inject
	private HttpServletRequest httpRequest;

	private boolean isResource(String path) {
		for (String resource : RESOURCES) {
			if (path.toLowerCase().startsWith(resource))
				return true;
		}
		return false;
	}

	private boolean isCertificate(String path) {
		Matcher matcher = PageLinkTransformer.REGEXP_CERTIFICATE_PATH.matcher(path.toLowerCase());
		if (matcher.matches()) {
			return true;
		}
		return false;
	}


	private boolean isUsi(String path) {
		Matcher matcher = PageLinkTransformer.REGEXP_USI_PATH.matcher(path.toLowerCase());
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	private boolean isPublicPage(String path) {
		return path.equals(LOGIN_PAGE) ||
				path.equals(FORGOT_PASSWORD_PAGE) ||
				path.equals(CREATE_ACCOUNT_PAGE) ||
				path.startsWith(PASSWORD_RECOVERY_PAGE) ||
				path.equals(SELECT_COLLEGE_PAGE) ||
				path.startsWith(CALENDAR_FILE) ||
				path.startsWith(UNSUBSCRIBE_PAGE);
	}

	private String getPageName(String path) {
		int nextSlashX = path.length();
		String pageName;
		while (true) {
			pageName = path.substring(1, nextSlashX);

			if (!pageName.endsWith("/") && resolver.isPageName(pageName)) {
				return pageName;
			}

			nextSlashX = path.lastIndexOf('/', nextSlashX - 1);

			if (nextSlashX <= 1) {
				return null;
			}
		}
	}

	@Override
	public boolean dispatch(Request request, Response response)
			throws IOException {

		String path = request.getPath();

		if (isResource(path)) {
			return true;
		}

		if (isUsi(path)) {
			return false;
		}

		if (isCertificate(path)) {
			return false;
		}

		boolean processed = ProcessSignedRequest.valueOf(authenticationService, accessLinksValidatorFactory, cayenneService.newContext(),
				httpRequest,
				request, cookie).process();

		if (processed) {
			return false;
		} else {
			return processCommonRequest(request, response, path);
		}
	}

	private boolean processCommonRequest(Request request, Response response, String path) throws IOException {
		String pageName = getPageName(path);
		if (pageName == null) {
			return false;
		}

		Component page = componentSource.getPage(pageName);

		Contact contact = portalService.getContact();

		if (page != null) {
			String loginPath = request.getContextPath() + LOGIN_PAGE;

			if (contact == null) {
				if (!isPublicPage(path)) {
					cookieService.pushPreviousPagePath(path);
					response.sendRedirect(loginPath);
					return true;
				} else {
					return false;
				}
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
