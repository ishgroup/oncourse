/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring

import io.bootique.annotation.BQConfigProperty
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.servlet.ISessionManager
import ish.oncourse.server.http.HttpFactory
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.monitoring.impl.MonitoringServiceImpl

class MonitoringServiceFactory {
    private String user
    private String password

    String getUser() {
        return user
    }

    @BQConfigProperty
    void setUser(String user) {
        this.user = user
    }

    String getPassword() {
        return password
    }

    @BQConfigProperty
    void setPassword(String password) {
        this.password = password
    }

    MonitoringService createMonitoringService(ISessionManager sessionManager, PreferenceController preferenceController, LicenseService licenseService, HttpFactory httpFactory, ICayenneService cayenneService) {
        return new MonitoringServiceImpl(sessionManager, preferenceController, licenseService, httpFactory, cayenneService, user, password)
    }
}
