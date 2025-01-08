/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.modules

import io.bootique.di.Binder
import io.bootique.di.Provides
import javax.inject.Singleton
import io.bootique.di.TypeLiteral
import io.bootique.ConfigModule
import io.bootique.config.ConfigurationFactory
import io.bootique.jetty.MappedServlet
import io.bootique.jetty.AngelJettyModule
import ish.oncourse.server.monitoring.MonitoringService
import ish.oncourse.server.monitoring.MonitoringServiceFactory
import ish.oncourse.server.monitoring.MonitoringServletContextHandlerExtender
import ish.oncourse.server.monitoring.MonitoringStatisticService
import ish.oncourse.server.monitoring.servlet.MonitoringServlet

class MonitoringModule extends ConfigModule {

    private static final TypeLiteral<MappedServlet<MonitoringServlet>> MONITORING_SERVLET =
            new TypeLiteral<MappedServlet<MonitoringServlet>>() {
            }

    @Singleton
    @Provides
    MonitoringService createMonitoringService(ConfigurationFactory configFactory) {
        return configFactory
                .config(MonitoringServiceFactory.class, defaultConfigPrefix())
                .createMonitoringService()
    }

    @Singleton
    @Provides
    MappedServlet<MonitoringServlet> createMonitoringServlet(MonitoringService monitoringService, MonitoringStatisticService monitoringStatisticService) {
        return new MappedServlet<>(new MonitoringServlet(monitoringService, monitoringStatisticService), Collections.singleton(MonitoringServlet.MONITORING_PATH), MonitoringServlet.class.getSimpleName())
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

        binder.bind(MonitoringStatisticService.class).inSingletonScope()
    }
}
