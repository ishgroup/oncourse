package ish.oncourse.cms.services.access;

import java.io.IOException;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

public class PageAccessDispatcher implements Dispatcher {

	private static final String LOGIN_PAGE = "/login";

	public static String[] NON_REDIRECTABLE_PATHS = new String[] { LOGIN_PAGE, "/assets", "/ui/" };

	@Inject
	private IAuthenticationService authenticationService;

	public boolean dispatch(Request request, Response response) throws IOException {

		String path = request.getPath();
		for (String p : NON_REDIRECTABLE_PATHS) {
			if (path.startsWith(p)) {
				return false;
			}
		}
		if (authenticationService.getUser() == null) {
			String loginPath = request.getContextPath() + LOGIN_PAGE;
			response.sendRedirect(loginPath);
			return true;
		}

		return false;
	}
}
