package ish.oncourse.website.linktransforms;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestHandler;

public class URLRewriteRequestFilter implements HttpServletRequestFilter {
	private static final String WEB_FOLDER_PATH = "/web";

	public boolean service(HttpServletRequest request,
			HttpServletResponse response, HttpServletRequestHandler handler)
			throws IOException {

		String path = request.getServletPath();
		if (path.startsWith(WEB_FOLDER_PATH)) {
			request = new URLRewriteRequestWrapper(request);
		}

		handler.service(request, response);

		return true;
	}
}
