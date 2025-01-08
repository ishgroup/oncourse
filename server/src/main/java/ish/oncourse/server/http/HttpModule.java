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

import javax.inject.Provider;
import io.bootique.di.Provides;
import javax.inject.Singleton;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import org.eclipse.jetty.server.Server;


// Configure http settings section from oncCourse.yml
public class HttpModule extends ConfigModule {

    @Singleton
    @Provides
    HttpFactory createHttpervice(Provider<Server> serverProvider, ConfigurationFactory configFactory) {
        return configFactory
                .config(HttpFactory.class, defaultConfigPrefix()).serverProvider(serverProvider);
    }
}
