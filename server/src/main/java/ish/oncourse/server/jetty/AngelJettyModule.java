/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.jetty;

import com.google.inject.Provides;
import io.bootique.config.ConfigurationFactory;
import io.bootique.jetty.JettyModule;
import ish.oncourse.server.jetty.server.AngelServerFactory;
import ish.oncourse.server.license.LicenseService;
import ish.oncourse.server.modules.AngelHttpsConnectorFactory;

public class AngelJettyModule extends JettyModule {

    public AngelJettyModule(String configPrefix) {
        super(configPrefix);
    }

    public AngelJettyModule() {
        super("jetty");
    }

    @Override
    protected <T> T config(Class<T> type, ConfigurationFactory configurationFactory) {
        return (T) configurationFactory.config(AngelServerFactory.class, getConfigPrefix());
    }



}
