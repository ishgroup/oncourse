/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.website.linktransforms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class URLRewriteRequestWrapper extends HttpServletRequestWrapper {

	private static final String EMPTY_STR = "";
	private static final String WEB_FOLDER_PATH = "/web";

	public URLRewriteRequestWrapper(HttpServletRequest request) {
		super(request);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequestWrapper#getPathInfo()
	 */
	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		String pathInfo = super.getPathInfo();
		return pathInfo == null ? null : pathInfo.replace(WEB_FOLDER_PATH, EMPTY_STR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequestWrapper#getServletPath()
	 */
	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		String servletPath = super.getServletPath();
		return servletPath == null ? null : servletPath.replace(WEB_FOLDER_PATH, EMPTY_STR);
	}

}
