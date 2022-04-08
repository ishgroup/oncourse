/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.modules

import com.google.inject.Binder
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.TypeLiteral
import io.bootique.ConfigModule
import io.bootique.config.ConfigurationFactory
import io.bootique.jetty.MappedServlet
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.api.servlet.ISessionManager
import ish.oncourse.server.http.HttpFactory
import ish.oncourse.server.jetty.AngelJettyModule
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.monitoring.MonitoringService
import ish.oncourse.server.monitoring.MonitoringServiceFactory
import ish.oncourse.server.monitoring.MonitoringServletContextHandlerExtender
import ish.oncourse.server.monitoring.servlet.MonitoringServlet

class MonitoringModule extends ConfigModule {

    private static final TypeLiteral<MappedServlet<MonitoringServlet>> MONITORING_SERVLET =
            new TypeLiteral<MappedServlet<MonitoringServlet>>() {
            }

    @Singleton
    @Provides
    MonitoringService createMonitoringService(ISessionManager sessionManager, PreferenceController preferenceController, LicenseService licenseService, HttpFactory httpFactory, ICayenneService cayenneService, ConfigurationFactory configFactory) {
        return configFactory
                .config(MonitoringServiceFactory.class, defaultConfigPrefix())
                .createMonitoringService(sessionManager, preferenceController, licenseService, httpFactory, cayenneService)
    }

    @Singleton
    @Provides
    MappedServlet<MonitoringServlet> createMonitoringServlet(MonitoringService monitoringService) {
        return new MappedServlet<>(new MonitoringServlet(monitoringService), Collections.singleton(MonitoringServlet.MONITORING_PATH), MonitoringServlet.class.getSimpleName())
    }

    @Singleton
    @Provides
    MonitoringServletContextHandlerExtender createMonitoringServletContextHandlerExtender(MonitoringService monitoringService) {
        return new MonitoringServletContextHandlerExtender(monitoringService)
    }

    @Override
    void configure(Binder binder) {
        AngelJettyModule.extend(binder)
        .addMappedServlet(MONITORING_SERVLET)
        .addContextHandlerExtender(MonitoringServletContextHandlerExtender)
    }
}
