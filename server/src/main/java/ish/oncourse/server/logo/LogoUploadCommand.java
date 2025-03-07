/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.logo;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.cayenne.Preference;

import java.util.Objects;

public class LogoUploadCommand extends CommandWithMetadata {
    
    private final Provider<LogoService> logoServiceProvider;
    private final Provider<PreferenceController> preferenceControllerProvider;

    @Inject
    public LogoUploadCommand(Provider<LogoService> logoServiceProvider, Provider<PreferenceController> preferenceControllerProvider)  {
        super(CommandMetadata.builder(LogoUploadCommand.class).description("Sync onCourse logo settings...").build());
        this.logoServiceProvider = logoServiceProvider;
        this.preferenceControllerProvider = preferenceControllerProvider;
    }

    @Override
    public CommandOutcome run(Cli cli) {
        LogoService service = logoServiceProvider.get();
        PreferenceController preferenceController = preferenceControllerProvider.get();

        service.getLogoProperties().forEach((preferenceKey, propertyValue) -> {
            Preference dbPreference = preferenceController.getPreference(preferenceKey, false);

            // delete
            if (Objects.isNull(propertyValue)) {
                if (!Objects.isNull(dbPreference)) {
                    dbPreference.getObjectContext().deleteObject(dbPreference);
                    dbPreference.getObjectContext().commitChanges();
                }
            } else {
                // create
                if (Objects.isNull(dbPreference)) {
                    preferenceController.setValueForKey(preferenceKey, propertyValue);
                } else {
                    // update
                    dbPreference.setValueString(propertyValue);
                    dbPreference.getObjectContext().commitChanges();
                }
            }
        });
        return CommandOutcome.succeeded();
    }
}
