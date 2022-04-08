/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.monitoring.servlet

import com.fasterxml.jackson.databind.ObjectMapper
import ish.oncourse.server.monitoring.MonitoringModel
import ish.oncourse.server.monitoring.MonitoringService

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Return the onCourse monitoring metrics.
 *
 * This servlet use basic-auth.
 */
class MonitoringServlet extends HttpServlet {
	public static String MONITORING_PATH = "/monitoring"

	private final MonitoringService monitoringService

	MonitoringServlet(MonitoringService monitoringService) {
		this.monitoringService = monitoringService
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
		if (monitoringService.isEnable()) {
			def mapper = new ObjectMapper()
			response.setContentType("application/json")
			response.setCharacterEncoding("UTF-8")
			MonitoringModel monitoringMetrics = monitoringService.getMonitoringMetrics()
			response.getWriter().print(mapper.writeValueAsString(monitoringMetrics))
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND)
		}
	}
}
