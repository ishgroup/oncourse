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

package ish.oncourse.server.http;

import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import io.bootique.di.Provides;
import org.eclipse.jetty.server.Server;

import javax.inject.Provider;
import javax.inject.Singleton;

public class HttpModule extends ConfigModule {

    @Singleton
    @Provides
    HttpFactory createHttpervice(Provider<Server> serverProvider, ConfigurationFactory configFactory) {
        return configFactory
                .config(HttpFactory.class, defaultConfigPrefix()).serverProvider(serverProvider);
    }
}
