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
import io.bootique.BQRuntime;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import ish.math.context.MoneyContextUpdater;
import ish.oncourse.server.api.servlet.AngelSessionDataStoreFactory;
import ish.oncourse.server.bugsnag.BugsnagFactory;
import ish.oncourse.server.db.SchemaUpdateService;
import ish.oncourse.server.http.HttpFactory;
import ish.oncourse.server.integration.EventService;
import ish.oncourse.server.integration.PluginService;
import ish.oncourse.server.license.LicenseService;
import ish.oncourse.server.localization.UploadLocalizationSettingsCommand;
import ish.oncourse.server.messaging.MailDeliveryService;
import ish.oncourse.server.modules.AngelHttpsConnectorFactory;
import ish.oncourse.server.services.ISchedulerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.quartz.Scheduler;

public class AngelCommand extends CommandWithMetadata {

    final private Provider<AngelServerFactory> serverFactoryProvider;
    final private Provider<PreferenceController> prefControllerProvider;
    final private Provider<SchemaUpdateService>  schemaUpdateServiceProvider;
    final private Provider<ISchedulerService> schedulerServiceProvider;
    final private Provider<Scheduler> schedulerProvider;
    final private Provider<Server> serverProvider;
    final private Provider<CayenneService> cayenneServiceProvider;
    final private Provider<BugsnagFactory> bugsnagFactoryProvider;
    final private Provider<HttpFactory> httpFactoryProvider;
    final private Provider<LicenseService> licenseServiceProvider;
    final private Provider<PluginService> pluginServiceProvider;
    final private Provider<MailDeliveryService> mailDeliveryServiceProvider;
    final private Provider<EventService> eventServiceProvider;
    final private Provider<MoneyContextUpdater> moneyContextProvider;
    final private Provider<UploadLocalizationSettingsCommand> uploadLocalizationSettingsCommandProvider;

    private static final Logger logger =  LogManager.getLogger();

    @Inject
    public AngelCommand(Provider<Server> serverProvider,
                        Provider<AngelServerFactory> serverFactoryProvider,
                        Provider<PreferenceController> prefControllerProvider,
                        Provider<SchemaUpdateService> schemaUpdateServiceProvider,
                        Provider<ISchedulerService> schedulerServiceProvider,
                        Provider<Scheduler> schedulerProvider,
                        Provider<CayenneService> cayenneServiceProvider,
                        Provider<BugsnagFactory> bugsnagFactoryProvider,
                        Provider<HttpFactory> httpFactoryProvider,
                        Provider<LicenseService> licenseServiceProvider,
                        Provider<PluginService> pluginServiceProvider,
                        Provider<MailDeliveryService> mailDeliveryServiceProvider,
                        Provider<EventService> eventServiceProvider,
                        Provider<MoneyContextUpdater> moneyContextProvider,
                        Provider<UploadLocalizationSettingsCommand> uploadLocalizationSettingsCommandProvider) {
        super(CommandMetadata.builder(AngelCommand.class).description("Run onCourse server.").build());
        this.serverProvider = serverProvider;
        this.cayenneServiceProvider = cayenneServiceProvider;
        this.serverFactoryProvider = serverFactoryProvider;
        this.prefControllerProvider = prefControllerProvider;
        this.schemaUpdateServiceProvider = schemaUpdateServiceProvider;
        this.schedulerServiceProvider = schedulerServiceProvider;
        this.schedulerProvider = schedulerProvider;
        this.bugsnagFactoryProvider = bugsnagFactoryProvider;
        this.httpFactoryProvider = httpFactoryProvider;
        this.licenseServiceProvider = licenseServiceProvider;
        this.pluginServiceProvider = pluginServiceProvider;
        this.mailDeliveryServiceProvider = mailDeliveryServiceProvider;
        this.eventServiceProvider = eventServiceProvider;
        this.moneyContextProvider = moneyContextProvider;
        this.uploadLocalizationSettingsCommandProvider = uploadLocalizationSettingsCommandProvider;
    }

    @Override
    public CommandOutcome run(Cli cli) {
        var serverFactory = serverFactoryProvider.get();
        try {
            CayenneService cayenneService = cayenneServiceProvider.get();
            Server server = serverProvider.get();
            HttpFactory httpFactory = httpFactoryProvider.get();

            logger.warn("Apply Liquibase changes...");
            serverFactory.applyingDatabaseSchemaChanges(prefControllerProvider.get(),
                    schemaUpdateServiceProvider.get(),
                    licenseServiceProvider.get(),
                    cayenneService,mailDeliveryServiceProvider.get(),
                    httpFactory
            );

            logger.warn("Configure QRTZ...");
            serverFactory.configureQRTZScheduler(schedulerServiceProvider.get(), schedulerProvider.get(),
                    prefControllerProvider.get());


            logger.warn("Start Plugins...");
            serverFactory.startPlugins(pluginServiceProvider.get());

            logger.warn("Configure Localization settings from OnCourse.yml...");
            uploadLocalizationSettingsCommandProvider.get().run(cli);

            logger.warn("Sync Localization settings from OnCourse.yml with Database...");
            serverFactory.updateLocalizationSettingsInDB(prefControllerProvider.get(), moneyContextProvider.get());

            logger.warn("Server ready.");

            server.addBean(new AngelSessionDataStoreFactory(cayenneService, prefControllerProvider.get(), eventServiceProvider.get()));
            bugsnagFactoryProvider.get().init();
            httpFactory.init();
        } catch (Exception e) {
            return CommandOutcome.failed(1, e);
        }
        logger.error("Server ready for client connections");

        return CommandOutcome.succeeded();
    }
}
