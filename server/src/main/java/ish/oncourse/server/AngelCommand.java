/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import ish.oncourse.server.api.servlet.AngelSessionDataStoreFactory;
import ish.oncourse.server.bugsnag.BugsnagFactory;
import ish.oncourse.server.db.SchemaUpdateService;
import ish.oncourse.server.http.HttpFactory;
import ish.oncourse.server.integration.PluginService;
import ish.oncourse.server.jmx.RegisterMBean;
import ish.oncourse.server.license.LicenseService;
import ish.oncourse.server.services.ISchedulerService;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;


public class AngelCommand extends CommandWithMetadata {

    final private Provider<AngelServerFactory> serverFactoryProvider;
    final private Provider<PreferenceController> prefControllerProvider;
    final private Provider<SchemaUpdateService>  schemaUpdateServiceProvider;
    final private Provider<ISchedulerService> schedulerServiceProvider;
    final private Provider<Scheduler> schedulerProvider;
    final private Provider<RegisterMBean> registerMBeanProvider;
    final private Provider<Server> serverProvider;
    final private Provider<CayenneService> cayenneServiceProvider;
    final private Provider<BugsnagFactory> bugsnagFactoryProvider;
    final private Provider<HttpFactory> httpFactoryProvider;
    final private Provider<LicenseService> licenseServiceProvider;
    final private Provider<PluginService> pluginServiceProvider;

    private static final Logger logger = LoggerFactory.getLogger(AngelCommand.class);

    @Inject
    public AngelCommand(Provider<Server> serverProvider,
                        Provider<AngelServerFactory> serverFactoryProvider,
                        Provider<PreferenceController> prefControllerProvider,
                        Provider<SchemaUpdateService>  schemaUpdateServiceProvider,
                        Provider<ISchedulerService> schedulerServiceProvider,
                        Provider<Scheduler> schedulerProvider,
                        Provider<RegisterMBean> registerMBeanProvider,
                        Provider<CayenneService> cayenneServiceProvider,
                        Provider<BugsnagFactory> bugsnagFactoryProvider,
                        Provider<HttpFactory> httpFactoryProvider,
                        Provider<LicenseService> licenseServiceProvider,
                        Provider<PluginService> pluginServiceProvider) {
        super(CommandMetadata.builder(AngelCommand.class).description("Run onCourse server.").build());
        this.serverProvider = serverProvider;
        this.cayenneServiceProvider = cayenneServiceProvider;
        this.serverFactoryProvider = serverFactoryProvider;
        this.prefControllerProvider = prefControllerProvider;
        this.schemaUpdateServiceProvider = schemaUpdateServiceProvider;
        this.schedulerServiceProvider = schedulerServiceProvider;
        this.schedulerProvider = schedulerProvider;
        this.registerMBeanProvider = registerMBeanProvider;
        this.bugsnagFactoryProvider = bugsnagFactoryProvider;
        this.httpFactoryProvider = httpFactoryProvider;
        this.licenseServiceProvider = licenseServiceProvider;
        this.pluginServiceProvider = pluginServiceProvider;
    }

    @Override
    public CommandOutcome run(Cli cli) {

        var serverFactory = serverFactoryProvider.get();
        try {
            CayenneService cayenneService = cayenneServiceProvider.get();
            serverFactory.start(prefControllerProvider.get(),
                    schemaUpdateServiceProvider.get(),
                    schedulerServiceProvider.get(),
                    schedulerProvider.get(),
                    registerMBeanProvider.get(),
                    licenseServiceProvider.get(),
                    cayenneService,
                    pluginServiceProvider.get()
                    );

            Server server = serverProvider.get();
            server.addBean(new AngelSessionDataStoreFactory(cayenneService, prefControllerProvider.get()));
            server.addBean(new MBeanContainer(ManagementFactory.getPlatformMBeanServer()));
            bugsnagFactoryProvider.get().init();
            httpFactoryProvider.get().init();
        } catch (Exception e) {
            return CommandOutcome.failed(1, e);
        }
        logger.error("Server ready for client connections");

        return CommandOutcome.succeeded();
    }
}
