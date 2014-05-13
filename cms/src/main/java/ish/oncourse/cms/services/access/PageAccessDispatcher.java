package ish.oncourse.cms.services.access;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

import java.io.IOException;
import java.io.OutputStream;

public class PageAccessDispatcher implements Dispatcher {

	private static final String LOGIN_PAGE = "/login";

	public static String[] NON_REDIRECTABLE_PATHS = new String[] { LOGIN_PAGE, "/assets", "/ui/", "/ma.", "/webdav" };

	@Inject
	private IAuthenticationService authenticationService;

	public boolean dispatch(Request request, Response response) throws IOException {

		String path = request.getPath();
		for (String p : NON_REDIRECTABLE_PATHS) {
			if (path.startsWith(p)) {
				return false;
			}
		}
		if (path.startsWith("/checksession")) {
			OutputStream os = response.getOutputStream("text/html;charset=UTF-8");
			if (request.getSession(false) != null) {
				os.write("session alive".getBytes());
			} else {
				os.write("session timeout".getBytes());
			}
			os.flush();
			return true;
		}
		if (authenticationService.getUser() == null && authenticationService.getSystemUser() == null) {
			String loginPath = request.getContextPath() + LOGIN_PAGE;
			response.sendRedirect(loginPath);
			return true;
		}

		return false;
	}
}
