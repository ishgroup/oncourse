/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.localization;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import ish.math.Country;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.cayenne.Preference;
import ish.oncourse.server.localization.country.LocalizationService;
import ish.oncourse.server.localization.logo.LogoService;

import java.util.Objects;
import java.util.Optional;

public class UploadLocalizationSettingsCommand extends CommandWithMetadata {
    
    private final Provider<LogoService> logoServiceProvider;
    private final Provider<LocalizationService> localizationServiceProvider;
    private final Provider<PreferenceController> preferenceControllerProvider;

    @Inject
    public UploadLocalizationSettingsCommand(Provider<LogoService> logoServiceProvider, Provider<LocalizationService> localizationServiceProvider, Provider<PreferenceController> preferenceControllerProvider)  {
        super(CommandMetadata.builder(UploadLocalizationSettingsCommand.class).description("Sync onCourse logo settings...").build());
        this.logoServiceProvider = logoServiceProvider;
        this.localizationServiceProvider = localizationServiceProvider;
        this.preferenceControllerProvider = preferenceControllerProvider;
    }

    @Override
    public CommandOutcome run(Cli cli) {
        LocalizationService localizationService = localizationServiceProvider.get();
        LogoService service = logoServiceProvider.get();
        PreferenceController preferenceController = preferenceControllerProvider.get();

        Optional<Country> country = localizationService.getCountry();
        if (country.isPresent()) {
            preferenceController.setCountry(country.get());
        } else {
            return CommandOutcome.failed(1, "Undefined 'localization.country' setting, check Oncourse.yml");
        }

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
