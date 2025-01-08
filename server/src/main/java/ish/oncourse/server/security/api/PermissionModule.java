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

package ish.oncourse.server.security.api;

import io.bootique.di.Injector;
import io.bootique.di.Provides;
import javax.inject.Singleton;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;

public class PermissionModule extends ConfigModule {

    private static final String PERMISSIONS_CONFIG_PREFIX = "permissions";

    @Singleton
    @Provides
    IPermissionService createPermissionService(Injector injector, ConfigurationFactory configFactory) {
        return configFactory
                .config(PermissionServiceFactory.class, PERMISSIONS_CONFIG_PREFIX)
                .createPermissionService(injector);

    }
}
