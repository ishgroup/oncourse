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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Named;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import ish.oncourse.server.scripting.GroovyScriptService;
import ish.oncourse.server.upgrades.DataPopulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataPopulationCommand  extends CommandWithMetadata {

    private static final Logger logger = LogManager.getLogger();

    private String angelVersion;
    private Provider<CayenneService> cayenneServiceProvider;
    private Provider<GroovyScriptService> groovyScriptServiceProvider;

    @Inject
    public DataPopulationCommand(@Named(AngelModule.ANGEL_VERSION) String angelVersion,
                                 Provider<CayenneService> cayenneServiceProvider,
                                 Provider<GroovyScriptService> groovyScriptServiceProvider) {
        super(CommandMetadata.builder(DataPopulationCommand.class).description("Run data population.").build());
        this.angelVersion = angelVersion;
        this.cayenneServiceProvider = cayenneServiceProvider;
        this.groovyScriptServiceProvider = groovyScriptServiceProvider;

    }
    @Override
    public CommandOutcome run(Cli cli) {
        try {
            // Populate resources in the background
            var dataPopulation = new DataPopulation(angelVersion,
                    this.cayenneServiceProvider.get());
            dataPopulation.run();
            logger.warn("initialize script triggers...");
            groovyScriptServiceProvider.get().initTriggers();
        } catch (Exception e) { 
            logger.catching(e);
            return CommandOutcome.failed(1, "Error happened in Data Population Command", e);

        }
        return CommandOutcome.succeeded();
    }
}
