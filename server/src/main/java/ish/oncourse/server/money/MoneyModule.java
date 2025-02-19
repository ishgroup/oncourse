/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.money;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;
import ish.math.context.MoneyContext;
import ish.math.MoneyManager;
import ish.math.context.MoneyContextUpdater;

public class MoneyModule extends ConfigModule {

    @Singleton
    @Provides
    MoneyContextProvider createMoneyContextProvider(ConfigurationFactory configFactory) {
        MoneyContextProvider context = configFactory.config(MoneyContextProvider.class, defaultConfigPrefix());
        MoneyManager.updateSystemContext(context);
        return context;
    }

    @Singleton
    @Provides
    MoneyContext createMoneyContext(MoneyContextProvider contextProvider) {
        return contextProvider;
    }

    @Singleton
    @Provides
    MoneyContextUpdater createMoneyContextUpdater(MoneyContextProvider contextProvider) {

        return contextProvider;
    }
}
