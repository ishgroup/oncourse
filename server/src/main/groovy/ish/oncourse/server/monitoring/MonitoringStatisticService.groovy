/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring

import javax.inject.Inject
import com.zaxxer.hikari.HikariConfigMXBean
import com.zaxxer.hikari.HikariPoolMXBean
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.servlet.ISessionManager
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.http.HttpFactory
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.monitoring.model.datasource.ConnectionStatistic
import ish.oncourse.server.monitoring.model.datasource.HikariConfig
import ish.oncourse.server.monitoring.model.memory.MemoryStatistic
import ish.oncourse.server.monitoring.model.system.SystemStatistic
import org.apache.cayenne.query.ObjectSelect

import javax.management.JMX
import javax.management.MBeanServer
import javax.management.ObjectName
import java.lang.management.ManagementFactory
import java.lang.management.MemoryPoolMXBean

class MonitoringStatisticService {

    private static final List<String> notHeapMemoryPools = Arrays.asList(
            "Metaspace",
            "Compressed Class Space",
    )

    private final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer()

    @Inject
    private final HttpFactory httpFactory
    @Inject
    private final LicenseService licenseService
    @Inject
    private final PreferenceController preferenceController
    @Inject
    private final ISessionManager sessionManager
    @Inject
    private final ICayenneService cayenneService

    MemoryStatistic getMemoryStatictic() {
        long notHeapMemory = 0
        notHeapMemoryPools.forEach { pool ->
            ObjectName poolName = new ObjectName("java.lang:type=MemoryPool,name=${pool}")
            MemoryPoolMXBean poolProxy = JMX.newMXBeanProxy(mBeanServer, poolName, MemoryPoolMXBean.class)
            notHeapMemory += poolProxy.getUsage().used
        }

        Runtime runtime = Runtime.getRuntime()
        long maxHeapSize = runtime.maxMemory()
        long currentHeapSize = runtime.totalMemory()
        long freeMemory = runtime.freeMemory()

        return new MemoryStatistic.Builder()
                .setMaxHeapSize(maxHeapSize)
                .setCurrentHeapSize(currentHeapSize)
                .setUsedHeapMemory(currentHeapSize - freeMemory)
                .setNotHeapMemory(notHeapMemory)
                .build()
    }

    ConnectionStatistic getDatabaseConnectionStatictic(String datasourseName) {
        ObjectName poolName = new ObjectName("com.zaxxer.hikari:type=Pool (${datasourseName})");
        HikariPoolMXBean poolProxy = JMX.newMXBeanProxy(mBeanServer, poolName, HikariPoolMXBean.class)

        return new ConnectionStatistic.Builder()
                .setTotalConnections(poolProxy.totalConnections)
                .setActiveConnections(poolProxy.activeConnections)
                .setIdleConnections(poolProxy.idleConnections)
                .setThreadsAwaitingConnection(poolProxy.threadsAwaitingConnection)
                .build()
    }

    HikariConfig getHikariConfig(String datasourseName) {
        ObjectName poolName = new ObjectName("com.zaxxer.hikari:type=PoolConfig (${datasourseName})")
        HikariConfigMXBean configProxy = JMX.newMXBeanProxy(mBeanServer, poolName, HikariConfigMXBean.class)

        return new HikariConfig.Builder()
                .setConnectionTimeout(configProxy.connectionTimeout)
                .setIdleTimeout(configProxy.idleTimeout)
                .setIdleTimeout(configProxy.idleTimeout)
                .setMaxLifetime(configProxy.maxLifetime)
                .setMinimumIdle(configProxy.minimumIdle)
                .setMaximumPoolSize(configProxy.maximumPoolSize)
                .setLeakDetectionThreshold(configProxy.leakDetectionThreshold)
                .build()
    }


    static SystemStatistic getSystemStatictic() {
        def operatingSystemMxBean = ManagementFactory.getOperatingSystemMXBean()
        double loadAverage = operatingSystemMxBean.systemLoadAverage
        int numberOfCPU = operatingSystemMxBean.availableProcessors
        int currentThreadsNumber = Thread.activeCount()

        return new SystemStatistic.Builder()
                .setAverageCpuLoad(loadAverage)
                .setNumberOfCpu(numberOfCPU)
                .setActiveThreadsCount(currentThreadsNumber)
                .build()
    }

    static Properties getEnvironmentProperties() {
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

    Map getNetworkProperties() {
        Map properties = new HashMap()
        properties.put("ip", httpFactory.getIp())
        properties.put("port", httpFactory.getPort())
        properties.put("path", httpFactory.getPath())
        return properties
    }

    Map getLicenseProperties() {
        Map properties = new HashMap()
        properties.put("users.max", licenseService.getMax_concurrent_users())
        properties.put("users.current", sessionManager.getLoggedInUsersCount(preferenceController.getTimeoutThreshold()))
        properties.put("college_key", licenseService.getCollege_key())
        properties.put("custom_scripts", licenseService.getLisense("license.scripting"))
        properties.put("access_control", licenseService.getLisense("license.access_control"))
        return properties
    }

    Map getOnCourseProperties() {
        def context = cayenneService.getNewReadonlyContext()
        def enrolmentsCount = ObjectSelect.query(Enrolment.class).selectCount(context)
        Map properties = new HashMap()
        properties.put("version", ResourcesUtil.getReleaseVersionString())
        properties.put("enrolments.count", enrolmentsCount)
        return properties
    }
}
