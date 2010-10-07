package ish.oncourse.cms.services.access;

import ish.oncourse.services.security.IAuthenticationService;

import java.io.IOException;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

public class PageAccessDispatcher implements Dispatcher {

	private static final String LOGIN_PAGE = "login";

	@Inject
	private ComponentClassResolver resolver;

	@Inject
	private IAuthenticationService authenticationService;

	public boolean dispatch(Request request, Response response)
			throws IOException {

		String path = request.getPath();
		if (path.equals("")) {
			return false;
		}

		int nextSlash = path.length();
		String pageName;

		while (true) {
			pageName = path.substring(1, nextSlash);
			if (!pageName.endsWith("/") && resolver.isPageName(pageName)) {
				break;
			}
			nextSlash = path.lastIndexOf('/', nextSlash - 1);
			if (nextSlash <= 1) {
				return false;
			}
		}

		return checkAccess(pageName, request, response);
	}

	/**
	 * 
	 * @param pageName
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public boolean checkAccess(final String pageName, final Request request,
			final Response response) throws IOException {

		boolean hasAccess = false;

		// all but login page must be accessed authenticated...
		boolean loginPage = LOGIN_PAGE.equalsIgnoreCase(pageName);

		// redirect to login
		if (!loginPage && authenticationService.getUser() == null) {
			String loginPath = "http://" + request.getServerName() + request.getContextPath() + "/" + LOGIN_PAGE;
			response.sendRedirect(loginPath);
			hasAccess = true;
		} else if (loginPage && authenticationService.getUser() != null) {
			String homePage = "http://" + request.getServerName() + request.getContextPath() + "/";
			response.sendRedirect(homePage);
			hasAccess = true;
		}

		return hasAccess;
	}

}
