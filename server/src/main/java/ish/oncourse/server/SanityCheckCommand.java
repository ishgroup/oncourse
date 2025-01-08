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
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import ish.oncourse.server.db.SanityCheckService;
import ish.oncourse.server.db.TransactionCheckService;
import ish.oncourse.server.license.LicenseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SanityCheckCommand extends CommandWithMetadata {

    private static final Logger logger = LogManager.getLogger();

    private Provider<SanityCheckService> sanityCheckServiceProvider;
    private final Provider<LicenseService> licenseServiceProvider;
    private Provider<TransactionCheckService> transactionCheckServiceProvider;

    @Inject
    public SanityCheckCommand(Provider<SanityCheckService> sanityCheckServiceProvider,
                              Provider<LicenseService> licenseServiceProvider,
                              Provider<TransactionCheckService> transactionCheckServiceProvider) {
        super(CommandMetadata.builder(SanityCheckCommand.class).description("Run sanity check.").build());

        this.sanityCheckServiceProvider = sanityCheckServiceProvider;
        this.licenseServiceProvider = licenseServiceProvider;
        this.transactionCheckServiceProvider = transactionCheckServiceProvider;
    }

    @Override
    public CommandOutcome run(Cli cli) {
        // Perform data sanity check
        try {
            logger.warn("Start data sanity check");
            sanityCheckServiceProvider.get().performCheck();
            LicenseService licenseService = licenseServiceProvider.get();
            
            logger.warn("Start transaction checking...");
            transactionCheckServiceProvider.get().performCheck();
        } catch (Exception e) {
            logger.catching(e);
            return CommandOutcome.failed(1, e);
        }
        return CommandOutcome.succeeded();
    }
}
