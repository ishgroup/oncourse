/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.servlet;

import ish.oncourse.common.ResourcesUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Return the onCourse version. Useful as a health check to see that the application is running.
 *
 * This servlet it outside the authentication mechanisn, so returns without any login checks.
 */
public class HealthCheckServlet extends HttpServlet {

	public static final String PATH = "/healthcheck";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.getWriter().print(ResourcesUtil.getReleaseVersionString());
	}
}
