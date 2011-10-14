/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.linktransform;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestHandler;

/**
 * Deprecated, because we don't need to rewrite context path with the ROOT app
 * @author ksenia
 *
 */
@Deprecated
public class URLRewriteRequestFilter implements HttpServletRequestFilter {
	public static final String WEB_FOLDER_PATH = "/web";

	public boolean service(HttpServletRequest request, HttpServletResponse response, HttpServletRequestHandler handler) throws IOException {

		String path = request.getServletPath();
		if (path.startsWith(WEB_FOLDER_PATH)) {
			request = new URLRewriteRequestWrapper(request, WEB_FOLDER_PATH);
		}

		handler.service(request, response);

		return true;
	}
}
