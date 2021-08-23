/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.jetty;

import io.bootique.config.ConfigurationFactory;
import io.bootique.jetty.JettyModule;
import ish.jetty.server.AngelServerFactory;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;

public class AngelJettyModule extends JettyModule {

    public AngelJettyModule(String configPrefix) {
        super(configPrefix);
    }

    public AngelJettyModule() {
        super(StringUtils.substringBefore(JettyModule.class.getSimpleName().toLowerCase(Locale.ROOT),"module"));
    }

    @Override
    protected <T> T config(Class<T> type, ConfigurationFactory configurationFactory) {
        return (T) configurationFactory.config(AngelServerFactory.class, getConfigPrefix());
    }

}
