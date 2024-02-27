/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring.model

import ish.oncourse.server.monitoring.model.datasource.ConnectionStatistic
import ish.oncourse.server.monitoring.model.datasource.HikariConfig
import ish.oncourse.server.monitoring.model.memory.MemoryStatistic
import ish.oncourse.server.monitoring.model.system.SystemStatistic

class MonitoringStatictic {

    private ConnectionStatistic connectionStatistic
    private MemoryStatistic memoryStatistic
    private HikariConfig hikariConfig
    private SystemStatistic systemStatistic
    private Properties environmentProperties
    private Map networkProperties
    private Map licenseProperties
    private Map onCourseProperties



    protected MonitoringStatictic(ConnectionStatistic connectionStatistic, MemoryStatistic memoryStatistic,
                                  HikariConfig hikariConfig, SystemStatistic systemStatistic,
                                  Properties environmentProperties, Map networkProperties, Map licenseProperties,
                                  Map onCourseProperties) {
        this.connectionStatistic = connectionStatistic
        this.memoryStatistic = memoryStatistic
        this.hikariConfig = hikariConfig
        this.systemStatistic = systemStatistic
        this.environmentProperties = environmentProperties
        this.networkProperties = networkProperties
        this.licenseProperties = licenseProperties
        this.onCourseProperties = onCourseProperties
    }

    ConnectionStatistic getConnectionStatistic() {
        return connectionStatistic
    }

    MemoryStatistic getMemoryStatistic() {
        return memoryStatistic
    }

    HikariConfig getHikariConfig() {
        return hikariConfig
    }

    SystemStatistic getSystemStatistic() {
        return systemStatistic
    }

    Properties getEnvironmentProperties() {
        return environmentProperties
    }

    Map getNetworkProperties() {
        return networkProperties
    }

    Map getLicenseProperties() {
        return licenseProperties
    }

    Map getOnCourseProperties() {
        return onCourseProperties
    }

    static class Builder {

        private ConnectionStatistic connectionStatistic
        private MemoryStatistic memoryStatistic
        private HikariConfig hikariConfig
        private SystemStatistic systemStatistic
        private Properties environmentProperties
        private Map networkProperties
        private Map licenseProperties
        private Map onCourseProperties

        Builder setConnectionStatistic(ConnectionStatistic connectionStatistic) {
            this.connectionStatistic = connectionStatistic
            return this
        }

        Builder setMemoryStatistic(MemoryStatistic memoryStatistic) {
            this.memoryStatistic = memoryStatistic
            return this
        }

        Builder setHikariConfig(HikariConfig hikariConfig) {
            this.hikariConfig = hikariConfig
            return this
        }

        Builder setSystemStatistic(SystemStatistic systemStatistic) {
            this.systemStatistic = systemStatistic
            return this
        }

        Builder setEnvironmentProperties(Properties environmentProperties) {
            this.environmentProperties = environmentProperties
            return this
        }

        Builder setNetworkProperties(Map networkProperties) {
            this.networkProperties = networkProperties
            return this
        }

        Builder setLicenseProperties(Map licenseProperties) {
            this.licenseProperties = licenseProperties
            return this
        }

        Builder setOnCourseProperties(Map onCourseProperties) {
            this.onCourseProperties = onCourseProperties
            return this
        }

        MonitoringStatictic build() {
            return new MonitoringStatictic(connectionStatistic, memoryStatistic, hikariConfig, systemStatistic,
                    environmentProperties, networkProperties, licenseProperties, onCourseProperties)
        }
    }
}
