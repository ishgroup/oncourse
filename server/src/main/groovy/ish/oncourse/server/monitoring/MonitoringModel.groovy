/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring

class MonitoringModel {
    private Map time
    private Map network
    private Properties environment
    private Map license
    private Map systemRuntime
    private Map onCourse

    Map getTime() {
        return time
    }

    void setTime(Map time) {
        this.time = time
    }

    Map getNetwork() {
        return network
    }

    void setNetwork(Map network) {
        this.network = network
    }

    Properties getEnvironment() {
        return environment
    }

    void setEnvironment(Properties environment) {
        this.environment = environment
    }

    Map getLicense() {
        return license
    }

    void setLicense(Map license) {
        this.license = license
    }

    Map getSystemRuntime() {
        return systemRuntime
    }

    void setSystemRuntime(Map systemRuntime) {
        this.systemRuntime = systemRuntime
    }

    Map getOnCourse() {
        return onCourse
    }

    void setOnCourse(Map onCourse) {
        this.onCourse = onCourse
    }
}
