/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.monitoring.servlet

import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.monitoring.MonitoringService
import ish.oncourse.server.monitoring.MonitoringStatisticService
import ish.oncourse.server.monitoring.model.MonitoringStatictic
import ish.oncourse.server.monitoring.model.datasource.ConnectionStatistic
import ish.oncourse.server.monitoring.model.datasource.HikariConfig
import ish.oncourse.server.monitoring.model.memory.MemoryStatistic
import ish.oncourse.server.monitoring.model.system.SystemStatistic
import ish.oncourse.server.monitoring.service.HtmlPrinter
import ish.oncourse.server.monitoring.util.MonitoringPrinter

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

	private final MonitoringStatisticService monitoringStatisticService

	MonitoringServlet(MonitoringService monitoringService, MonitoringStatisticService monitoringStatisticService) {
		this.monitoringService = monitoringService
		this.monitoringStatisticService = monitoringStatisticService
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
		if (monitoringService.isEnable()) {

			StringBuilder output = new StringBuilder(MonitoringPrinter.printText(ResourcesUtil.getReleaseVersionString()))

			// display datasource connection/memory/hikari config/system statistic/environment props/network props/license props/onCourse props
			ConnectionStatistic connectionStatistic = monitoringStatisticService.getDatabaseConnectionStatictic("angel")
			MemoryStatistic memoryStatistic = monitoringStatisticService.getMemoryStatictic()
			HikariConfig currentHikariConfig = monitoringStatisticService.getHikariConfig("angel")
			SystemStatistic systemStatistic = monitoringStatisticService.getSystemStatictic()
			Properties environmentProperties = monitoringStatisticService.getEnvironmentProperties()
			Map networkProperties = monitoringStatisticService.getNetworkProperties()
			Map licenseProperties = monitoringStatisticService.getLicenseProperties()
			Map onCourseProperties = monitoringStatisticService.getOnCourseProperties()

			output.append(new HtmlPrinter().print(new MonitoringStatictic.Builder()
					.setConnectionStatistic(connectionStatistic)
					.setMemoryStatistic(memoryStatistic)
					.setHikariConfig(currentHikariConfig)
					.setSystemStatistic(systemStatistic)
					.setEnvironmentProperties(environmentProperties)
					.setNetworkProperties(networkProperties)
					.setLicenseProperties(licenseProperties)
					.setOnCourseProperties(onCourseProperties)
					.build()
			))

			response.setHeader('Content-Type', 'text/html')
			response.outputStream.write("<div style=\"font-family: monospace;\">${output.toString()}</div>".getBytes())
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND)
		}
	}
}
