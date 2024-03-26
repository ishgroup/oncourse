/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring.impl

import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.monitoring.MonitoringService

/**
 * Monitoring service which provides monitoring metrics: time, network, environment, license, systemRuntime, onCourse version
 */
@API
@CompileStatic
class MonitoringServiceImpl implements MonitoringService {
    private String userName
    private String password

    MonitoringServiceImpl(String user, String password) {
        userName = user
        this.password = password
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
