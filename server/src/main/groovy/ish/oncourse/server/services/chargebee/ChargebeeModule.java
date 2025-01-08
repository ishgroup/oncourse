/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee;

import io.bootique.di.Provides;
import javax.inject.Singleton;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.PreferenceController;

public class ChargebeeModule extends ConfigModule {

    @Singleton
    @Provides
    public ChargebeeService createChargebeeService(ConfigurationFactory configFactory, ICayenneService cayenneService,
                                                   PreferenceController preferenceController) {
        return configFactory.config(ChargebeeService.class, getConfigPrefix())
                .createChargebeeService(cayenneService, preferenceController);
    }
}
