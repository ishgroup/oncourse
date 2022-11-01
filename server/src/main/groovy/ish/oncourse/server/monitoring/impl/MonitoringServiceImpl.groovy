/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring.impl

import com.sun.management.OperatingSystemMXBean
import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.servlet.ISessionManager
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.http.HttpFactory
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.monitoring.MonitoringModel
import ish.oncourse.server.monitoring.MonitoringService
import org.apache.cayenne.query.ObjectSelect
import org.eclipse.jetty.security.ConstraintMapping
import org.eclipse.jetty.security.ConstraintSecurityHandler
import org.eclipse.jetty.security.HashLoginService
import org.eclipse.jetty.security.SecurityHandler
import org.eclipse.jetty.security.UserStore
import org.eclipse.jetty.security.authentication.BasicAuthenticator
import org.eclipse.jetty.util.security.Constraint
import org.eclipse.jetty.util.security.Credential

import java.lang.management.ManagementFactory
import java.math.RoundingMode
import java.time.Instant

/**
 * Monitoring service which provides monitoring metrics: time, network, environment, license, systemRuntime, onCourse version
 */
@API
@CompileStatic
class MonitoringServiceImpl implements MonitoringService {
    private String userName
    private String password

    private final ISessionManager sessionManager
    private final PreferenceController preferenceController
    private final LicenseService licenseService
    private final HttpFactory httpFactory
    private final ICayenneService cayenneService

    MonitoringServiceImpl(ISessionManager sessionManager, PreferenceController preferenceController, LicenseService licenseService, HttpFactory httpFactory, ICayenneService cayenneService, String user, String password) {
        this.sessionManager = sessionManager
        this.preferenceController = preferenceController
        this.licenseService = licenseService
        this.httpFactory = httpFactory
        this.cayenneService = cayenneService
        userName = user
        this.password = password
    }

    @Override
    MonitoringModel getMonitoringMetrics() {
        return new MonitoringModel().with { m ->
            m.time = getTimeProperties()
            m.network = getNetworkProperties()
            m.environment = getEnvironmentProperties()
            m.license = getLicenseProperties()
            m.systemRuntime = getSystemRuntimeProperties()
            m.onCourse = getOnCourseProperties()
            m
        }
    }

    private static Map getTimeProperties() {
        Map properties = new HashMap()
        properties.put("timezone", TimeZone.getDefault())
        properties.put("current", Instant.now().toEpochMilli())
        properties.put("iso8601", Instant.now().toString())
        properties.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime())
        return properties
    }

    private Map getNetworkProperties() {
        Map properties = new HashMap()
        properties.put("ip", httpFactory.getIp())
        properties.put("port", httpFactory.getPort())
        properties.put("path", httpFactory.getPath())
        return properties
    }

    private static Properties getEnvironmentProperties() {
        Properties properties = System.getProperties()
        return properties.findAll {
            def propName = it.getKey()
            return (propName.equals("java.vendor") || propName.equals("java.version") || propName.equals("java.vm.info") ||
                    propName.equals("java.vm.name") || propName.equals("java.vm.specification.name") ||
                    propName.equals("java.vm.specification.vendor") || propName.equals("java.vm.specification.version") ||
                    propName.equals("java.vm.vendor") || propName.equals("java.vm.version") ||
                    propName.equals("os.arch") || propName.equals("os.name") || propName.equals("os.version"))
        } as Properties
    }

    private Map getLicenseProperties() {
        Map properties = new HashMap()
        properties.put("users.max", licenseService.getMax_concurrent_users())
        properties.put("users.current", sessionManager.getLoggedInUsersCount(preferenceController.getTimeoutThreshold()))
        properties.put("college_key", licenseService.getCollege_key())
        properties.put("custom_scripts", licenseService.getLisense("license.scripting"))
        properties.put("access_control", licenseService.getLisense("license.access_control"))
        return properties
    }

    private static Map getSystemRuntimeProperties() {
        def runtime = Runtime.getRuntime()
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class)
        Map properties = new HashMap()
        properties.put("availableProcessors", runtime.availableProcessors())
        properties.put("heap.total", runtime.totalMemory())
        properties.put("heap.used", runtime.totalMemory() - runtime.freeMemory())
        properties.put("threads", ManagementFactory.getThreadMXBean().getThreadCount())
        properties.put("cpu.usage", new BigDecimal(osBean.getProcessCpuLoad()).setScale(2, RoundingMode.HALF_UP).doubleValue())
        return properties
    }

    private Map getOnCourseProperties() {
        def context = cayenneService.getNewReadonlyContext()
        def enrolmentsCount = ObjectSelect.query(Enrolment.class).selectCount(context)
        Map properties = new HashMap()
        properties.put("version", ResourcesUtil.getReleaseVersionString())
        properties.put("enrolments.count", enrolmentsCount)
        return properties
    }

    @Override
    boolean isEnable() {
        return userName != null && password != null
    }

    @Override
    String getUserName() {
        return userName
    }

    @Override
    String getPassword() {
        return password
    }
}
