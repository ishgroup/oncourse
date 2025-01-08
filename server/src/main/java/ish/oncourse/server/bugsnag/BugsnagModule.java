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

package ish.oncourse.server.bugsnag;

import io.bootique.di.Provides;
import javax.inject.Singleton;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import ish.oncourse.server.license.LicenseService;

public class BugsnagModule extends ConfigModule {

    @Singleton
    @Provides
    BugsnagFactory createBugsnag(ConfigurationFactory configFactory, LicenseService licenseService) {
        return configFactory
                .config(BugsnagFactory.class, defaultConfigPrefix()).collegeKey(licenseService.getCollege_key());
    }
}
